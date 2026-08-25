package com.xinling.stock.domain.vo;

import java.math.BigDecimal;

/**
 * 单维度评分 VO
 */
public class ScoreDimensionVO {

    /** 维度编码: sector_trend */
    private String dimensionCode;

    /** 维度名称: 板块趋势 */
    private String dimensionName;

    /** 权重 */
    private BigDecimal weight;

    /** 得分(0-100) */
    private BigDecimal score;

    /** 加权得分 */
    private BigDecimal weightedScore;

    /** 评分说明 */
    private String description;

    /** 信号: positive / neutral / negative */
    private String signal;

    public String getDimensionCode() { return dimensionCode; }
    public void setDimensionCode(String dimensionCode) { this.dimensionCode = dimensionCode; }

    public String getDimensionName() { return dimensionName; }
    public void setDimensionName(String dimensionName) { this.dimensionName = dimensionName; }

    public BigDecimal getWeight() { return weight; }
    public void setWeight(BigDecimal weight) { this.weight = weight; }

    public BigDecimal getScore() { return score; }
    public void setScore(BigDecimal score) { this.score = score; }

    public BigDecimal getWeightedScore() { return weightedScore; }
    public void setWeightedScore(BigDecimal weightedScore) { this.weightedScore = weightedScore; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getSignal() { return signal; }
    public void setSignal(String signal) { this.signal = signal; }
}
