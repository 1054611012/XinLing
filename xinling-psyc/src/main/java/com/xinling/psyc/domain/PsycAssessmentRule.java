package com.xinling.psyc.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.xinling.common.annotation.Excel;
import com.xinling.common.core.domain.BaseEntity;

/**
 * 测评动态评分区间规则对象 psyc_assessment_rule
 *
 * @author xinling
 * @date 2025-10-28
 */
public class PsycAssessmentRule extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 规则ID */
    private Long id;

    /** 测评ID，关联测评主表 */
    @Excel(name = "测评ID，关联测评主表")
    private Long testId;

    /** 区间下限（含），如：50.00 */
    @Excel(name = "区间下限", readConverterExp = "含=")
    private BigDecimal minScore;

    /** 区间上限（含），如：100.00 */
    @Excel(name = "区间上限", readConverterExp = "含=")
    private BigDecimal maxScore;

    /** 等级名称，如：优秀、良好、一般 */
    @Excel(name = "等级名称，如：优秀、良好、一般")
    private String level;

    /** 建议文案，针对该等级的指导建议 */
    @Excel(name = "建议文案，针对该等级的指导建议")
    private String suggestion;

    /** 参考结果，该等级对应的典型特征、案例说明等补充信息 */
    @Excel(name = "参考结果，该等级对应的典型特征、案例说明等补充信息")
    private String referenceResult;

    /** 优先级，值越大优先匹配（当区间重叠时生效） */
    @Excel(name = "优先级，值越大优先匹配", readConverterExp = "当=区间重叠时生效")
    private Long priority;

    /** 是否删除：0-未删除，1-已删除 */
    @Excel(name = "是否删除：0-未删除，1-已删除")
    private Long isDeleted;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }
    public void setTestId(Long testId)
    {
        this.testId = testId;
    }

    public Long getTestId()
    {
        return testId;
    }
    public void setMinScore(BigDecimal minScore)
    {
        this.minScore = minScore;
    }

    public BigDecimal getMinScore()
    {
        return minScore;
    }
    public void setMaxScore(BigDecimal maxScore)
    {
        this.maxScore = maxScore;
    }

    public BigDecimal getMaxScore()
    {
        return maxScore;
    }
    public void setLevel(String level)
    {
        this.level = level;
    }

    public String getLevel()
    {
        return level;
    }
    public void setSuggestion(String suggestion)
    {
        this.suggestion = suggestion;
    }

    public String getSuggestion()
    {
        return suggestion;
    }
    public void setReferenceResult(String referenceResult)
    {
        this.referenceResult = referenceResult;
    }

    public String getReferenceResult()
    {
        return referenceResult;
    }
    public void setPriority(Long priority)
    {
        this.priority = priority;
    }

    public Long getPriority()
    {
        return priority;
    }
    public void setIsDeleted(Long isDeleted)
    {
        this.isDeleted = isDeleted;
    }

    public Long getIsDeleted()
    {
        return isDeleted;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("testId", getTestId())
            .append("minScore", getMinScore())
            .append("maxScore", getMaxScore())
            .append("level", getLevel())
            .append("suggestion", getSuggestion())
            .append("referenceResult", getReferenceResult())
            .append("priority", getPriority())
            .append("isDeleted", getIsDeleted())
            .toString();
    }
}
