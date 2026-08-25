package com.xinling.stock.domain.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * AI分析报告实体（含各维度分析 + 交易建议）
 */
public class AnalysisReport implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String symbolType;
    private String symbolCode;
    private String symbolName;
    private Date analysisDate;
    private String analysisType;

    // 综合评分
    private BigDecimal comprehensiveScore;
    private String scoreDetail;

    // 各维度分析
    private String trendAnalysis;
    private String maAnalysis;
    private String macdAnalysis;
    private String kdjAnalysis;
    private String volumeAnalysis;
    private String sectorAnalysis;
    private String futuresLinkage;
    private String fundAnalysis;
    private String newsSentiment;
    private String marketSentiment;

    // AI总结
    private String aiSummary;
    private String riskAnalysis;

    // 交易建议
    private BigDecimal buyProbability;
    private String riskLevel;
    private BigDecimal suggestedPosition;
    private String buyZone;
    private String sellZone;
    private BigDecimal stopLoss;
    private BigDecimal takeProfit;
    private String recommendation;

    // 元信息
    private String modelUsed;
    private String promptVersion;
    private Integer analysisStatus;
    private String errorMsg;
    private Date createTime;
    private Date updateTime;
    private Integer isDeleted;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSymbolType() { return symbolType; }
    public void setSymbolType(String symbolType) { this.symbolType = symbolType; }

    public String getSymbolCode() { return symbolCode; }
    public void setSymbolCode(String symbolCode) { this.symbolCode = symbolCode; }

    public String getSymbolName() { return symbolName; }
    public void setSymbolName(String symbolName) { this.symbolName = symbolName; }

    public Date getAnalysisDate() { return analysisDate; }
    public void setAnalysisDate(Date analysisDate) { this.analysisDate = analysisDate; }

    public String getAnalysisType() { return analysisType; }
    public void setAnalysisType(String analysisType) { this.analysisType = analysisType; }

    public BigDecimal getComprehensiveScore() { return comprehensiveScore; }
    public void setComprehensiveScore(BigDecimal comprehensiveScore) { this.comprehensiveScore = comprehensiveScore; }

    public String getScoreDetail() { return scoreDetail; }
    public void setScoreDetail(String scoreDetail) { this.scoreDetail = scoreDetail; }

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

    public String getModelUsed() { return modelUsed; }
    public void setModelUsed(String modelUsed) { this.modelUsed = modelUsed; }

    public String getPromptVersion() { return promptVersion; }
    public void setPromptVersion(String promptVersion) { this.promptVersion = promptVersion; }

    public Integer getAnalysisStatus() { return analysisStatus; }
    public void setAnalysisStatus(Integer analysisStatus) { this.analysisStatus = analysisStatus; }

    public String getErrorMsg() { return errorMsg; }
    public void setErrorMsg(String errorMsg) { this.errorMsg = errorMsg; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }

    public Integer getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Integer isDeleted) { this.isDeleted = isDeleted; }
}
