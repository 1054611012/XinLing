package com.xinling.stock.domain.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 基金经理信息实体
 */
public class FundManager implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String managerName;
    private String company;
    private Integer workYears;
    private BigDecimal manageScale;
    private BigDecimal bestReturn;
    private BigDecimal avgReturn1y;
    private BigDecimal avgReturn3y;
    private BigDecimal avgReturn5y;
    private BigDecimal winRate;
    private String style;
    private String description;
    private Integer status;
    private Date createTime;
    private Date updateTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getManagerName() { return managerName; }
    public void setManagerName(String managerName) { this.managerName = managerName; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public Integer getWorkYears() { return workYears; }
    public void setWorkYears(Integer workYears) { this.workYears = workYears; }

    public BigDecimal getManageScale() { return manageScale; }
    public void setManageScale(BigDecimal manageScale) { this.manageScale = manageScale; }

    public BigDecimal getBestReturn() { return bestReturn; }
    public void setBestReturn(BigDecimal bestReturn) { this.bestReturn = bestReturn; }

    public BigDecimal getAvgReturn1y() { return avgReturn1y; }
    public void setAvgReturn1y(BigDecimal avgReturn1y) { this.avgReturn1y = avgReturn1y; }

    public BigDecimal getAvgReturn3y() { return avgReturn3y; }
    public void setAvgReturn3y(BigDecimal avgReturn3y) { this.avgReturn3y = avgReturn3y; }

    public BigDecimal getAvgReturn5y() { return avgReturn5y; }
    public void setAvgReturn5y(BigDecimal avgReturn5y) { this.avgReturn5y = avgReturn5y; }

    public BigDecimal getWinRate() { return winRate; }
    public void setWinRate(BigDecimal winRate) { this.winRate = winRate; }

    public String getStyle() { return style; }
    public void setStyle(String style) { this.style = style; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
}
