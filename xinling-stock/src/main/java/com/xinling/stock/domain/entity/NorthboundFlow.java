package com.xinling.stock.domain.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 北向资金（沪深港通）每日流向实体
 */
public class NorthboundFlow implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Date tradeDate;
    private BigDecimal shNetInflow;
    private BigDecimal szNetInflow;
    private BigDecimal totalNetInflow;
    private BigDecimal cumulativeInflow;
    private Date createTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Date getTradeDate() { return tradeDate; }
    public void setTradeDate(Date tradeDate) { this.tradeDate = tradeDate; }

    public BigDecimal getShNetInflow() { return shNetInflow; }
    public void setShNetInflow(BigDecimal shNetInflow) { this.shNetInflow = shNetInflow; }

    public BigDecimal getSzNetInflow() { return szNetInflow; }
    public void setSzNetInflow(BigDecimal szNetInflow) { this.szNetInflow = szNetInflow; }

    public BigDecimal getTotalNetInflow() { return totalNetInflow; }
    public void setTotalNetInflow(BigDecimal totalNetInflow) { this.totalNetInflow = totalNetInflow; }

    public BigDecimal getCumulativeInflow() { return cumulativeInflow; }
    public void setCumulativeInflow(BigDecimal cumulativeInflow) { this.cumulativeInflow = cumulativeInflow; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
}
