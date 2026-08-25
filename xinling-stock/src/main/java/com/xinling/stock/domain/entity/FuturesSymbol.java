package com.xinling.stock.domain.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 期货品种定义实体
 */
public class FuturesSymbol implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String futuresCode;
    private String futuresName;
    private String exchange;
    private String category;
    private String unit;
    private Integer multiplier;
    private BigDecimal priceTick;
    private String description;
    private Integer status;
    private Date createTime;
    private Date updateTime;
    private Integer isDeleted;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFuturesCode() { return futuresCode; }
    public void setFuturesCode(String futuresCode) { this.futuresCode = futuresCode; }

    public String getFuturesName() { return futuresName; }
    public void setFuturesName(String futuresName) { this.futuresName = futuresName; }

    public String getExchange() { return exchange; }
    public void setExchange(String exchange) { this.exchange = exchange; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public Integer getMultiplier() { return multiplier; }
    public void setMultiplier(Integer multiplier) { this.multiplier = multiplier; }

    public BigDecimal getPriceTick() { return priceTick; }
    public void setPriceTick(BigDecimal priceTick) { this.priceTick = priceTick; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }

    public Integer getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Integer isDeleted) { this.isDeleted = isDeleted; }
}
