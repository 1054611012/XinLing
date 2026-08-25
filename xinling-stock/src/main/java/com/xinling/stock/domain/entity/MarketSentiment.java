package com.xinling.stock.domain.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 全市场情绪指标实体（涨停/跌停/封板率/涨跌比）
 */
public class MarketSentiment implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Date tradeDate;
    private Integer limitUpCount;
    private Integer limitDownCount;
    private Integer stLimitUp;
    private Integer stLimitDown;
    private BigDecimal limitUpAmount;
    private Integer 炸板Count;
    private Integer 连板Count;
    private Integer max连板;
    private BigDecimal 封板率;
    private Integer totalUp;
    private Integer totalDown;
    private Integer totalFlat;
    private BigDecimal upDownRatio;
    private BigDecimal sentimentScore;
    private Date createTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Date getTradeDate() { return tradeDate; }
    public void setTradeDate(Date tradeDate) { this.tradeDate = tradeDate; }

    public Integer getLimitUpCount() { return limitUpCount; }
    public void setLimitUpCount(Integer limitUpCount) { this.limitUpCount = limitUpCount; }

    public Integer getLimitDownCount() { return limitDownCount; }
    public void setLimitDownCount(Integer limitDownCount) { this.limitDownCount = limitDownCount; }

    public Integer getStLimitUp() { return stLimitUp; }
    public void setStLimitUp(Integer stLimitUp) { this.stLimitUp = stLimitUp; }

    public Integer getStLimitDown() { return stLimitDown; }
    public void setStLimitDown(Integer stLimitDown) { this.stLimitDown = stLimitDown; }

    public BigDecimal getLimitUpAmount() { return limitUpAmount; }
    public void setLimitUpAmount(BigDecimal limitUpAmount) { this.limitUpAmount = limitUpAmount; }

    public Integer get炸板Count() { return 炸板Count; }
    public void set炸板Count(Integer 炸板Count) { this.炸板Count = 炸板Count; }

    public Integer get连板Count() { return 连板Count; }
    public void set连板Count(Integer 连板Count) { this.连板Count = 连板Count; }

    public Integer getMax连板() { return max连板; }
    public void setMax连板(Integer max连板) { this.max连板 = max连板; }

    public BigDecimal get封板率() { return 封板率; }
    public void set封板率(BigDecimal 封板率) { this.封板率 = 封板率; }

    public Integer getTotalUp() { return totalUp; }
    public void setTotalUp(Integer totalUp) { this.totalUp = totalUp; }

    public Integer getTotalDown() { return totalDown; }
    public void setTotalDown(Integer totalDown) { this.totalDown = totalDown; }

    public Integer getTotalFlat() { return totalFlat; }
    public void setTotalFlat(Integer totalFlat) { this.totalFlat = totalFlat; }

    public BigDecimal getUpDownRatio() { return upDownRatio; }
    public void setUpDownRatio(BigDecimal upDownRatio) { this.upDownRatio = upDownRatio; }

    public BigDecimal getSentimentScore() { return sentimentScore; }
    public void setSentimentScore(BigDecimal sentimentScore) { this.sentimentScore = sentimentScore; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
}
