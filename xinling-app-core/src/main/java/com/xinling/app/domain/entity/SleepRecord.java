package com.xinling.app.domain.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 睡眠记录实体
 */
@Data
public class SleepRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long userId;
    private Date startTime;
    private Date endTime;
    private Integer duration;
    private Integer sleepScore;
    private Integer deepSleepMinutes;
    private Integer lightSleepMinutes;
    private Integer remSleepMinutes;
    private Integer interruptCount;
    private Integer snoringCount;
    private Long audioMixId;
    private Date createTime;
    private Date updateTime;

}
