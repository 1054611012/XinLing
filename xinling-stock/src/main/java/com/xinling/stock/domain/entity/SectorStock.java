package com.xinling.stock.domain.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 板块成分股关联实体
 */
public class SectorStock implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long sectorId;
    private String stockCode;
    private String stockName;
    private BigDecimal weight;
    private Integer isLeader;
    private Date createTime;
    private Integer isDeleted;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSectorId() { return sectorId; }
    public void setSectorId(Long sectorId) { this.sectorId = sectorId; }

    public String getStockCode() { return stockCode; }
    public void setStockCode(String stockCode) { this.stockCode = stockCode; }

    public String getStockName() { return stockName; }
    public void setStockName(String stockName) { this.stockName = stockName; }

    public BigDecimal getWeight() { return weight; }
    public void setWeight(BigDecimal weight) { this.weight = weight; }

    public Integer getIsLeader() { return isLeader; }
    public void setIsLeader(Integer isLeader) { this.isLeader = isLeader; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public Integer getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Integer isDeleted) { this.isDeleted = isDeleted; }
}
