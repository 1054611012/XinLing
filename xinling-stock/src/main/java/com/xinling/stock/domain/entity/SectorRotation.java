package com.xinling.stock.domain.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 板块轮动分析记录实体
 */
public class SectorRotation implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Date tradeDate;
    private String topSectors;
    private String rotationDirection;
    private String rotationSignal;
    private String analysisSummary;
    private Date createTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Date getTradeDate() { return tradeDate; }
    public void setTradeDate(Date tradeDate) { this.tradeDate = tradeDate; }

    public String getTopSectors() { return topSectors; }
    public void setTopSectors(String topSectors) { this.topSectors = topSectors; }

    public String getRotationDirection() { return rotationDirection; }
    public void setRotationDirection(String rotationDirection) { this.rotationDirection = rotationDirection; }

    public String getRotationSignal() { return rotationSignal; }
    public void setRotationSignal(String rotationSignal) { this.rotationSignal = rotationSignal; }

    public String getAnalysisSummary() { return analysisSummary; }
    public void setAnalysisSummary(String analysisSummary) { this.analysisSummary = analysisSummary; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
}
