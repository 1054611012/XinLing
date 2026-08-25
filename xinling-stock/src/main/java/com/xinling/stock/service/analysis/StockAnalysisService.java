package com.xinling.stock.service.analysis;

import com.xinling.stock.domain.entity.*;
import com.xinling.stock.domain.vo.AnalysisResultVO;
import com.xinling.stock.domain.vo.ScoreDimensionVO;
import com.xinling.stock.mapper.*;
import com.xinling.stock.service.indicator.TechnicalIndicatorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * AI 股票分析核心服务
 *
 * 协调所有维度：数据采集 → 技术指标计算 → 规则评分 → AI分析 → 报告生成
 */
@Service
public class StockAnalysisService {

    private static final Logger log = LoggerFactory.getLogger(StockAnalysisService.class);

    private final StockMapper stockMapper;
    private final SectorMapper sectorMapper;
    private final SectorPerformanceMapper sectorPerformanceMapper;
    private final KlineDailyMapper klineDailyMapper;
    private final TechnicalIndicatorMapper technicalIndicatorMapper;
    private final FundFlowMapper fundFlowMapper;
    private final MarketSentimentMapper marketSentimentMapper;
    private final LinkageAnalysisMapper linkageAnalysisMapper;
    private final AnalysisReportMapper analysisReportMapper;
    private final SectorFuturesMappingMapper sectorFuturesMappingMapper;

    private final TechnicalIndicatorService technicalIndicatorService;
    private final ScoringEngineService scoringEngineService;

    public StockAnalysisService(StockMapper stockMapper,
                                SectorMapper sectorMapper,
                                SectorPerformanceMapper sectorPerformanceMapper,
                                KlineDailyMapper klineDailyMapper,
                                TechnicalIndicatorMapper technicalIndicatorMapper,
                                FundFlowMapper fundFlowMapper,
                                MarketSentimentMapper marketSentimentMapper,
                                LinkageAnalysisMapper linkageAnalysisMapper,
                                AnalysisReportMapper analysisReportMapper,
                                SectorFuturesMappingMapper sectorFuturesMappingMapper,
                                TechnicalIndicatorService technicalIndicatorService,
                                ScoringEngineService scoringEngineService) {
        this.stockMapper = stockMapper;
        this.sectorMapper = sectorMapper;
        this.sectorPerformanceMapper = sectorPerformanceMapper;
        this.klineDailyMapper = klineDailyMapper;
        this.technicalIndicatorMapper = technicalIndicatorMapper;
        this.fundFlowMapper = fundFlowMapper;
        this.marketSentimentMapper = marketSentimentMapper;
        this.linkageAnalysisMapper = linkageAnalysisMapper;
        this.analysisReportMapper = analysisReportMapper;
        this.sectorFuturesMappingMapper = sectorFuturesMappingMapper;
        this.technicalIndicatorService = technicalIndicatorService;
        this.scoringEngineService = scoringEngineService;
    }

    /**
     * 对个股进行全维度分析
     */
    public AnalysisResultVO analyzeStock(String stockCode, String analysisType, boolean deepAnalysis) {
        log.info("开始分析个股: {}, 类型: {}", stockCode, analysisType);

        // 1. 获取基础信息
        Stock stock = stockMapper.selectByCode(stockCode);
        if (stock == null) {
            AnalysisResultVO error = new AnalysisResultVO();
            error.setComprehensiveScore(BigDecimal.ZERO);
            error.setAiSummary("未找到股票: " + stockCode);
            error.setRecommendation("watch");
            return error;
        }

        // 2. 获取K线数据
        Date endDate = new Date();
        Date startDate = getStartDate(analysisType);
        List<KlineDaily> klineList = klineDailyMapper.selectBySymbolAndDateRange(
                "stock", stockCode, startDate, endDate);

        if (klineList.isEmpty()) {
            AnalysisResultVO error = new AnalysisResultVO();
            error.setComprehensiveScore(BigDecimal.ZERO);
            error.setAiSummary("股票 " + stock.getStockName() + " 暂无K线数据");
            error.setRecommendation("watch");
            error.setSectorAnalysis(stock.getIndustry());
            return error;
        }

        // 3. 计算技术指标
        TechnicalIndicator indicator = technicalIndicatorService.calculateAndSave(
                "stock", stockCode, klineList.get(klineList.size() - 1).getTradeDate());

        // 4. 获取板块数据
        SectorPerformance sectorPerf = null;
        if (stock.getSectorId() != null) {
            sectorPerf = sectorPerformanceMapper.selectBySectorAndDate(
                    stock.getSectorId(), getLatestTradeDate(klineList));
        }

        // 5. 获取资金流向
        FundFlow fundFlow = fundFlowMapper.selectByStockAndDate(
                stockCode, getLatestTradeDate(klineList));

        // 6. 获取市场情绪
        MarketSentiment sentiment = marketSentimentMapper.selectByDate(
                getLatestTradeDate(klineList));

        // 7. 执行规则评分
        ScoringEngineService.ScoringContext ctx = new ScoringEngineService.ScoringContext();
        ctx.setIndicator(indicator);
        ctx.setSectorPerformance(sectorPerf);
        ctx.setFundFlow(fundFlow);
        ctx.setMarketSentiment(sentiment);
        ctx.setKlineList(klineList);

        ScoringEngineService.ScoringResult scoringResult = scoringEngineService.executeScoring(ctx);

        // 8. 构建分析报告
        AnalysisResultVO result = buildAnalysisResult(scoringResult, indicator, klineList,
                sectorPerf, fundFlow, sentiment, stock, null);

        // 9. 持久化
        saveAnalysisReport(result, "stock", stockCode, stock.getStockName(), analysisType);

        return result;
    }

    /**
     * 对板块进行全维度分析
     */
    public AnalysisResultVO analyzeSector(String sectorCode, String analysisType, boolean deepAnalysis) {
        log.info("开始分析板块: {}, 类型: {}", sectorCode, analysisType);

        // 1. 获取板块信息
        Sector sector = sectorMapper.selectByCode(sectorCode);
        if (sector == null) {
            AnalysisResultVO error = new AnalysisResultVO();
            error.setComprehensiveScore(BigDecimal.ZERO);
            error.setAiSummary("未找到板块: " + sectorCode);
            error.setRecommendation("watch");
            return error;
        }

        // 2. 获取板块指数K线
        Date endDate = new Date();
        Date startDate = getStartDate(analysisType);
        List<KlineDaily> klineList = klineDailyMapper.selectBySymbolAndDateRange(
                "sector", sectorCode, startDate, endDate);

        if (klineList.isEmpty()) {
            AnalysisResultVO error = new AnalysisResultVO();
            error.setComprehensiveScore(BigDecimal.ZERO);
            error.setScoreLevel("★☆☆☆☆");
            error.setAiSummary("板块 " + sector.getSectorName() + " 暂无K线数据");
            error.setRecommendation("watch");
            error.setRiskLevel("mid");
            return error;
        }

        // 3. 计算技术指标
        TechnicalIndicator indicator = null;
        indicator = technicalIndicatorService.calculateAndSave(
                "sector", sectorCode, getLatestTradeDate(klineList));

        // 4. 获取板块表现
        SectorPerformance sectorPerf = sectorPerformanceMapper.selectBySectorAndDate(
                sector.getId(), getLatestTradeDate(klineList));

        // 5. 获取期货联动分析
        LinkageAnalysis linkage = linkageAnalysisMapper.selectBySectorAndDate(
                sector.getId(), getLatestTradeDate(klineList));

        // 6. 获取市场情绪
        MarketSentiment sentiment = marketSentimentMapper.selectByDate(
                getLatestTradeDate(klineList));

        // 7. 执行评分
        ScoringEngineService.ScoringContext ctx = new ScoringEngineService.ScoringContext();
        ctx.setIndicator(indicator);
        ctx.setSectorPerformance(sectorPerf);
        ctx.setLinkageAnalysis(linkage);
        ctx.setMarketSentiment(sentiment);
        ctx.setKlineList(klineList);

        ScoringEngineService.ScoringResult scoringResult = scoringEngineService.executeScoring(ctx);

        // 8. 构建报告
        AnalysisResultVO result = buildAnalysisResult(scoringResult, indicator, klineList,
                sectorPerf, null, sentiment, null, sector);

        // 9. 添加期货联动信息
        if (linkage != null) {
            result.setFuturesLinkage(linkage.getAnalysisConclusion());
        }

        // 10. 保存
        saveAnalysisReport(result, "sector", sectorCode, sector.getSectorName(), analysisType);

        return result;
    }

    // ========================================================================
    //  报告构建
    // ========================================================================

    private AnalysisResultVO buildAnalysisResult(
            ScoringEngineService.ScoringResult scoringResult,
            TechnicalIndicator indicator,
            List<KlineDaily> klineList,
            SectorPerformance sectorPerf,
            FundFlow fundFlow,
            MarketSentiment sentiment,
            Stock stock,
            Sector sector) {

        AnalysisResultVO result = new AnalysisResultVO();

        BigDecimal comprehensiveScore = scoringResult.getComprehensiveScore();
        result.setComprehensiveScore(comprehensiveScore);
        result.setScoreLevel(convertScoreToStar(comprehensiveScore));
        result.setDimensions(scoringResult.getDimensions());

        // 生成各维度分析文本
        result.setMaAnalysis(generateMaAnalysis(indicator));
        result.setMacdAnalysis(generateMacdAnalysis(indicator));
        result.setKdjAnalysis(generateKdjAnalysis(indicator));
        result.setVolumeAnalysis(generateVolumeAnalysis(indicator, klineList));
        result.setSectorAnalysis(generateSectorAnalysis(sectorPerf, sector));
        result.setFundAnalysis(generateFundAnalysis(fundFlow));
        result.setMarketSentiment(generateSentimentAnalysis(sentiment));

        // 生成操作建议
        generateTradingAdvice(result, comprehensiveScore, indicator, klineList);

        // AI总结
        result.setAiSummary(generateSummary(result, stock, sector));
        result.setRiskAnalysis(generateRiskAnalysis(result, indicator, klineList));

        return result;
    }

    // ========================================================================
    //  各维度文本生成
    // ========================================================================

    private String generateMaAnalysis(TechnicalIndicator ti) {
        if (ti == null) return "暂无均线数据";
        StringBuilder sb = new StringBuilder();

        // 检查多头/空头
        boolean bullish = ti.getMa5() != null && ti.getMa10() != null
                && ti.getMa5().compareTo(ti.getMa10()) > 0;
        boolean bearish = ti.getMa5() != null && ti.getMa10() != null
                && ti.getMa5().compareTo(ti.getMa10()) < 0;

        sb.append("MA5=").append(formatPrice(ti.getMa5()))
          .append(" MA10=").append(formatPrice(ti.getMa10()))
          .append(" MA20=").append(formatPrice(ti.getMa20()))
          .append(" MA60=").append(formatPrice(ti.getMa60()));

        if (bullish && ti.getMa10().compareTo(ti.getMa20()) > 0) {
            sb.append("。均线呈多头排列，中期趋势偏强");
        } else if (bearish && ti.getMa10().compareTo(ti.getMa20()) < 0) {
            sb.append("。均线呈空头排列，中期趋势偏弱");
        } else {
            sb.append("。均线交织，趋势尚不明确");
        }

        return sb.toString();
    }

    private String generateMacdAnalysis(TechnicalIndicator ti) {
        if (ti == null || ti.getMacdDif() == null) return "暂无MACD数据";
        StringBuilder sb = new StringBuilder();

        boolean goldenCross = ti.getMacdDif().compareTo(ti.getMacdDea()) > 0;
        boolean redBar = ti.getMacdBar() != null && ti.getMacdBar().doubleValue() > 0;

        sb.append("DIF=").append(formatPrice(ti.getMacdDif()))
          .append(" DEA=").append(formatPrice(ti.getMacdDea()))
          .append(" BAR=").append(formatPrice(ti.getMacdBar()));

        if (goldenCross) {
            sb.append("。MACD处于金叉状态");
            if (redBar) sb.append("，红柱放大，动能增强");
        } else {
            sb.append("。MACD处于死叉状态");
            if (!redBar) sb.append("，绿柱放大，动能减弱");
        }

        if (ti.getMacdDif().doubleValue() > 0) {
            sb.append("，位于零轴上方");
        } else {
            sb.append("，位于零轴下方");
        }

        return sb.toString();
    }

    private String generateKdjAnalysis(TechnicalIndicator ti) {
        if (ti == null || ti.getKdjJ() == null) return "暂无KDJ数据";
        StringBuilder sb = new StringBuilder();

        double j = ti.getKdjJ().doubleValue();
        sb.append("K=").append(formatPrice(ti.getKdjK()))
          .append(" D=").append(formatPrice(ti.getKdjD()))
          .append(" J=").append(formatPrice(ti.getKdjJ()));

        if (j < 20) sb.append("。J值进入超卖区(J<20)，存在反弹预期");
        else if (j > 100) sb.append("。J值进入超买区(J>100)，注意回调风险");
        else if (j < 50) sb.append("。J值处于中低位，仍有上行空间");
        else sb.append("。J值处于中高位");

        return sb.toString();
    }

    private String generateVolumeAnalysis(TechnicalIndicator ti, List<KlineDaily> klineList) {
        if (klineList == null || klineList.size() < 2) return "暂无成交量数据";
        StringBuilder sb = new StringBuilder();

        KlineDaily latest = klineList.get(klineList.size() - 1);
        KlineDaily prev = klineList.get(klineList.size() - 2);

        boolean isUp = latest.getChangePct() != null && latest.getChangePct().doubleValue() > 0;
        double volRatio = ti != null && ti.getVolumeRatio() != null
                ? ti.getVolumeRatio().doubleValue() : 1.0;

        sb.append("量比=").append(String.format("%.2f", volRatio))
          .append(" 成交额=").append(formatAmount(latest.getAmount()));

        if (isUp && volRatio > 1.2) sb.append("。放量上涨，资金认可度高");
        else if (isUp && volRatio < 0.8) sb.append("。缩量上涨，上涨动力略显不足");
        else if (!isUp && volRatio > 1.2) sb.append("。放量下跌，抛压较大");
        else if (!isUp && volRatio < 0.8) sb.append("。缩量下跌，属于正常调整范畴");

        return sb.toString();
    }

    private String generateSectorAnalysis(SectorPerformance sp, Sector sector) {
        if (sp == null) return "暂无板块数据";
        StringBuilder sb = new StringBuilder();

        if (sector != null) {
            sb.append("所属板块: ").append(sector.getSectorName()).append("。");
        }

        sb.append("涨幅=").append(formatPct(sp.getChangePct()))
          .append(" 成交额=").append(formatAmount(sp.getAmount()))
          .append(" 上涨=").append(sp.getAdvanceCount()).append("家")
          .append(" 下跌=").append(sp.getDeclineCount()).append("家");

        if (sp.getLimitUpCount() != null && sp.getLimitUpCount() > 0) {
            sb.append(" 涨停=").append(sp.getLimitUpCount()).append("家");
        }

        if (sp.getLeaderStock() != null) {
            sb.append(" 领涨股=").append(sp.getLeaderStock());
        }

        if (sp.getRank1d() != null && sp.getRank1d() <= 10) {
            sb.append("。板块排名第").append(sp.getRank1d()).append("，表现强势");
        } else if (sp.getRank1d() != null && sp.getRank1d() > 200) {
            sb.append("。板块排名靠后，表现较弱");
        }

        return sb.toString();
    }

    private String generateFundAnalysis(FundFlow ff) {
        if (ff == null) return "暂无资金流向数据";
        StringBuilder sb = new StringBuilder();

        double inflow = ff.getMainNetInflow() != null ? ff.getMainNetInflow().doubleValue() : 0;
        sb.append("主力净流入=").append(formatWanYuan(inflow));

        if (inflow > 0) {
            sb.append("，主力资金呈净流入状态");
            if (inflow > 50000) sb.append("，流入规模较大");
        } else {
            sb.append("，主力资金呈净流出状态");
            if (inflow < -50000) sb.append("，流出规模较大");
        }

        return sb.toString();
    }

    private String generateSentimentAnalysis(MarketSentiment ms) {
        if (ms == null) return "暂无市场情绪数据";
        StringBuilder sb = new StringBuilder();

        sb.append("涨停=").append(ms.getLimitUpCount()).append("家")
          .append(" 跌停=").append(ms.getLimitDownCount()).append("家")
          .append(" 上涨=").append(ms.getTotalUp()).append("家")
          .append(" 下跌=").append(ms.getTotalDown()).append("家");

        if (ms.get封板率() != null) {
            sb.append(" 封板率=").append(ms.get封板率()).append("%");
        }

        int netUp = (ms.getTotalUp() != null ? ms.getTotalUp() : 0)
                  - (ms.getTotalDown() != null ? ms.getTotalDown() : 0);
        if (netUp > 1000) sb.append("。市场情绪亢奋，赚钱效应强");
        else if (netUp > 0) sb.append("。市场情绪偏暖");
        else if (netUp > -1000) sb.append("。市场情绪偏冷");
        else sb.append("。市场情绪低迷，风险偏好下降");

        return sb.toString();
    }

    // ========================================================================
    //  交易建议生成
    // ========================================================================

    private void generateTradingAdvice(AnalysisResultVO result, BigDecimal score,
                                        TechnicalIndicator ti, List<KlineDaily> klineList) {
        double s = score.doubleValue();

        // 买入概率
        result.setBuyProbability(score);

        // 风险等级
        if (s >= 70) result.setRiskLevel("low");
        else if (s >= 50) result.setRiskLevel("mid");
        else result.setRiskLevel("high");

        // 建议仓位
        if (s >= 80) result.setSuggestedPosition(BigDecimal.valueOf(60));
        else if (s >= 60) result.setSuggestedPosition(BigDecimal.valueOf(40));
        else if (s >= 40) result.setSuggestedPosition(BigDecimal.valueOf(20));
        else result.setSuggestedPosition(BigDecimal.valueOf(5));

        // 操作建议
        if (s >= 80) result.setRecommendation("buy");
        else if (s >= 65) result.setRecommendation("hold");
        else if (s >= 45) result.setRecommendation("watch");
        else result.setRecommendation("sell");

        // 止损止盈
        if (ti != null && ti.getMa20() != null && klineList != null && !klineList.isEmpty()) {
            KlineDaily latest = klineList.get(klineList.size() - 1);
            double currentPrice = latest.getClosePrice().doubleValue();
            double ma20 = ti.getMa20().doubleValue();

            result.setStopLoss(BigDecimal.valueOf(Math.round(ma20 * 0.95 * 100) / 100.0));
            result.setTakeProfit(BigDecimal.valueOf(Math.round(currentPrice * 1.15 * 100) / 100.0));

            // 买入区间
            double lower = Math.min(currentPrice, ma20) * 0.98;
            double upper = currentPrice * 1.02;
            result.setBuyZone(String.format("%.2f - %.2f", lower, upper));
        }
    }

    // ========================================================================
    //  AI总结生成
    // ========================================================================

    private String generateSummary(AnalysisResultVO result, Stock stock, Sector sector) {
        StringBuilder sb = new StringBuilder();

        if (stock != null) {
            sb.append(stock.getStockName()).append("(").append(stock.getStockCode()).append(")");
        } else if (sector != null) {
            sb.append(sector.getSectorName()).append("板块");
        }

        sb.append("综合评分").append(result.getComprehensiveScore()).append("分（")
          .append(result.getScoreLevel()).append("），");

        if ("buy".equals(result.getRecommendation())) {
            sb.append("各维度表现良好，具备较好的投资价值。建议在回调至支撑位时分批建仓，");
            sb.append("仓位控制在").append(result.getSuggestedPosition()).append("%以内。");
        } else if ("hold".equals(result.getRecommendation())) {
            sb.append("整体趋势尚可，但部分维度存在不确定性。已持仓者可继续持有，");
            sb.append("新开仓建议等待更明确的信号。");
        } else if ("watch".equals(result.getRecommendation())) {
            sb.append("多个维度信号偏弱，当前不具备明确的买入条件。");
            sb.append("建议观望为主，等待趋势明朗。");
        } else {
            sb.append("整体评分偏低，风险较高。建议减仓或回避。");
        }

        return sb.toString();
    }

    private String generateRiskAnalysis(AnalysisResultVO result,
                                         TechnicalIndicator ti, List<KlineDaily> klineList) {
        List<String> risks = new ArrayList<>();

        if ("high".equals(result.getRiskLevel())) {
            risks.add("综合评分偏低，系统性风险较高");
        }

        if (ti != null && ti.getKdjJ() != null && ti.getKdjJ().doubleValue() > 100) {
            risks.add("KDJ超买，短期回调风险增加");
        }

        if (ti != null && ti.getMa20() != null && klineList != null && !klineList.isEmpty()) {
            KlineDaily latest = klineList.get(klineList.size() - 1);
            if (latest.getClosePrice().compareTo(ti.getMa20()) < 0) {
                risks.add("价格已跌破MA20，中期趋势转弱");
            }
        }

        if (result.getComprehensiveScore().doubleValue() < 40) {
            risks.add("不建议在当前条件下操作");
        }

        if (risks.isEmpty()) {
            risks.add("暂无显著风险信号，但需持续关注市场变化");
        }

        return risks.stream().map(r -> "- " + r).collect(Collectors.joining("\n"));
    }

    // ========================================================================
    //  持久化
    // ========================================================================

    private void saveAnalysisReport(AnalysisResultVO result, String symbolType,
                                     String symbolCode, String symbolName, String analysisType) {
        try {
            AnalysisReport report = new AnalysisReport();
            report.setSymbolType(symbolType);
            report.setSymbolCode(symbolCode);
            report.setSymbolName(symbolName);
            report.setAnalysisDate(new java.sql.Date(System.currentTimeMillis()));
            report.setAnalysisType(analysisType);
            report.setComprehensiveScore(result.getComprehensiveScore());
            report.setComprehensiveScore(result.getComprehensiveScore());
            report.setTrendAnalysis(result.getTrendAnalysis());
            report.setMaAnalysis(result.getMaAnalysis());
            report.setMacdAnalysis(result.getMacdAnalysis());
            report.setKdjAnalysis(result.getKdjAnalysis());
            report.setVolumeAnalysis(result.getVolumeAnalysis());
            report.setSectorAnalysis(result.getSectorAnalysis());
            report.setFuturesLinkage(result.getFuturesLinkage());
            report.setFundAnalysis(result.getFundAnalysis());
            report.setNewsSentiment(result.getNewsSentiment());
            report.setMarketSentiment(result.getMarketSentiment());
            report.setAiSummary(result.getAiSummary());
            report.setRiskAnalysis(result.getRiskAnalysis());
            report.setBuyProbability(result.getBuyProbability());
            report.setRiskLevel(result.getRiskLevel());
            report.setSuggestedPosition(result.getSuggestedPosition());
            report.setBuyZone(result.getBuyZone());
            report.setStopLoss(result.getStopLoss());
            report.setTakeProfit(result.getTakeProfit());
            report.setRecommendation(result.getRecommendation());
            report.setAnalysisStatus(1);

            // 构建评分详情JSON
            if (result.getDimensions() != null) {
                StringBuilder json = new StringBuilder("{");
                for (ScoreDimensionVO dim : result.getDimensions()) {
                    json.append("\"").append(dim.getDimensionCode()).append("\":")
                        .append(dim.getScore()).append(",");
                }
                if (json.length() > 1) json.deleteCharAt(json.length() - 1);
                json.append("}");
                report.setScoreDetail(json.toString());
            }

            analysisReportMapper.insert(report);
        } catch (Exception e) {
            log.error("保存分析报告失败", e);
        }
    }

    // ========================================================================
    //  工具方法
    // ========================================================================

    private String convertScoreToStar(BigDecimal score) {
        int s = score.intValue();
        if (s >= 90) return "★★★★★";
        if (s >= 75) return "★★★★☆";
        if (s >= 60) return "★★★☆☆";
        if (s >= 40) return "★★☆☆☆";
        return "★☆☆☆☆";
    }

    private Date getLatestTradeDate(List<KlineDaily> klineList) {
        if (klineList == null || klineList.isEmpty()) {
            return new Date();
        }
        return klineList.get(klineList.size() - 1).getTradeDate();
    }

    private Date getStartDate(String analysisType) {
        Calendar cal = Calendar.getInstance();
        if ("weekly".equals(analysisType)) {
            cal.add(Calendar.DAY_OF_YEAR, -400);
        } else if ("monthly".equals(analysisType)) {
            cal.add(Calendar.DAY_OF_YEAR, -800);
        } else {
            cal.add(Calendar.DAY_OF_YEAR, -365);
        }
        return cal.getTime();
    }

    private String formatPrice(BigDecimal price) {
        return price != null ? price.setScale(2, RoundingMode.HALF_UP).toString() : "N/A";
    }

    private String formatPct(BigDecimal pct) {
        return pct != null ? String.format("%+.2f%%", pct.doubleValue()) : "N/A";
    }

    private String formatAmount(BigDecimal amount) {
        if (amount == null) return "N/A";
        double v = amount.doubleValue();
        if (v >= 1e8) return String.format("%.2f亿", v / 1e8);
        if (v >= 1e4) return String.format("%.2f万", v / 1e4);
        return amount.toString();
    }

    private String formatWanYuan(double value) {
        if (value >= 10000) return String.format("%+.2f亿", value / 10000);
        return String.format("%+.0f万", value);
    }
}
