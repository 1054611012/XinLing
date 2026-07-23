package com.xinling.app.domain.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 音频播放历史实体类
 * 记录用户的音频播放记录，用于统计播放时长和播放次数
 */
public class AudioPlayHistory implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 音频ID
     */
    private Long audioId;

    /**
     * 已播放时长（秒）
     */
    private Integer playedDuration;

    /**
     * 创建时间
     */
    private Date createTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getAudioId() { return audioId; }
    public void setAudioId(Long audioId) { this.audioId = audioId; }
    public Integer getPlayedDuration() { return playedDuration; }
    public void setPlayedDuration(Integer playedDuration) { this.playedDuration = playedDuration; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
}
