package com.xinling.psyc.domain;

import java.util.List;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.xinling.common.annotation.Excel;
import com.xinling.common.core.domain.BaseEntity;

/**
 * 动态管理对象 psyc_post
 *
 * @author xinling
 * @date 2025-10-30
 */
public class PsycPost extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 动态ID */
    private Long id;

    /** 发布用户ID */
    @Excel(name = "发布用户ID")
    private Long userId;

    /** 文字内容 */
    @Excel(name = "文字内容")
    private String content;

    /** 可见范围(1公开,2仅好友,3私密) */
    @Excel(name = "可见范围", readConverterExp = "1=公开,2=仅好友,3=私密")
    private Long visible;

    /** 点赞数 */
    @Excel(name = "点赞数")
    private Long likeCount;

    /** 评论数 */
    @Excel(name = "评论数")
    private Long commentCount;

    /** 收藏数 */
    @Excel(name = "收藏数")
    private Long favoriteCount;

    /** 分享数 */
    @Excel(name = "分享数")
    private Long shareCount;

    /** 状态(1正常,0删除,2审核中) */
    @Excel(name = "状态(1正常,0删除,2审核中)")
    private Long status;

    /** 发布时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "发布时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date createdAt;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date updatedAt;

    /** 动态媒体资源信息 */
    private List<PsycPostMedia> psycPostMediaList;

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

    public void setContent(String content)
    {
        this.content = content;
    }

    public String getContent()
    {
        return content;
    }

    public void setVisible(Long visible)
    {
        this.visible = visible;
    }

    public Long getVisible()
    {
        return visible;
    }

    public void setLikeCount(Long likeCount)
    {
        this.likeCount = likeCount;
    }

    public Long getLikeCount()
    {
        return likeCount;
    }

    public void setCommentCount(Long commentCount)
    {
        this.commentCount = commentCount;
    }

    public Long getCommentCount()
    {
        return commentCount;
    }

    public void setFavoriteCount(Long favoriteCount)
    {
        this.favoriteCount = favoriteCount;
    }

    public Long getFavoriteCount()
    {
        return favoriteCount;
    }

    public void setShareCount(Long shareCount)
    {
        this.shareCount = shareCount;
    }

    public Long getShareCount()
    {
        return shareCount;
    }

    public void setStatus(Long status)
    {
        this.status = status;
    }

    public Long getStatus()
    {
        return status;
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

    public List<PsycPostMedia> getPsycPostMediaList()
    {
        return psycPostMediaList;
    }

    public void setPsycPostMediaList(List<PsycPostMedia> psycPostMediaList)
    {
        this.psycPostMediaList = psycPostMediaList;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("userId", getUserId())
            .append("content", getContent())
            .append("visible", getVisible())
            .append("likeCount", getLikeCount())
            .append("commentCount", getCommentCount())
            .append("favoriteCount", getFavoriteCount())
            .append("shareCount", getShareCount())
            .append("status", getStatus())
            .append("createdAt", getCreatedAt())
            .append("updatedAt", getUpdatedAt())
            .append("psycPostMediaList", getPsycPostMediaList())
            .toString();
    }
}
