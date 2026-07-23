package com.xinling.psyc.domain;

import com.xinling.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.xinling.common.annotation.Excel;

/**
 * 意见反馈的回复记录对象 psyc_feedback_reply
 *
 * @author xinling
 * @date 2025-11-27
 */
public class PsycFeedbackReply extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 回复记录主键ID */
    private Long id;

    /** 关联的反馈ID（外键） */
    @Excel(name = "关联的反馈ID", readConverterExp = "外=键")
    private Long feedbackId;

    /** 回复的管理员ID */
    @Excel(name = "回复的管理员ID")
    private Long replyUserId;

    /** 回复内容 */
    @Excel(name = "回复内容")
    private String content;

    @Excel(name = "回复用户名称")
    private transient String replyUserName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(Long feedbackId) {
        this.feedbackId = feedbackId;
    }

    public Long getReplyUserId() {
        return replyUserId;
    }

    public void setReplyUserId(Long replyUserId) {
        this.replyUserId = replyUserId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getReplyUserName() {
        return replyUserName;
    }

    public void setReplyUserName(String replyUserName) {
        this.replyUserName = replyUserName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("feedbackId", getFeedbackId())
            .append("replyUserId", getReplyUserId())
            .append("replyUserName", getReplyUserName())
            .append("content", getContent())
            .append("createTime", getCreateTime())
            .toString();
    }
}
