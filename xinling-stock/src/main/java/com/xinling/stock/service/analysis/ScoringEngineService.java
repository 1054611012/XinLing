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
 * 规则评分引擎
 *
 * 基于硬编码的规则对每个维度进行打分（0-100），
 * 然后按权重计算综合评分。
 * AI 层在此基础上进行修正和解读。
 */
@Service
public class ScoringEngineService {

    private static final Logger log = LoggerFactory.getLogger(ScoringEngineService.class);
    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

    private final ConfigScoreWeightMapper configScoreWeightMapper;

    public ScoringEngineService(ConfigScoreWeightMapper configScoreWeightMapper) {
        this.configScoreWeightMapper = configScoreWeightMapper;
    }

    /**
     * 执行综合评分，返回各维度评分列表 + 总分
     */
    public ScoringResult executeScoring(ScoringContext ctx) {
        List<ScoreDimensionVO> dimensions = new ArrayList<>();
        BigDecimal totalScore = BigDecimal.ZERO;
        BigDecimal totalWeight = BigDecimal.ZERO;

        // 1. 板块趋势评分
        if (ctx.getSectorPerformance() != null) {
            ScoreDimensionVO sd = scoreSectorTrend(ctx.getSectorPerformance());
            dimensions.add(sd);
            totalScore = totalScore.add(sd.getWeightedScore());
            totalWeight = totalWeight.add(sd.getWeight());
        }

        // 2. 期货联动评分
        if (ctx.getLinkageAnalysis() != null) {
            ScoreDimensionVO sd = scoreFuturesSync(ctx.getLinkageAnalysis());
            dimensions.add(sd);
            totalScore = totalScore.add(sd.getWeightedScore());
            totalWeight = totalWeight.add(sd.getWeight());
        }

        // 3. 均线系统评分
        if (ctx.getIndicator() != null) {
            ScoreDimensionVO sd = scoreMaTrend(ctx.getIndicator());
            dimensions.add(sd);
            totalScore = totalScore.add(sd.getWeightedScore());
            totalWeight = totalWeight.add(sd.getWeight());
        }

        // 4. MACD评分
        if (ctx.getIndicator() != null) {
            ScoreDimensionVO sd = scoreMacd(ctx.getIndicator());
            dimensions.add(sd);
            totalScore = totalScore.add(sd.getWeightedScore());
            totalWeight = totalWeight.add(sd.getWeight());
        }

        // 5. KDJ评分
        if (ctx.getIndicator() != null) {
            ScoreDimensionVO sd = scoreKdj(ctx.getIndicator());
            dimensions.add(sd);
            totalScore = totalScore.add(sd.getWeightedScore());
            totalWeight = totalWeight.add(sd.getWeight());
        }

        // 6. 成交量评分
        if (ctx.getIndicator() != null && ctx.getKlineList() != null && !ctx.getKlineList().isEmpty()) {
            ScoreDimensionVO sd = scoreVolume(ctx.getIndicator(), ctx.getKlineList());
            dimensions.add(sd);
            totalScore = totalScore.add(sd.getWeightedScore());
            totalWeight = totalWeight.add(sd.getWeight());
        }

        // 7. 资金面评分
        if (ctx.getFundFlow() != null) {
            ScoreDimensionVO sd = scoreFundFlow(ctx.getFundFlow());
            dimensions.add(sd);
            totalScore = totalScore.add(sd.getWeightedScore());
            totalWeight = totalWeight.add(sd.getWeight());
        }

        // 8. 市场情绪评分
        if (ctx.getMarketSentiment() != null) {
            ScoreDimensionVO sd = scoreMarketSentiment(ctx.getMarketSentiment());
            dimensions.add(sd);
            totalScore = totalScore.add(sd.getWeightedScore());
            totalWeight = totalWeight.add(sd.getWeight());
        }

        // 计算综合评分（归一化）
        BigDecimal comprehensiveScore = totalWeight.compareTo(BigDecimal.ZERO) > 0
                ? totalScore.divide(totalWeight, 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        return new ScoringResult(comprehensiveScore, dimensions);
    }

    // ========================================================================
    //  维度评分规则
    // ========================================================================

    /**
     * 板块趋势评分 (权重 15%)
     * 规则: 涨幅越高 + 成交额越大 + 上涨家数多 = 高分
     */
    private ScoreDimensionVO scoreSectorTrend(SectorPerformance sp) {
        BigDecimal score = BigDecimal.valueOf(50); // 基础分50

        // 涨跌幅打分: ±5% 对应 ±25分
        if (sp.getChangePct() != null) {
            double change = sp.getChangePct().doubleValue();
            score = score.add(BigDecimal.valueOf(change * 5));
        }

        // 上涨/下跌家数比打分
        if (sp.getAdvanceCount() != null && sp.getDeclineCount() != null
                && (sp.getAdvanceCount() + sp.getDeclineCount()) > 0) {
            double ratio = (double) sp.getAdvanceCount()
                    / (sp.getAdvanceCount() + sp.getDeclineCount());
            // ratio 0.5->0分, 1.0->25分, 0.0->-25分
            score = score.add(BigDecimal.valueOf((ratio - 0.5) * 50));
        }

        // 排行榜加分: 前5名加分
        if (sp.getRank1d() != null && sp.getRank1d() <= 5) {
            score = score.add(BigDecimal.valueOf(10));
        }

        // 涨停加分
        if (sp.getLimitUpCount() != null && sp.getLimitUpCount() > 0) {
            score = score.add(BigDecimal.valueOf(Math.min(sp.getLimitUpCount() * 3, 15)));
        }

        return buildDimension("sector_trend", "板块趋势", clampScore(score));
    }

    /**
     * 期货联动评分 (权重 10%)
     * 规则: 方向一致则高分，背离则低分
     */
    private ScoreDimensionVO scoreFuturesSync(LinkageAnalysis la) {
        BigDecimal score = BigDecimal.valueOf(50);

        if (la.getLinkageScore() != null) {
            // 直接使用联动分析评分作为基础
            score = la.getLinkageScore();
        }

        if ("consistent".equals(la.getDirectionConsistency())) {
            score = score.add(BigDecimal.valueOf(20));
        } else if ("partial".equals(la.getDirectionConsistency())) {
            score = score.add(BigDecimal.valueOf(5));
        } else if ("deviation".equals(la.getDirectionConsistency())) {
            score = score.subtract(BigDecimal.valueOf(20));
        }

        // 背离品种越多越扣分
        if (la.getDeviationCount() != null && la.getDeviationCount() > 0) {
            score = score.subtract(BigDecimal.valueOf(la.getDeviationCount() * 3));
        }

        return buildDimension("futures_sync", "期货联动", clampScore(score));
    }

    /**
     * 均线系统评分 (权重 15%)
     * 规则: 多头排列 + 价格在均线上方 = 高分
     *       空头排列 + 价格在均线下方 = 低分
     */
    private ScoreDimensionVO scoreMaTrend(TechnicalIndicator ti) {
        BigDecimal score = BigDecimal.valueOf(50);

        // 检查多头排列: MA5 > MA10 > MA20 > MA60
        boolean isBullish = isMaBullish(ti);
        // 检查空头排列: MA5 < MA10 < MA20 < MA60
        boolean isBearish = isMaBearish(ti);

        if (isBullish) {
            score = BigDecimal.valueOf(85);
        } else if (isBearish) {
            score = BigDecimal.valueOf(20);
        } else {
            // 部分多头 = 中间分
            if (isMaPartialBullish(ti)) score = BigDecimal.valueOf(65);
            if (isMaPartialBearish(ti)) score = BigDecimal.valueOf(35);
        }

        return buildDimension("ma_trend", "均线趋势", clampScore(score));
    }

    /**
     * MACD评分 (权重 10%)
     * 规则: 金叉+红柱+零轴上=高分, 死叉+绿柱+零轴下=低分
     */
    private ScoreDimensionVO scoreMacd(TechnicalIndicator ti) {
        BigDecimal score = BigDecimal.valueOf(50);
        StringBuilder signal = new StringBuilder();

        if (ti.getMacdDif() == null || ti.getMacdDea() == null) {
            return buildDimension("macd", "MACD指标", clampScore(score));
        }

        double dif = ti.getMacdDif().doubleValue();
        double dea = ti.getMacdDea().doubleValue();
        double bar = ti.getMacdBar() != null ? ti.getMacdBar().doubleValue() : 0;

        // 金叉/死叉判断
        if (dif > dea) {
            score = score.add(BigDecimal.valueOf(15));
            signal.append("金叉");
        } else {
            score = score.subtract(BigDecimal.valueOf(15));
            signal.append("死叉");
        }

        // 零轴位置
        if (dif > 0 && dea > 0) {
            score = score.add(BigDecimal.valueOf(10));
            signal.append("+零轴上");
        } else if (dif < 0 && dea < 0) {
            score = score.subtract(BigDecimal.valueOf(10));
            signal.append("+零轴下");
        }

        // 红绿柱趋势
        if (bar > 0) {
            score = score.add(BigDecimal.valueOf(10));
            signal.append("+红柱");
        } else {
            score = score.subtract(BigDecimal.valueOf(10));
            signal.append("+绿柱");
        }

        return buildDimension("macd", "MACD指标", clampScore(score));
    }

    /**
     * KDJ评分 (权重 5%)
     * 规则: J<20超卖可能反弹=高分, J>100超买风险=低分
     */
    private ScoreDimensionVO scoreKdj(TechnicalIndicator ti) {
        BigDecimal score = BigDecimal.valueOf(50);

        if (ti.getKdjJ() == null) {
            return buildDimension("kdj", "KDJ指标", clampScore(score));
        }

        double j = ti.getKdjJ().doubleValue();

        if (j < 20) {
            // 超卖区 = 反弹机会
            score = BigDecimal.valueOf(80);
        } else if (j < 50) {
            score = BigDecimal.valueOf(60);
        } else if (j < 80) {
            score = BigDecimal.valueOf(50);
        } else if (j < 100) {
            score = BigDecimal.valueOf(35);
        } else {
            // J > 100 超买
            score = BigDecimal.valueOf(20);
        }

        return buildDimension("kdj", "KDJ指标", clampScore(score));
    }

    /**
     * 成交量评分 (权重 15%)
     * 规则: 放量上涨=高分, 缩量上涨=低分, 缩量下跌=正常, 放量下跌=低分
     */
    private ScoreDimensionVO scoreVolume(TechnicalIndicator ti, List<KlineDaily> klineList) {
        BigDecimal score = BigDecimal.valueOf(50);

        if (klineList.size() < 2) return buildDimension("volume", "成交量", clampScore(score));

        KlineDaily latest = klineList.get(klineList.size() - 1);
        KlineDaily prev = klineList.get(klineList.size() - 2);

        boolean isUp = latest.getChangePct() != null && latest.getChangePct().doubleValue() > 0;
        boolean isDown = latest.getChangePct() != null && latest.getChangePct().doubleValue() < 0;
        boolean volumeUp = ti.getVolumeRatio() != null && ti.getVolumeRatio().doubleValue() > 1.2;
        boolean volumeDown = ti.getVolumeRatio() != null && ti.getVolumeRatio().doubleValue() < 0.8;

        if (isUp && volumeUp) {
            // 放量上涨 → 资金认可
            score = BigDecimal.valueOf(90);
        } else if (isUp && volumeDown) {
            // 缩量上涨 → 动力不足
            score = BigDecimal.valueOf(40);
        } else if (isDown && volumeUp) {
            // 放量下跌 → 抛压大
            score = BigDecimal.valueOf(15);
        } else if (isDown && volumeDown) {
            // 缩量下跌 → 正常调整
            score = BigDecimal.valueOf(70);
        }

        return buildDimension("volume", "成交量", clampScore(score));
    }

    /**
     * 资金面评分 (权重 15%)
     * 规则: 主力净流入=高分, 净流出=低分
     */
    private ScoreDimensionVO scoreFundFlow(FundFlow ff) {
        BigDecimal score = BigDecimal.valueOf(50);

        if (ff.getMainNetInflow() != null) {
            double inflow = ff.getMainNetInflow().doubleValue();
            if (inflow > 0) {
                // 净流入: 每1亿加3分, 上限40分
                double bonus = Math.min(inflow / 10000 * 3, 40);
                score = score.add(BigDecimal.valueOf(bonus));
            } else {
                // 净流出: 每1亿减3分, 上限-40分
                double penalty = Math.min(Math.abs(inflow) / 10000 * 3, 40);
                score = score.subtract(BigDecimal.valueOf(penalty));
            }
        }

        return buildDimension("fund_flow", "主力资金", clampScore(score));
    }

    /**
     * 市场情绪评分 (权重 5%)
     * 规则: 涨停多+封板率高+涨跌比大=高分
     */
    private ScoreDimensionVO scoreMarketSentiment(MarketSentiment ms) {
        BigDecimal score = BigDecimal.valueOf(50);

        // 涨跌比
        if (ms.getUpDownRatio() != null) {
            double ratio = ms.getUpDownRatio().doubleValue();
            if (ratio > 3) score = score.add(BigDecimal.valueOf(20));
            else if (ratio > 1.5) score = score.add(BigDecimal.valueOf(10));
            else if (ratio < 0.5) score = score.subtract(BigDecimal.valueOf(15));
            else if (ratio < 0.3) score = score.subtract(BigDecimal.valueOf(25));
        }

        // 封板率
        if (ms.get封板率() != null) {
            double rate = ms.get封板率().doubleValue();
            if (rate > 80) score = score.add(BigDecimal.valueOf(15));
            else if (rate < 50) score = score.subtract(BigDecimal.valueOf(10));
        }

        // 涨停家数
        if (ms.getLimitUpCount() != null) {
            if (ms.getLimitUpCount() > 100) score = score.add(BigDecimal.valueOf(15));
            else if (ms.getLimitUpCount() > 50) score = score.add(BigDecimal.valueOf(8));
            else if (ms.getLimitUpCount() < 20) score = score.subtract(BigDecimal.valueOf(5));
        }

        return buildDimension("market_sentiment", "市场情绪", clampScore(score));
    }

    // ========================================================================
    //  均线形态判断
    // ========================================================================

    private boolean isMaBullish(TechnicalIndicator ti) {
        return allNotNull(ti.getMa5(), ti.getMa10(), ti.getMa20(), ti.getMa60())
                && ti.getMa5().compareTo(ti.getMa10()) > 0
                && ti.getMa10().compareTo(ti.getMa20()) > 0
                && ti.getMa20().compareTo(ti.getMa60()) > 0;
    }

    private boolean isMaBearish(TechnicalIndicator ti) {
        return allNotNull(ti.getMa5(), ti.getMa10(), ti.getMa20(), ti.getMa60())
                && ti.getMa5().compareTo(ti.getMa10()) < 0
                && ti.getMa10().compareTo(ti.getMa20()) < 0
                && ti.getMa20().compareTo(ti.getMa60()) < 0;
    }

    private boolean isMaPartialBullish(TechnicalIndicator ti) {
        return allNotNull(ti.getMa5(), ti.getMa10(), ti.getMa20())
                && ti.getMa5().compareTo(ti.getMa10()) > 0
                && ti.getMa10().compareTo(ti.getMa20()) > 0;
    }

    private boolean isMaPartialBearish(TechnicalIndicator ti) {
        return allNotNull(ti.getMa5(), ti.getMa10(), ti.getMa20())
                && ti.getMa5().compareTo(ti.getMa10()) < 0
                && ti.getMa10().compareTo(ti.getMa20()) < 0;
    }

    // ========================================================================
    //  工具方法
    // ========================================================================

    private ScoreDimensionVO buildDimension(String code, String name, BigDecimal score) {
        // 从配置表读取权重
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
        if (score.compareTo(HUNDRED) > 0) return HUNDRED;
        if (score.compareTo(BigDecimal.ZERO) < 0) return BigDecimal.ZERO;
        return score.setScale(2, RoundingMode.HALF_UP);
    }

    private boolean allNotNull(BigDecimal... values) {
        for (BigDecimal v : values) {
            if (v == null) return false;
        }
        return true;
    }

    // ========================================================================
    //  内部类
    // ========================================================================

    /**
     * 评分上下文：所有评分所需的输入数据
     */
    public static class ScoringContext {
        private TechnicalIndicator indicator;
        private SectorPerformance sectorPerformance;
        private LinkageAnalysis linkageAnalysis;
        private FundFlow fundFlow;
        private MarketSentiment marketSentiment;
        private List<KlineDaily> klineList;

        public TechnicalIndicator getIndicator() { return indicator; }
        public void setIndicator(TechnicalIndicator indicator) { this.indicator = indicator; }

        public SectorPerformance getSectorPerformance() { return sectorPerformance; }
        public void setSectorPerformance(SectorPerformance sectorPerformance) { this.sectorPerformance = sectorPerformance; }

        public LinkageAnalysis getLinkageAnalysis() { return linkageAnalysis; }
        public void setLinkageAnalysis(LinkageAnalysis linkageAnalysis) { this.linkageAnalysis = linkageAnalysis; }

        public FundFlow getFundFlow() { return fundFlow; }
        public void setFundFlow(FundFlow fundFlow) { this.fundFlow = fundFlow; }

        public MarketSentiment getMarketSentiment() { return marketSentiment; }
        public void setMarketSentiment(MarketSentiment marketSentiment) { this.marketSentiment = marketSentiment; }

        public List<KlineDaily> getKlineList() { return klineList; }
        public void setKlineList(List<KlineDaily> klineList) { this.klineList = klineList; }
    }

    /**
     * 评分结果
     */
    public static class ScoringResult {
        private final BigDecimal comprehensiveScore;
        private final List<ScoreDimensionVO> dimensions;

        public ScoringResult(BigDecimal comprehensiveScore, List<ScoreDimensionVO> dimensions) {
            this.comprehensiveScore = comprehensiveScore;
            this.dimensions = dimensions;
        }

        public BigDecimal getComprehensiveScore() { return comprehensiveScore; }
        public List<ScoreDimensionVO> getDimensions() { return dimensions; }
    }
}
