package com.xinling.stock.domain.vo;

import java.math.BigDecimal;
import java.util.List;

/**
 * AI分析结果 VO（前端展示用）
 */
public class AnalysisResultVO {

    /** 综合评分(0-100) */
    private BigDecimal comprehensiveScore;

    /** 评分等级: ★ ~ ★★★★★ */
    private String scoreLevel;

    /** 各维度评分明细 */
    private List<ScoreDimensionVO> dimensions;

    /** 综合趋势描述 */
    private String trendAnalysis;

    /** 均线分析 */
    private String maAnalysis;

    /** MACD分析 */
    private String macdAnalysis;

    /** KDJ分析 */
    private String kdjAnalysis;

    /** 成交量分析 */
    private String volumeAnalysis;

    /** 板块分析 */
    private String sectorAnalysis;

    /** 期货联动分析 */
    private String futuresLinkage;

    /** 资金面分析 */
    private String fundAnalysis;

    /** 消息面分析 */
    private String newsSentiment;

    /** 市场情绪分析 */
    private String marketSentiment;

    /** AI综合总结 */
    private String aiSummary;

    /** 风险分析 */
    private String riskAnalysis;

    /** 买入概率(%) */
    private BigDecimal buyProbability;

    /** 风险等级: low / mid / high */
    private String riskLevel;

    /** 建议仓位(%) */
    private BigDecimal suggestedPosition;

    /** 买入区间 */
    private String buyZone;

    /** 卖出区间 */
    private String sellZone;

    /** 止损位 */
    private BigDecimal stopLoss;

    /** 止盈位 */
    private BigDecimal takeProfit;

    /** 操作建议: strong_buy / buy / hold / watch / sell / strong_sell */
    private String recommendation;

    /** 是否使用AI深度分析 */
    private Boolean isDeepAnalysis;

    // ===== getters & setters =====

    public BigDecimal getComprehensiveScore() { return comprehensiveScore; }
    public void setComprehensiveScore(BigDecimal comprehensiveScore) { this.comprehensiveScore = comprehensiveScore; }

    public String getScoreLevel() { return scoreLevel; }
    public void setScoreLevel(String scoreLevel) { this.scoreLevel = scoreLevel; }

    public List<ScoreDimensionVO> getDimensions() { return dimensions; }
    public void setDimensions(List<ScoreDimensionVO> dimensions) { this.dimensions = dimensions; }

    public String getTrendAnalysis() { return trendAnalysis; }
    public void setTrendAnalysis(String trendAnalysis) { this.trendAnalysis = trendAnalysis; }

    public String getMaAnalysis() { return maAnalysis; }
    public void setMaAnalysis(String maAnalysis) { this.maAnalysis = maAnalysis; }

    public String getMacdAnalysis() { return macdAnalysis; }
    public void setMacdAnalysis(String macdAnalysis) { this.macdAnalysis = macdAnalysis; }

    public String getKdjAnalysis() { return kdjAnalysis; }
    public void setKdjAnalysis(String kdjAnalysis) { this.kdjAnalysis = kdjAnalysis; }

    public String getVolumeAnalysis() { return volumeAnalysis; }
    public void setVolumeAnalysis(String volumeAnalysis) { this.volumeAnalysis = volumeAnalysis; }

    public String getSectorAnalysis() { return sectorAnalysis; }
    public void setSectorAnalysis(String sectorAnalysis) { this.sectorAnalysis = sectorAnalysis; }

    public String getFuturesLinkage() { return futuresLinkage; }
    public void setFuturesLinkage(String futuresLinkage) { this.futuresLinkage = futuresLinkage; }

    public String getFundAnalysis() { return fundAnalysis; }
    public void setFundAnalysis(String fundAnalysis) { this.fundAnalysis = fundAnalysis; }

    public String getNewsSentiment() { return newsSentiment; }
    public void setNewsSentiment(String newsSentiment) { this.newsSentiment = newsSentiment; }

    public String getMarketSentiment() { return marketSentiment; }
    public void setMarketSentiment(String marketSentiment) { this.marketSentiment = marketSentiment; }

    public String getAiSummary() { return aiSummary; }
    public void setAiSummary(String aiSummary) { this.aiSummary = aiSummary; }

    public String getRiskAnalysis() { return riskAnalysis; }
    public void setRiskAnalysis(String riskAnalysis) { this.riskAnalysis = riskAnalysis; }

    public BigDecimal getBuyProbability() { return buyProbability; }
    public void setBuyProbability(BigDecimal buyProbability) { this.buyProbability = buyProbability; }

    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }

    public BigDecimal getSuggestedPosition() { return suggestedPosition; }
    public void setSuggestedPosition(BigDecimal suggestedPosition) { this.suggestedPosition = suggestedPosition; }

    public String getBuyZone() { return buyZone; }
    public void setBuyZone(String buyZone) { this.buyZone = buyZone; }

    public String getSellZone() { return sellZone; }
    public void setSellZone(String sellZone) { this.sellZone = sellZone; }

    public BigDecimal getStopLoss() { return stopLoss; }
    public void setStopLoss(BigDecimal stopLoss) { this.stopLoss = stopLoss; }

    public BigDecimal getTakeProfit() { return takeProfit; }
    public void setTakeProfit(BigDecimal takeProfit) { this.takeProfit = takeProfit; }

    public String getRecommendation() { return recommendation; }
    public void setRecommendation(String recommendation) { this.recommendation = recommendation; }

    public Boolean getIsDeepAnalysis() { return isDeepAnalysis; }
    public void setIsDeepAnalysis(Boolean isDeepAnalysis) { this.isDeepAnalysis = isDeepAnalysis; }
}
