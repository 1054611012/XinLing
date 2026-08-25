package com.xinling.stock.domain.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 板块资金流向汇总实体
 */
public class SectorFundFlow implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long sectorId;
    private Date tradeDate;
    private BigDecimal mainNetInflow;
    private BigDecimal amount;
    private Integer inflowRank;
    private Date createTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSectorId() { return sectorId; }
    public void setSectorId(Long sectorId) { this.sectorId = sectorId; }

    public Date getTradeDate() { return tradeDate; }
    public void setTradeDate(Date tradeDate) { this.tradeDate = tradeDate; }

    public BigDecimal getMainNetInflow() { return mainNetInflow; }
    public void setMainNetInflow(BigDecimal mainNetInflow) { this.mainNetInflow = mainNetInflow; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public Integer getInflowRank() { return inflowRank; }
    public void setInflowRank(Integer inflowRank) { this.inflowRank = inflowRank; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
}
