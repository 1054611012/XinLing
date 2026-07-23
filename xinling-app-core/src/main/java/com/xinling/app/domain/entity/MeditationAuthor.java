package com.xinling.app.domain.entity;

import java.util.Date;

/**
 * 冥想作者
 *
 * @author xinling
 */
public class MeditationAuthor {
    private Long id;
    private Long meditationId;
    /** 关联的音频素材ID（作者可以关联到冥想下的具体某个音频） */
    private Long audioItemId;
    /** 关联的音频素材详情（非DB字段，用于前端展示） */
    private AudioItem audioItem;
    private String name;
    private String avatar;
    private Integer sortOrder;
    private Date createTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getMeditationId() { return meditationId; }
    public void setMeditationId(Long meditationId) { this.meditationId = meditationId; }
    public Long getAudioItemId() { return audioItemId; }
    public void setAudioItemId(Long audioItemId) { this.audioItemId = audioItemId; }
    public AudioItem getAudioItem() { return audioItem; }
    public void setAudioItem(AudioItem audioItem) { this.audioItem = audioItem; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
}
