package com.xinling.app.domain.entity;

import java.util.Date;
import java.util.List;

/**
 * 白噪音内容
 *
 * @author xinling
 */
public class WhiteNoise {
    private Long id;
    private String title;
    private String description;
    private String coverUrl;
    private Long audioItemId;
    private Integer status;
    private Integer sortOrder;
    private Date createTime;
    private Date updateTime;

    /** 关联的素材详情 */
    private AudioItem audioItem;
    /** 多张背景图 */
    private List<ContentBg> backgroundImages;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCoverUrl() { return coverUrl; }
    public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }
    public Long getAudioItemId() { return audioItemId; }
    public void setAudioItemId(Long audioItemId) { this.audioItemId = audioItemId; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
    public AudioItem getAudioItem() { return audioItem; }
    public void setAudioItem(AudioItem audioItem) { this.audioItem = audioItem; }
    public List<ContentBg> getBackgroundImages() { return backgroundImages; }
    public void setBackgroundImages(List<ContentBg> backgroundImages) { this.backgroundImages = backgroundImages; }
}
