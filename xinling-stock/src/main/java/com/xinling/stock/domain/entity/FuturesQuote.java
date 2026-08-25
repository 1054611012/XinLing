package com.xinling.stock.domain.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 期货主力合约行情实体
 */
public class FuturesQuote implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long futuresId;
    private Date tradeDate;
    private String contractCode;
    private BigDecimal openPrice;
    private BigDecimal highPrice;
    private BigDecimal lowPrice;
    private BigDecimal closePrice;
    private BigDecimal preSettle;
    private Long volume;
    private Long openInterest;
    private BigDecimal changePct;
    private Date createTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getFuturesId() { return futuresId; }
    public void setFuturesId(Long futuresId) { this.futuresId = futuresId; }

    public Date getTradeDate() { return tradeDate; }
    public void setTradeDate(Date tradeDate) { this.tradeDate = tradeDate; }

    public String getContractCode() { return contractCode; }
    public void setContractCode(String contractCode) { this.contractCode = contractCode; }

    public BigDecimal getOpenPrice() { return openPrice; }
    public void setOpenPrice(BigDecimal openPrice) { this.openPrice = openPrice; }

    public BigDecimal getHighPrice() { return highPrice; }
    public void setHighPrice(BigDecimal highPrice) { this.highPrice = highPrice; }

    public BigDecimal getLowPrice() { return lowPrice; }
    public void setLowPrice(BigDecimal lowPrice) { this.lowPrice = lowPrice; }

    public BigDecimal getClosePrice() { return closePrice; }
    public void setClosePrice(BigDecimal closePrice) { this.closePrice = closePrice; }

    public BigDecimal getPreSettle() { return preSettle; }
    public void setPreSettle(BigDecimal preSettle) { this.preSettle = preSettle; }

    public Long getVolume() { return volume; }
    public void setVolume(Long volume) { this.volume = volume; }

    public Long getOpenInterest() { return openInterest; }
    public void setOpenInterest(Long openInterest) { this.openInterest = openInterest; }

    public BigDecimal getChangePct() { return changePct; }
    public void setChangePct(BigDecimal changePct) { this.changePct = changePct; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
}
