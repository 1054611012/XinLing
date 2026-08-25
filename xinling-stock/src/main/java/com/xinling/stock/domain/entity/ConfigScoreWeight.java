package com.xinling.stock.domain.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * AI综合评分权重配置实体
 */
public class ConfigScoreWeight implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String dimensionCode;
    private String dimensionName;
    private BigDecimal weight;
    private Integer enabled;
    private String updateFreq;
    private String description;
    private String scoringRule;
    private Date createTime;
    private Date updateTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDimensionCode() { return dimensionCode; }
    public void setDimensionCode(String dimensionCode) { this.dimensionCode = dimensionCode; }

    public String getDimensionName() { return dimensionName; }
    public void setDimensionName(String dimensionName) { this.dimensionName = dimensionName; }

    public BigDecimal getWeight() { return weight; }
    public void setWeight(BigDecimal weight) { this.weight = weight; }

    public Integer getEnabled() { return enabled; }
    public void setEnabled(Integer enabled) { this.enabled = enabled; }

    public String getUpdateFreq() { return updateFreq; }
    public void setUpdateFreq(String updateFreq) { this.updateFreq = updateFreq; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getScoringRule() { return scoringRule; }
    public void setScoringRule(String scoringRule) { this.scoringRule = scoringRule; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
}
