package com.xinling.psyc.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.xinling.common.annotation.Excel;
import com.xinling.common.core.domain.BaseEntity;

/**
 * 题目选项对象 psyc_options
 *
 * @author xinling
 * @date 2025-10-29
 */
public class PsycOptions extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 选项ID */
    private Long id;

    /** 题目ID */
    @Excel(name = "题目ID")
    private Long questionId;

    /** 选项内容 */
    @Excel(name = "选项内容")
    private String content;

    /** 选项分值 */
    @Excel(name = "选项分值")
    private BigDecimal score;

    /** 排序顺序 */
    @Excel(name = "排序顺序")
    private Long sortOrder;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date createdAt;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date updatedAt;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }
    public void setQuestionId(Long questionId)
    {
        this.questionId = questionId;
    }

    public Long getQuestionId()
    {
        return questionId;
    }
    public void setContent(String content)
    {
        this.content = content;
    }

    public String getContent()
    {
        return content;
    }
    public void setScore(BigDecimal score)
    {
        this.score = score;
    }

    public BigDecimal getScore()
    {
        return score;
    }
    public void setSortOrder(Long sortOrder)
    {
        this.sortOrder = sortOrder;
    }

    public Long getSortOrder()
    {
        return sortOrder;
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

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("questionId", getQuestionId())
            .append("content", getContent())
            .append("score", getScore())
            .append("sortOrder", getSortOrder())
            .append("createdAt", getCreatedAt())
            .append("updatedAt", getUpdatedAt())
            .toString();
    }
}
