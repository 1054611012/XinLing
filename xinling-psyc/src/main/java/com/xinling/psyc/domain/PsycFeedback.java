package com.xinling.psyc.domain;

import java.util.List;

import com.xinling.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.xinling.common.annotation.Excel;

/**
 * 意见反馈对象 psyc_feedback
 *
 * @author xinling
 * @date 2025-11-27
 */
public class PsycFeedback extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 提交反馈的用户ID */
    @Excel(name = "提交反馈的用户ID")
    private Long userId;

    // 不存数据库
    @Excel(name = "用户名称")
    private transient String userName;

    /** 反馈类型：1-Bug问题 2-产品建议 3-投诉 4-功能需求 5-其他 */
    @Excel(name = "反馈类型：1-Bug问题 2-产品建议 3-投诉 4-功能需求 5-其他")
    private Long type;

    /** 反馈内容（用户填写的文本描述） */
    @Excel(name = "反馈内容", readConverterExp = "用=户填写的文本描述")
    private String content;

    /** 截图/图片列表，JSON数组形式 */
    @Excel(name = "截图/图片列表，JSON数组形式")
    private String images;

    /** 用户联系方式（手机号/邮箱） */
    @Excel(name = "用户联系方式", readConverterExp = "手=机号/邮箱")
    private String contact;

    /** 自动收集的设备信息（系统、型号、APP版本等） */
    @Excel(name = "自动收集的设备信息", readConverterExp = "系统、型号、APP版本等")
    private String deviceInfo;

    /** 处理状态：0-待处理 1-处理中 2-已处理 3-已关闭 */
    @Excel(name = "处理状态：0-待处理 1-处理中 2-已处理 3-已关闭")
    private Long status;

    /** 意见反馈的回复记录信息 */
    private List<PsycFeedbackReply> psycFeedbackReplyList;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public Long getUserId()
    {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setType(Long type)
    {
        this.type = type;
    }

    public Long getType()
    {
        return type;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public String getContent()
    {
        return content;
    }

    public void setImages(String images)
    {
        this.images = images;
    }

    public String getImages()
    {
        return images;
    }

    public void setContact(String contact)
    {
        this.contact = contact;
    }

    public String getContact()
    {
        return contact;
    }

    public void setDeviceInfo(String deviceInfo)
    {
        this.deviceInfo = deviceInfo;
    }

    public String getDeviceInfo()
    {
        return deviceInfo;
    }

    public void setStatus(Long status)
    {
        this.status = status;
    }

    public Long getStatus()
    {
        return status;
    }

    public List<PsycFeedbackReply> getPsycFeedbackReplyList()
    {
        return psycFeedbackReplyList;
    }

    public void setPsycFeedbackReplyList(List<PsycFeedbackReply> psycFeedbackReplyList)
    {
        this.psycFeedbackReplyList = psycFeedbackReplyList;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("userId", getUserId())
            .append("type", getType())
            .append("content", getContent())
            .append("images", getImages())
            .append("contact", getContact())
            .append("deviceInfo", getDeviceInfo())
            .append("status", getStatus())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("psycFeedbackReplyList", getPsycFeedbackReplyList())
            .toString();
    }
}
