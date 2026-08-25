package com.xinling.stock.domain.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 基金持仓(季度重仓股)实体
 */
public class FundHolding implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String fundCode;
    private Date reportDate;
    private String reportType;
    private String stockCode;
    private String stockName;
    private BigDecimal holdAmount;
    private BigDecimal holdRatio;
    private Integer rankNo;
    private String industry;
    private Date createTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFundCode() { return fundCode; }
    public void setFundCode(String fundCode) { this.fundCode = fundCode; }

    public Date getReportDate() { return reportDate; }
    public void setReportDate(Date reportDate) { this.reportDate = reportDate; }

    public String getReportType() { return reportType; }
    public void setReportType(String reportType) { this.reportType = reportType; }

    public String getStockCode() { return stockCode; }
    public void setStockCode(String stockCode) { this.stockCode = stockCode; }

    public String getStockName() { return stockName; }
    public void setStockName(String stockName) { this.stockName = stockName; }

    public BigDecimal getHoldAmount() { return holdAmount; }
    public void setHoldAmount(BigDecimal holdAmount) { this.holdAmount = holdAmount; }

    public BigDecimal getHoldRatio() { return holdRatio; }
    public void setHoldRatio(BigDecimal holdRatio) { this.holdRatio = holdRatio; }

    public Integer getRankNo() { return rankNo; }
    public void setRankNo(Integer rankNo) { this.rankNo = rankNo; }

    public String getIndustry() { return industry; }
    public void setIndustry(String industry) { this.industry = industry; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
}
