package com.xinling.stock.domain.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 期货联动分析结果实体（板块与关联期货的方向一致性评分）
 */
public class LinkageAnalysis implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long sectorId;
    private Date tradeDate;
    private String relatedFutures;
    private String directionConsistency;
    private BigDecimal linkageScore;
    private BigDecimal sectorChangePct;
    private BigDecimal avgFuturesChange;
    private Integer deviationCount;
    private String analysisConclusion;
    private Date createTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSectorId() { return sectorId; }
    public void setSectorId(Long sectorId) { this.sectorId = sectorId; }

    public Date getTradeDate() { return tradeDate; }
    public void setTradeDate(Date tradeDate) { this.tradeDate = tradeDate; }

    public String getRelatedFutures() { return relatedFutures; }
    public void setRelatedFutures(String relatedFutures) { this.relatedFutures = relatedFutures; }

    public String getDirectionConsistency() { return directionConsistency; }
    public void setDirectionConsistency(String directionConsistency) { this.directionConsistency = directionConsistency; }

    public BigDecimal getLinkageScore() { return linkageScore; }
    public void setLinkageScore(BigDecimal linkageScore) { this.linkageScore = linkageScore; }

    public BigDecimal getSectorChangePct() { return sectorChangePct; }
    public void setSectorChangePct(BigDecimal sectorChangePct) { this.sectorChangePct = sectorChangePct; }

    public BigDecimal getAvgFuturesChange() { return avgFuturesChange; }
    public void setAvgFuturesChange(BigDecimal avgFuturesChange) { this.avgFuturesChange = avgFuturesChange; }

    public Integer getDeviationCount() { return deviationCount; }
    public void setDeviationCount(Integer deviationCount) { this.deviationCount = deviationCount; }

    public String getAnalysisConclusion() { return analysisConclusion; }
    public void setAnalysisConclusion(String analysisConclusion) { this.analysisConclusion = analysisConclusion; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
}
