package com.xinling.app.domain.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 音频播放历史实体类
 * 记录用户的音频播放记录，用于统计播放时长和播放次数
 */
@Data
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

}
