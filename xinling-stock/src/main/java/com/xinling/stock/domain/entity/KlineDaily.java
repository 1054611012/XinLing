package com.xinling.stock.domain.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 日K线数据实体（统一存储股票/板块/期货/指数）
 */
public class KlineDaily implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String symbolType;
    private String symbolCode;
    private Date tradeDate;
    private BigDecimal openPrice;
    private BigDecimal highPrice;
    private BigDecimal lowPrice;
    private BigDecimal closePrice;
    private BigDecimal preClose;
    private Long volume;
    private BigDecimal amount;
    private BigDecimal changePct;
    private BigDecimal amplitude;
    private BigDecimal turnoverRate;
    private Date createTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSymbolType() { return symbolType; }
    public void setSymbolType(String symbolType) { this.symbolType = symbolType; }

    public String getSymbolCode() { return symbolCode; }
    public void setSymbolCode(String symbolCode) { this.symbolCode = symbolCode; }

    public Date getTradeDate() { return tradeDate; }
    public void setTradeDate(Date tradeDate) { this.tradeDate = tradeDate; }

    public BigDecimal getOpenPrice() { return openPrice; }
    public void setOpenPrice(BigDecimal openPrice) { this.openPrice = openPrice; }

    public BigDecimal getHighPrice() { return highPrice; }
    public void setHighPrice(BigDecimal highPrice) { this.highPrice = highPrice; }

    public BigDecimal getLowPrice() { return lowPrice; }
    public void setLowPrice(BigDecimal lowPrice) { this.lowPrice = lowPrice; }

    public BigDecimal getClosePrice() { return closePrice; }
    public void setClosePrice(BigDecimal closePrice) { this.closePrice = closePrice; }

    public BigDecimal getPreClose() { return preClose; }
    public void setPreClose(BigDecimal preClose) { this.preClose = preClose; }

    public Long getVolume() { return volume; }
    public void setVolume(Long volume) { this.volume = volume; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public BigDecimal getChangePct() { return changePct; }
    public void setChangePct(BigDecimal changePct) { this.changePct = changePct; }

    public BigDecimal getAmplitude() { return amplitude; }
    public void setAmplitude(BigDecimal amplitude) { this.amplitude = amplitude; }

    public BigDecimal getTurnoverRate() { return turnoverRate; }
    public void setTurnoverRate(BigDecimal turnoverRate) { this.turnoverRate = turnoverRate; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
}
