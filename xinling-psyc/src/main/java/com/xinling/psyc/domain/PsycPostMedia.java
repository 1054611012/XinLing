package com.xinling.psyc.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.xinling.common.annotation.Excel;
import com.xinling.common.core.domain.BaseEntity;

/**
 * 动态媒体资源对象 psyc_post_media
 *
 * @author xinling
 * @date 2025-10-30
 */
public class PsycPostMedia extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Long id;

    /** 动态ID */
    @Excel(name = "动态ID")
    private Long postId;

    /** 媒体URL */
    @Excel(name = "媒体URL")
    private String mediaUrl;

    /** 类型(1图片,2视频) */
    @Excel(name = "类型(1图片,2视频)")
    private Long mediaType;

    /** 顺序 */
    @Excel(name = "顺序")
    private Long sortOrder;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }
    public void setPostId(Long postId)
    {
        this.postId = postId;
    }

    public Long getPostId()
    {
        return postId;
    }
    public void setMediaUrl(String mediaUrl)
    {
        this.mediaUrl = mediaUrl;
    }

    public String getMediaUrl()
    {
        return mediaUrl;
    }
    public void setMediaType(Long mediaType)
    {
        this.mediaType = mediaType;
    }

    public Long getMediaType()
    {
        return mediaType;
    }
    public void setSortOrder(Long sortOrder)
    {
        this.sortOrder = sortOrder;
    }

    public Long getSortOrder()
    {
        return sortOrder;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("postId", getPostId())
            .append("mediaUrl", getMediaUrl())
            .append("mediaType", getMediaType())
            .append("sortOrder", getSortOrder())
            .toString();
    }
}
