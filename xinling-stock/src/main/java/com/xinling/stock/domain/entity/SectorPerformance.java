package com.xinling.stock.domain.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 板块每日表现统计实体
 */
public class SectorPerformance implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long sectorId;
    private Date tradeDate;
    private BigDecimal changePct;
    private Long volume;
    private BigDecimal amount;
    private Integer advanceCount;
    private Integer declineCount;
    private Integer limitUpCount;
    private Integer limitDownCount;
    private String leaderStock;
    private BigDecimal leaderChangePct;
    private Integer rank1d;
    private Integer rank5d;
    private Integer rank20d;
    private Date createTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSectorId() { return sectorId; }
    public void setSectorId(Long sectorId) { this.sectorId = sectorId; }

    public Date getTradeDate() { return tradeDate; }
    public void setTradeDate(Date tradeDate) { this.tradeDate = tradeDate; }

    public BigDecimal getChangePct() { return changePct; }
    public void setChangePct(BigDecimal changePct) { this.changePct = changePct; }

    public Long getVolume() { return volume; }
    public void setVolume(Long volume) { this.volume = volume; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public Integer getAdvanceCount() { return advanceCount; }
    public void setAdvanceCount(Integer advanceCount) { this.advanceCount = advanceCount; }

    public Integer getDeclineCount() { return declineCount; }
    public void setDeclineCount(Integer declineCount) { this.declineCount = declineCount; }

    public Integer getLimitUpCount() { return limitUpCount; }
    public void setLimitUpCount(Integer limitUpCount) { this.limitUpCount = limitUpCount; }

    public Integer getLimitDownCount() { return limitDownCount; }
    public void setLimitDownCount(Integer limitDownCount) { this.limitDownCount = limitDownCount; }

    public String getLeaderStock() { return leaderStock; }
    public void setLeaderStock(String leaderStock) { this.leaderStock = leaderStock; }

    public BigDecimal getLeaderChangePct() { return leaderChangePct; }
    public void setLeaderChangePct(BigDecimal leaderChangePct) { this.leaderChangePct = leaderChangePct; }

    public Integer getRank1d() { return rank1d; }
    public void setRank1d(Integer rank1d) { this.rank1d = rank1d; }

    public Integer getRank5d() { return rank5d; }
    public void setRank5d(Integer rank5d) { this.rank5d = rank5d; }

    public Integer getRank20d() { return rank20d; }
    public void setRank20d(Integer rank20d) { this.rank20d = rank20d; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
}
