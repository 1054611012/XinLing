package com.xinling.stock.domain.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 基金净值历史实体
 */
public class FundNav implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String fundCode;
    private Date navDate;
    private BigDecimal unitNav;
    private BigDecimal totalNav;
    private BigDecimal dailyChangePct;
    private Date createTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFundCode() { return fundCode; }
    public void setFundCode(String fundCode) { this.fundCode = fundCode; }

    public Date getNavDate() { return navDate; }
    public void setNavDate(Date navDate) { this.navDate = navDate; }

    public BigDecimal getUnitNav() { return unitNav; }
    public void setUnitNav(BigDecimal unitNav) { this.unitNav = unitNav; }

    public BigDecimal getTotalNav() { return totalNav; }
    public void setTotalNav(BigDecimal totalNav) { this.totalNav = totalNav; }

    public BigDecimal getDailyChangePct() { return dailyChangePct; }
    public void setDailyChangePct(BigDecimal dailyChangePct) { this.dailyChangePct = dailyChangePct; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
}
