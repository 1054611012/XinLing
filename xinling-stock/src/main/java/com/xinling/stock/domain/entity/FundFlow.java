package com.xinling.stock.domain.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 个股资金流向实体（主力/超大单/大单/中单/小单）
 */
public class FundFlow implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String stockCode;
    private Date tradeDate;
    private BigDecimal mainNetInflow;
    private BigDecimal superLargeInflow;
    private BigDecimal largeInflow;
    private BigDecimal mediumInflow;
    private BigDecimal smallInflow;
    private BigDecimal mainNetPct;
    private Integer mainInflowRank;
    private Date createTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getStockCode() { return stockCode; }
    public void setStockCode(String stockCode) { this.stockCode = stockCode; }

    public Date getTradeDate() { return tradeDate; }
    public void setTradeDate(Date tradeDate) { this.tradeDate = tradeDate; }

    public BigDecimal getMainNetInflow() { return mainNetInflow; }
    public void setMainNetInflow(BigDecimal mainNetInflow) { this.mainNetInflow = mainNetInflow; }

    public BigDecimal getSuperLargeInflow() { return superLargeInflow; }
    public void setSuperLargeInflow(BigDecimal superLargeInflow) { this.superLargeInflow = superLargeInflow; }

    public BigDecimal getLargeInflow() { return largeInflow; }
    public void setLargeInflow(BigDecimal largeInflow) { this.largeInflow = largeInflow; }

    public BigDecimal getMediumInflow() { return mediumInflow; }
    public void setMediumInflow(BigDecimal mediumInflow) { this.mediumInflow = mediumInflow; }

    public BigDecimal getSmallInflow() { return smallInflow; }
    public void setSmallInflow(BigDecimal smallInflow) { this.smallInflow = smallInflow; }

    public BigDecimal getMainNetPct() { return mainNetPct; }
    public void setMainNetPct(BigDecimal mainNetPct) { this.mainNetPct = mainNetPct; }

    public Integer getMainInflowRank() { return mainInflowRank; }
    public void setMainInflowRank(Integer mainInflowRank) { this.mainInflowRank = mainInflowRank; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
}
