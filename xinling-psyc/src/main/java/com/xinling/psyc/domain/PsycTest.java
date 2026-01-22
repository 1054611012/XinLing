package com.xinling.psyc.domain;

import java.util.List;

import com.xinling.psyc.domain.vo.PsycQuestionsVo;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.xinling.common.annotation.Excel;
import com.xinling.common.core.domain.BaseEntity;

/**
 * 心理测评对象 psyc_test
 *
 * @author xinling
 * @date 2025-10-28
 */
public class PsycTest extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 测评项目ID */
    private Long id;

    /** 测评项目名称 */
    @Excel(name = "测评项目名称")
    private String testName;

    /** 测评简介 */
    @Excel(name = "测评简介")
    private String description;

    /** 状态：0停用 1启用 */
    @Excel(name = "状态：0停用 1启用")
    private Long status;

    /** 题目数量 */
    @Excel(name = "题目数量")
    private Long totalQuestions;

    /** 测评时长（分钟） */
    @Excel(name = "测评时长", readConverterExp = "分=钟")
    private Long duration;

    /** 测评动态评分区间规则信息 */
    private List<PsycAssessmentRule> psycAssessmentRuleList;

    /** 测评题目信息 */
    private List<PsycQuestions> psycQuestionsList;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }

    public void setTestName(String testName)
    {
        this.testName = testName;
    }

    public String getTestName()
    {
        return testName;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getDescription()
    {
        return description;
    }

    public void setStatus(Long status)
    {
        this.status = status;
    }

    public Long getStatus()
    {
        return status;
    }

    public void setTotalQuestions(Long totalQuestions)
    {
        this.totalQuestions = totalQuestions;
    }

    public Long getTotalQuestions()
    {
        return totalQuestions;
    }

    public void setDuration(Long duration)
    {
        this.duration = duration;
    }

    public Long getDuration()
    {
        return duration;
    }

    public List<PsycAssessmentRule> getPsycAssessmentRuleList()
    {
        return psycAssessmentRuleList;
    }

    public void setPsycAssessmentRuleList(List<PsycAssessmentRule> psycAssessmentRuleList)
    {
        this.psycAssessmentRuleList = psycAssessmentRuleList;
    }

    public List<PsycQuestions> getPsycQuestionsList() {
        return psycQuestionsList;
    }

    public void setPsycQuestionsList(List<PsycQuestions> psycQuestionsList) {
        this.psycQuestionsList = psycQuestionsList;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("testName", getTestName())
            .append("description", getDescription())
            .append("status", getStatus())
            .append("totalQuestions", getTotalQuestions())
            .append("duration", getDuration())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("psycAssessmentRuleList", getPsycAssessmentRuleList())
            .append("psycQuestionsList", getPsycQuestionsList())
            .toString();
    }
}
