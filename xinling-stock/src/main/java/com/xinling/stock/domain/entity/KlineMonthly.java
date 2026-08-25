package com.xinling.stock.domain.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 月K线数据实体
 */
public class KlineMonthly implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String symbolType;
    private String symbolCode;
    private String tradeMonth;
    private Date monthStart;
    private Date monthEnd;
    private BigDecimal openPrice;
    private BigDecimal highPrice;
    private BigDecimal lowPrice;
    private BigDecimal closePrice;
    private Long volume;
    private BigDecimal amount;
    private BigDecimal changePct;
    private Date createTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSymbolType() { return symbolType; }
    public void setSymbolType(String symbolType) { this.symbolType = symbolType; }

    public String getSymbolCode() { return symbolCode; }
    public void setSymbolCode(String symbolCode) { this.symbolCode = symbolCode; }

    public String getTradeMonth() { return tradeMonth; }
    public void setTradeMonth(String tradeMonth) { this.tradeMonth = tradeMonth; }

    public Date getMonthStart() { return monthStart; }
    public void setMonthStart(Date monthStart) { this.monthStart = monthStart; }

    public Date getMonthEnd() { return monthEnd; }
    public void setMonthEnd(Date monthEnd) { this.monthEnd = monthEnd; }

    public BigDecimal getOpenPrice() { return openPrice; }
    public void setOpenPrice(BigDecimal openPrice) { this.openPrice = openPrice; }

    public BigDecimal getHighPrice() { return highPrice; }
    public void setHighPrice(BigDecimal highPrice) { this.highPrice = highPrice; }

    public BigDecimal getLowPrice() { return lowPrice; }
    public void setLowPrice(BigDecimal lowPrice) { this.lowPrice = lowPrice; }

    public BigDecimal getClosePrice() { return closePrice; }
    public void setClosePrice(BigDecimal closePrice) { this.closePrice = closePrice; }

    public Long getVolume() { return volume; }
    public void setVolume(Long volume) { this.volume = volume; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public BigDecimal getChangePct() { return changePct; }
    public void setChangePct(BigDecimal changePct) { this.changePct = changePct; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
}
