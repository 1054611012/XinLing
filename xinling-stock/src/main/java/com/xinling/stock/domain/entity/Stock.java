package com.xinling.stock.domain.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * A股股票基本信息实体
 */
public class Stock implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String stockCode;
    private String stockName;
    private String market;
    private Long sectorId;
    private String industry;
    private String conceptTags;
    private BigDecimal totalShares;
    private BigDecimal circulatingShares;
    private Date listingDate;
    private Integer status;
    private BigDecimal peTtm;
    private BigDecimal pb;
    private BigDecimal marketCap;
    private Date createTime;
    private Date updateTime;
    private Integer isDeleted;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getStockCode() { return stockCode; }
    public void setStockCode(String stockCode) { this.stockCode = stockCode; }

    public String getStockName() { return stockName; }
    public void setStockName(String stockName) { this.stockName = stockName; }

    public String getMarket() { return market; }
    public void setMarket(String market) { this.market = market; }

    public Long getSectorId() { return sectorId; }
    public void setSectorId(Long sectorId) { this.sectorId = sectorId; }

    public String getIndustry() { return industry; }
    public void setIndustry(String industry) { this.industry = industry; }

    public String getConceptTags() { return conceptTags; }
    public void setConceptTags(String conceptTags) { this.conceptTags = conceptTags; }

    public BigDecimal getTotalShares() { return totalShares; }
    public void setTotalShares(BigDecimal totalShares) { this.totalShares = totalShares; }

    public BigDecimal getCirculatingShares() { return circulatingShares; }
    public void setCirculatingShares(BigDecimal circulatingShares) { this.circulatingShares = circulatingShares; }

    public Date getListingDate() { return listingDate; }
    public void setListingDate(Date listingDate) { this.listingDate = listingDate; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public BigDecimal getPeTtm() { return peTtm; }
    public void setPeTtm(BigDecimal peTtm) { this.peTtm = peTtm; }

    public BigDecimal getPb() { return pb; }
    public void setPb(BigDecimal pb) { this.pb = pb; }

    public BigDecimal getMarketCap() { return marketCap; }
    public void setMarketCap(BigDecimal marketCap) { this.marketCap = marketCap; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }

    public Integer getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Integer isDeleted) { this.isDeleted = isDeleted; }
}
