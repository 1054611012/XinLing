package com.xinling.stock.domain.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 技术指标计算结果实体（MA/MACD/KDJ/RSI/BOLL/OBV）
 */
public class TechnicalIndicator implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String symbolType;
    private String symbolCode;
    private Date tradeDate;

    // 均线
    private BigDecimal ma5;
    private BigDecimal ma10;
    private BigDecimal ma20;
    private BigDecimal ma30;
    private BigDecimal ma60;
    private BigDecimal ma120;
    private BigDecimal ma250;

    // MACD
    private BigDecimal macdDif;
    private BigDecimal macdDea;
    private BigDecimal macdBar;

    // KDJ
    private BigDecimal kdjK;
    private BigDecimal kdjD;
    private BigDecimal kdjJ;

    // RSI
    private BigDecimal rsi6;
    private BigDecimal rsi12;
    private BigDecimal rsi24;

    // BOLL
    private BigDecimal bollMid;
    private BigDecimal bollUpper;
    private BigDecimal bollLower;

    // 其他
    private Long obv;
    private BigDecimal volumeRatio;

    private Date createTime;
    private Date updateTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSymbolType() { return symbolType; }
    public void setSymbolType(String symbolType) { this.symbolType = symbolType; }

    public String getSymbolCode() { return symbolCode; }
    public void setSymbolCode(String symbolCode) { this.symbolCode = symbolCode; }

    public Date getTradeDate() { return tradeDate; }
    public void setTradeDate(Date tradeDate) { this.tradeDate = tradeDate; }

    public BigDecimal getMa5() { return ma5; }
    public void setMa5(BigDecimal ma5) { this.ma5 = ma5; }

    public BigDecimal getMa10() { return ma10; }
    public void setMa10(BigDecimal ma10) { this.ma10 = ma10; }

    public BigDecimal getMa20() { return ma20; }
    public void setMa20(BigDecimal ma20) { this.ma20 = ma20; }

    public BigDecimal getMa30() { return ma30; }
    public void setMa30(BigDecimal ma30) { this.ma30 = ma30; }

    public BigDecimal getMa60() { return ma60; }
    public void setMa60(BigDecimal ma60) { this.ma60 = ma60; }

    public BigDecimal getMa120() { return ma120; }
    public void setMa120(BigDecimal ma120) { this.ma120 = ma120; }

    public BigDecimal getMa250() { return ma250; }
    public void setMa250(BigDecimal ma250) { this.ma250 = ma250; }

    public BigDecimal getMacdDif() { return macdDif; }
    public void setMacdDif(BigDecimal macdDif) { this.macdDif = macdDif; }

    public BigDecimal getMacdDea() { return macdDea; }
    public void setMacdDea(BigDecimal macdDea) { this.macdDea = macdDea; }

    public BigDecimal getMacdBar() { return macdBar; }
    public void setMacdBar(BigDecimal macdBar) { this.macdBar = macdBar; }

    public BigDecimal getKdjK() { return kdjK; }
    public void setKdjK(BigDecimal kdjK) { this.kdjK = kdjK; }

    public BigDecimal getKdjD() { return kdjD; }
    public void setKdjD(BigDecimal kdjD) { this.kdjD = kdjD; }

    public BigDecimal getKdjJ() { return kdjJ; }
    public void setKdjJ(BigDecimal kdjJ) { this.kdjJ = kdjJ; }

    public BigDecimal getRsi6() { return rsi6; }
    public void setRsi6(BigDecimal rsi6) { this.rsi6 = rsi6; }

    public BigDecimal getRsi12() { return rsi12; }
    public void setRsi12(BigDecimal rsi12) { this.rsi12 = rsi12; }

    public BigDecimal getRsi24() { return rsi24; }
    public void setRsi24(BigDecimal rsi24) { this.rsi24 = rsi24; }

    public BigDecimal getBollMid() { return bollMid; }
    public void setBollMid(BigDecimal bollMid) { this.bollMid = bollMid; }

    public BigDecimal getBollUpper() { return bollUpper; }
    public void setBollUpper(BigDecimal bollUpper) { this.bollUpper = bollUpper; }

    public BigDecimal getBollLower() { return bollLower; }
    public void setBollLower(BigDecimal bollLower) { this.bollLower = bollLower; }

    public Long getObv() { return obv; }
    public void setObv(Long obv) { this.obv = obv; }

    public BigDecimal getVolumeRatio() { return volumeRatio; }
    public void setVolumeRatio(BigDecimal volumeRatio) { this.volumeRatio = volumeRatio; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
}
