package com.xinling.stock.domain.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 板块强度排名快照实体
 */
public class SectorRankSnapshot implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Date tradeDate;
    private Long sectorId;
    private Integer rank1d;
    private Integer rank5d;
    private Integer rank10d;
    private Integer rank20d;
    private Integer rankChange1d;
    private BigDecimal strengthScore;
    private Date createTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Date getTradeDate() { return tradeDate; }
    public void setTradeDate(Date tradeDate) { this.tradeDate = tradeDate; }

    public Long getSectorId() { return sectorId; }
    public void setSectorId(Long sectorId) { this.sectorId = sectorId; }

    public Integer getRank1d() { return rank1d; }
    public void setRank1d(Integer rank1d) { this.rank1d = rank1d; }

    public Integer getRank5d() { return rank5d; }
    public void setRank5d(Integer rank5d) { this.rank5d = rank5d; }

    public Integer getRank10d() { return rank10d; }
    public void setRank10d(Integer rank10d) { this.rank10d = rank10d; }

    public Integer getRank20d() { return rank20d; }
    public void setRank20d(Integer rank20d) { this.rank20d = rank20d; }

    public Integer getRankChange1d() { return rankChange1d; }
    public void setRankChange1d(Integer rankChange1d) { this.rankChange1d = rankChange1d; }

    public BigDecimal getStrengthScore() { return strengthScore; }
    public void setStrengthScore(BigDecimal strengthScore) { this.strengthScore = strengthScore; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
}
