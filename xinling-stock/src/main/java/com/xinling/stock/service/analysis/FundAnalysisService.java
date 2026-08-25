package com.xinling.stock.service.analysis;

import com.xinling.stock.domain.entity.*;
import com.xinling.stock.domain.vo.AnalysisResultVO;
import com.xinling.stock.domain.vo.ScoreDimensionVO;
import com.xinling.stock.mapper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * AI 基金分析服务
 *
 * 基于7大维度对基金进行评分：
 * 阶段收益 / 风控能力 / 基金经理 / 持仓质量 / 规模适宜 / 费率水平 / 外部评级
 */
@Service
public class FundAnalysisService {

    private static final Logger log = LoggerFactory.getLogger(FundAnalysisService.class);

    private final FundMapper fundMapper;
    private final FundNavMapper fundNavMapper;
    private final FundHoldingMapper fundHoldingMapper;
    private final FundPerformanceMapper fundPerformanceMapper;
    private final ConfigScoreWeightMapper configScoreWeightMapper;
    private final StockAnalysisService stockAnalysisService;

    public FundAnalysisService(FundMapper fundMapper,
                               FundNavMapper fundNavMapper,
                               FundHoldingMapper fundHoldingMapper,
                               FundPerformanceMapper fundPerformanceMapper,
                               ConfigScoreWeightMapper configScoreWeightMapper,
                               StockAnalysisService stockAnalysisService) {
        this.fundMapper = fundMapper;
        this.fundNavMapper = fundNavMapper;
        this.fundHoldingMapper = fundHoldingMapper;
        this.fundPerformanceMapper = fundPerformanceMapper;
        this.configScoreWeightMapper = configScoreWeightMapper;
        this.stockAnalysisService = stockAnalysisService;
    }

    /**
     * 基金全维度分析
     */
    public AnalysisResultVO analyze(String fundCode, boolean deepAnalysis) {
        log.info("开始分析基金: {}", fundCode);

        // 1. 获取基金信息
        Fund fund = fundMapper.selectByCode(fundCode);
        if (fund == null) {
            AnalysisResultVO error = new AnalysisResultVO();
            error.setComprehensiveScore(BigDecimal.ZERO);
            error.setAiSummary("未找到基金: " + fundCode);
            error.setRecommendation("watch");
            error.setRiskLevel("mid");
            return error;
        }

        // 2. 获取净值数据
        List<FundNav> navList = fundNavMapper.selectLatestByFund(fundCode, 500);

        // 3. 获取阶段表现数据
        FundPerformance perf = fundPerformanceMapper.selectLatestByFund(fundCode);

        // 4. 获取持仓数据
        List<FundHolding> holdings = fundHoldingMapper.selectLatestByFund(fundCode);

        // 5. 获取关联个股分析（前3重仓股）
        List<AnalysisResultVO> stockAnalyses = new ArrayList<>();
        if (holdings != null) {
            holdings.stream()
                    .filter(h -> h.getStockCode() != null)
                    .limit(3)
                    .forEach(h -> {
                        try {
                            AnalysisResultVO sa = stockAnalysisService.analyzeStock(
                                    h.getStockCode(), "daily", false);
                            sa.setSectorAnalysis(h.getStockName());
                            stockAnalyses.add(sa);
                        } catch (Exception e) {
                            log.warn("获取{}持仓股分析失败: {}", fundCode, h.getStockCode());
                        }
                    });
        }

        // 6. 7维度评分
        List<ScoreDimensionVO> dimensions = new ArrayList<>();
        BigDecimal totalScore = BigDecimal.ZERO;
        BigDecimal totalWeight = BigDecimal.ZERO;

        // 6.1 阶段收益评分
        ScoreDimensionVO returnScore = scoreFundReturn(perf);
        dimensions.add(returnScore);
        totalScore = totalScore.add(returnScore.getWeightedScore());
        totalWeight = totalWeight.add(returnScore.getWeight());

        // 6.2 风控能力评分
        ScoreDimensionVO riskScore = scoreFundRisk(perf);
        dimensions.add(riskScore);
        totalScore = totalScore.add(riskScore.getWeightedScore());
        totalWeight = totalWeight.add(riskScore.getWeight());

        // 6.3 持仓质量评分
        ScoreDimensionVO holdingScore = scoreHoldings(holdings, stockAnalyses);
        dimensions.add(holdingScore);
        totalScore = totalScore.add(holdingScore.getWeightedScore());
        totalWeight = totalWeight.add(holdingScore.getWeight());

        // 6.4 规模适宜评分
        ScoreDimensionVO scaleScore = scoreFundScale(fund);
        dimensions.add(scaleScore);
        totalScore = totalScore.add(scaleScore.getWeightedScore());
        totalWeight = totalWeight.add(scaleScore.getWeight());

        // 6.5 费率评分
        ScoreDimensionVO feeScore = scoreFundFee(fund);
        dimensions.add(feeScore);
        totalScore = totalScore.add(feeScore.getWeightedScore());
        totalWeight = totalWeight.add(feeScore.getWeight());

        // 6.6 外部评级评分
        ScoreDimensionVO ratingScore = scoreFundRating(perf);
        dimensions.add(ratingScore);
        totalScore = totalScore.add(ratingScore.getWeightedScore());
        totalWeight = totalWeight.add(ratingScore.getWeight());

        // 6.7 基金经理评分
        ScoreDimensionVO managerScore = scoreFundManager(fund);
        dimensions.add(managerScore);
        totalScore = totalScore.add(managerScore.getWeightedScore());
        totalWeight = totalWeight.add(managerScore.getWeight());

        // 7. 综合评分
        BigDecimal comprehensiveScore = totalWeight.compareTo(BigDecimal.ZERO) > 0
                ? totalScore.divide(totalWeight, 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        // 8. 生成报告
        AnalysisResultVO result = new AnalysisResultVO();
        result.setComprehensiveScore(comprehensiveScore);
        result.setScoreLevel(convertScoreToStar(comprehensiveScore));
        result.setDimensions(dimensions);

        // 生成各维度的分析文本
        result.setTrendAnalysis(generateReturnAnalysis(perf));
        result.setRiskAnalysis(generateRiskAnalysis(perf));
        result.setSectorAnalysis(generateHoldingAnalysis(holdings));
        result.setFundAnalysis(generateFeeAnalysis(fund));

        // 操作建议
        result.setRecommendation(generateRecommendation(comprehensiveScore, fund));
        result.setRiskLevel(comprehensiveScore.doubleValue() >= 70 ? "low"
                : comprehensiveScore.doubleValue() >= 50 ? "mid" : "high");
        result.setBuyProbability(comprehensiveScore);
        result.setSuggestedPosition(comprehensiveScore.doubleValue() >= 70 ? BigDecimal.valueOf(30)
                : comprehensiveScore.doubleValue() >= 50 ? BigDecimal.valueOf(20)
                : BigDecimal.valueOf(10));

        // AI总结
        result.setAiSummary(generateSummary(fund, comprehensiveScore, holdings, perf));

        return result;
    }

    // ========================================================================
    //  维度评分
    // ========================================================================

    private ScoreDimensionVO scoreFundReturn(FundPerformance perf) {
        BigDecimal score = BigDecimal.valueOf(50);
        if (perf == null) return buildScore("fund_return", "阶段收益", score);

        // 近1年收益
        if (perf.getReturn1y() != null) {
            double r = perf.getReturn1y().doubleValue();
            if (r > 30) score = BigDecimal.valueOf(90);
            else if (r > 20) score = BigDecimal.valueOf(80);
            else if (r > 10) score = BigDecimal.valueOf(65);
            else if (r > 0) score = BigDecimal.valueOf(50);
            else score = BigDecimal.valueOf(30);
        }

        // 同类排名加分
        if (perf.getRank1y() != null) {
            String rank = perf.getRank1y();
            if (rank.contains("前10") || rank.contains("前5")) score = score.add(BigDecimal.valueOf(10));
            else if (rank.contains("前30")) score = score.add(BigDecimal.valueOf(5));
            else if (rank.contains("后30") || rank.contains("后10")) score = score.subtract(BigDecimal.valueOf(10));
        }

        return buildScore("fund_return", "阶段收益", clampScore(score));
    }

    private ScoreDimensionVO scoreFundRisk(FundPerformance perf) {
        BigDecimal score = BigDecimal.valueOf(60);
        if (perf == null) return buildScore("fund_risk", "风控能力", score);

        // 最大回撤评分
        if (perf.getMaxDrawdown1y() != null) {
            double dd = Math.abs(perf.getMaxDrawdown1y().doubleValue());
            if (dd < 10) score = BigDecimal.valueOf(85);
            else if (dd < 20) score = BigDecimal.valueOf(65);
            else if (dd < 30) score = BigDecimal.valueOf(45);
            else score = BigDecimal.valueOf(25);
        }

        // 夏普比率（越高代表风险调整后收益越好）
        if (perf.getSharpRatio1y() != null) {
            double sr = perf.getSharpRatio1y().doubleValue();
            if (sr > 2) score = score.add(BigDecimal.valueOf(15));
            else if (sr > 1) score = score.add(BigDecimal.valueOf(8));
            else if (sr > 0) score = score.add(BigDecimal.valueOf(2));
            else score = score.subtract(BigDecimal.valueOf(10));
        }

        return buildScore("fund_risk", "风控能力", clampScore(score));
    }

    private ScoreDimensionVO scoreHoldings(List<FundHolding> holdings, List<AnalysisResultVO> stockAnalyses) {
        BigDecimal score = BigDecimal.valueOf(50);
        if (holdings == null || holdings.isEmpty()) return buildScore("fund_holdings", "持仓质量", score);

        // 前10集中度 - 过于集中扣分
        double top10Ratio = holdings.stream()
                .filter(h -> h.getHoldRatio() != null)
                .limit(10)
                .mapToDouble(h -> h.getHoldRatio().doubleValue())
                .sum();
        if (top10Ratio > 80) score = BigDecimal.valueOf(40); // 太集中
        else if (top10Ratio > 60) score = BigDecimal.valueOf(55);
        else score = BigDecimal.valueOf(65); // 分散

        // 关联个股分析 - 如果有强势股持仓加分
        long strongHoldings = stockAnalyses.stream()
                .filter(s -> s.getComprehensiveScore() != null
                        && s.getComprehensiveScore().doubleValue() >= 70)
                .count();
        score = score.add(BigDecimal.valueOf(strongHoldings * 5));

        return buildScore("fund_holdings", "持仓质量", clampScore(score));
    }

    private ScoreDimensionVO scoreFundScale(Fund fund) {
        BigDecimal score = BigDecimal.valueOf(50);
        if (fund == null || fund.getFundSize() == null) return buildScore("fund_scale", "规模适宜", score);

        double size = fund.getFundSize().doubleValue();
        if (size >= 5 && size <= 20) score = BigDecimal.valueOf(85);     // 最佳规模
        else if (size >= 20 && size <= 50) score = BigDecimal.valueOf(75);
        else if (size >= 1 && size < 5) score = BigDecimal.valueOf(60);   // 偏小
        else if (size > 50 && size <= 100) score = BigDecimal.valueOf(50); // 偏大
        else if (size > 100) score = BigDecimal.valueOf(30);               // 过大
        else score = BigDecimal.valueOf(20);                               // 迷你基金

        return buildScore("fund_scale", "规模适宜", clampScore(score));
    }

    private ScoreDimensionVO scoreFundFee(Fund fund) {
        BigDecimal score = BigDecimal.valueOf(60);
        if (fund == null) return buildScore("fund_fee", "费率水平", score);

        double totalFee = 0;
        if (fund.getManagementFee() != null) totalFee += fund.getManagementFee().doubleValue();
        if (fund.getCustodianFee() != null) totalFee += fund.getCustodianFee().doubleValue();

        if (totalFee <= 0.6) score = BigDecimal.valueOf(90);    // 指数基金水平
        else if (totalFee <= 1.0) score = BigDecimal.valueOf(75);
        else if (totalFee <= 1.5) score = BigDecimal.valueOf(60);
        else if (totalFee <= 2.0) score = BigDecimal.valueOf(45);
        else score = BigDecimal.valueOf(30);

        return buildScore("fund_fee", "费率水平", clampScore(score));
    }

    private ScoreDimensionVO scoreFundRating(FundPerformance perf) {
        BigDecimal score = BigDecimal.valueOf(50);
        if (perf == null) return buildScore("fund_rating", "外部评级", score);

        if (perf.getMorningstarRating() != null) {
            int star = perf.getMorningstarRating();
            if (star >= 4) score = BigDecimal.valueOf(85);
            else if (star == 3) score = BigDecimal.valueOf(60);
            else score = BigDecimal.valueOf(35);
        }

        return buildScore("fund_rating", "外部评级", clampScore(score));
    }

    private ScoreDimensionVO scoreFundManager(Fund fund) {
        BigDecimal score = BigDecimal.valueOf(50);
        if (fund == null || fund.getManager() == null) return buildScore("fund_manager", "基金经理", score);

        // 基金经理经验评分 - 先默认中等
        score = BigDecimal.valueOf(60);

        // 未来可以联动 stk_fund_manager 表获取更详细数据
        return buildScore("fund_manager", "基金经理", clampScore(score));
    }

    // ========================================================================
    //  分析文本生成
    // ========================================================================

    private String generateReturnAnalysis(FundPerformance perf) {
        if (perf == null) return "暂无阶段收益数据";
        StringBuilder sb = new StringBuilder();
        sb.append("近1周=").append(formatPct(perf.getReturn1w()))
          .append(" 近1月=").append(formatPct(perf.getReturn1m()))
          .append(" 近3月=").append(formatPct(perf.getReturn3m()))
          .append(" 近6月=").append(formatPct(perf.getReturn6m()))
          .append(" 近1年=").append(formatPct(perf.getReturn1y()))
          .append(" 近3年=").append(formatPct(perf.getReturn3y()));

        if (perf.getRank1y() != null) {
            sb.append(" 同类排名=").append(perf.getRank1y());
        }
        return sb.toString();
    }

    private String generateRiskAnalysis(FundPerformance perf) {
        if (perf == null) return "暂无风险数据";
        StringBuilder sb = new StringBuilder();
        sb.append("近1年最大回撤=").append(formatPct(perf.getMaxDrawdown1y()))
          .append(" 夏普比率=").append(perf.getSharpRatio1y() != null
                  ? String.format("%.2f", perf.getSharpRatio1y().doubleValue()) : "N/A");
        return sb.toString();
    }

    private String generateHoldingAnalysis(List<FundHolding> holdings) {
        if (holdings == null || holdings.isEmpty()) return "暂无最新持仓数据";
        StringBuilder sb = new StringBuilder();
        sb.append("前5重仓股: ");
        holdings.stream().limit(5).forEach(h ->
            sb.append(h.getStockName()).append("(")
              .append(h.getHoldRatio()).append("%) ")
        );
        return sb.toString();
    }

    private String generateFeeAnalysis(Fund fund) {
        if (fund == null) return "暂无费率数据";
        double mFee = fund.getManagementFee() != null ? fund.getManagementFee().doubleValue() : 0;
        double cFee = fund.getCustodianFee() != null ? fund.getCustodianFee().doubleValue() : 0;
        return String.format("管理费=%.2f%% 托管费=%.2f%% 合计=%.2f%%",
                mFee, cFee, mFee + cFee);
    }

    private String generateRecommendation(BigDecimal score, Fund fund) {
        double s = score.doubleValue();
        if (s >= 80) return "buy";
        else if (s >= 65) return "hold";
        else if (s >= 45) return "watch";
        else return "sell";
    }

    private String generateSummary(Fund fund, BigDecimal score, List<FundHolding> holdings,
                                    FundPerformance perf) {
        StringBuilder sb = new StringBuilder();
        sb.append(fund.getFundName()).append("(").append(fund.getFundCode()).append(")")
          .append(" 综合评分").append(score).append("分");

        if (perf != null && perf.getReturn1y() != null) {
            sb.append("，近1年收益").append(perf.getReturn1y()).append("%");
        }
        if (perf != null && perf.getMorningstarRating() != null) {
            sb.append("，晨星评级").append(perf.getMorningstarRating()).append("星");
        }

        if (score.doubleValue() >= 70) {
            sb.append("。该基金综合表现优秀，适合作为长期配置选择。");
        } else if (score.doubleValue() >= 50) {
            sb.append("。该基金表现中等，建议结合个人风险偏好决定。");
        } else {
            sb.append("。该基金多项指标偏弱，建议关注其他更优选项。");
        }

        return sb.toString();
    }

    // ========================================================================
    //  工具方法
    // ========================================================================

    private ScoreDimensionVO buildScore(String code, String name, BigDecimal score) {
        ConfigScoreWeight config = configScoreWeightMapper.selectByCode(code);
        BigDecimal weight = config != null ? config.getWeight() : BigDecimal.valueOf(0.10);

        ScoreDimensionVO vo = new ScoreDimensionVO();
        vo.setDimensionCode(code);
        vo.setDimensionName(name);
        vo.setScore(score);
        vo.setWeight(weight);
        vo.setWeightedScore(score.multiply(weight).setScale(2, RoundingMode.HALF_UP));
        vo.setSignal(score.compareTo(BigDecimal.valueOf(60)) >= 0 ? "positive"
                : score.compareTo(BigDecimal.valueOf(40)) >= 0 ? "neutral" : "negative");
        return vo;
    }

    private BigDecimal clampScore(BigDecimal score) {
        if (score.compareTo(BigDecimal.valueOf(100)) > 0) return BigDecimal.valueOf(100);
        if (score.compareTo(BigDecimal.ZERO) < 0) return BigDecimal.ZERO;
        return score.setScale(2, RoundingMode.HALF_UP);
    }

    private String formatPct(BigDecimal pct) {
        return pct != null ? String.format("%+.2f%%", pct.doubleValue()) : "N/A";
    }

    private String convertScoreToStar(BigDecimal score) {
        int s = score.intValue();
        if (s >= 90) return "★★★★★";
        if (s >= 75) return "★★★★☆";
        if (s >= 60) return "★★★☆☆";
        if (s >= 40) return "★★☆☆☆";
        return "★☆☆☆☆";
    }
}
