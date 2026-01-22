package com.xinling.psyc.domain;

import java.util.List;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.xinling.common.annotation.Excel;
import com.xinling.common.core.domain.BaseEntity;

/**
 * 题目对象 psyc_questions
 *
 * @author xinling
 * @date 2025-10-29
 */
public class PsycQuestions extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 题目ID */
    private Long id;

    /** 题目内容 */
    @Excel(name = "题目内容")
    private String content;

    /** 题目类型：single-单选题，multiple-多选题，judgment-判断题，fill-填空题，essay-简答题 */
    @Excel(name = "题目类型：single-单选题，multiple-多选题，judgment-判断题，fill-填空题，essay-简答题")
    private String type;

    /** 测试ID */
    @Excel(name = "测试ID")
    private Long testId;

    /** 难度：easy-简单，medium-中等，hard-困难 */
    @Excel(name = "难度：easy-简单，medium-中等，hard-困难")
    private String difficulty;

    /** 解析内容 */
    @Excel(name = "解析内容")
    private String analysis;

    /** 题目来源 */
    @Excel(name = "题目来源")
    private String source;

    /** 创建人ID */
    @Excel(name = "创建人ID")
    private Long createdBy;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date createdAt;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date updatedAt;

    /** 删除时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "删除时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date deletedAt;

    /** 题目选项信息 */
    private List<PsycOptions> psycOptionsList;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public String getContent()
    {
        return content;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getType()
    {
        return type;
    }

    public void setTestId(Long testId)
    {
        this.testId = testId;
    }

    public Long getTestId()
    {
        return testId;
    }

    public void setDifficulty(String difficulty)
    {
        this.difficulty = difficulty;
    }

    public String getDifficulty()
    {
        return difficulty;
    }

    public void setAnalysis(String analysis)
    {
        this.analysis = analysis;
    }

    public String getAnalysis()
    {
        return analysis;
    }

    public void setSource(String source)
    {
        this.source = source;
    }

    public String getSource()
    {
        return source;
    }

    public void setCreatedBy(Long createdBy)
    {
        this.createdBy = createdBy;
    }

    public Long getCreatedBy()
    {
        return createdBy;
    }

    public void setCreatedAt(Date createdAt)
    {
        this.createdAt = createdAt;
    }

    public Date getCreatedAt()
    {
        return createdAt;
    }

    public void setUpdatedAt(Date updatedAt)
    {
        this.updatedAt = updatedAt;
    }

    public Date getUpdatedAt()
    {
        return updatedAt;
    }

    public void setDeletedAt(Date deletedAt)
    {
        this.deletedAt = deletedAt;
    }

    public Date getDeletedAt()
    {
        return deletedAt;
    }

    public List<PsycOptions> getPsycOptionsList()
    {
        return psycOptionsList;
    }

    public void setPsycOptionsList(List<PsycOptions> psycOptionsList)
    {
        this.psycOptionsList = psycOptionsList;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("content", getContent())
            .append("type", getType())
            .append("testId", getTestId())
            .append("difficulty", getDifficulty())
            .append("analysis", getAnalysis())
            .append("source", getSource())
            .append("createdBy", getCreatedBy())
            .append("createdAt", getCreatedAt())
            .append("updatedAt", getUpdatedAt())
            .append("deletedAt", getDeletedAt())
            .append("psycOptionsList", getPsycOptionsList())
            .toString();
    }
}
