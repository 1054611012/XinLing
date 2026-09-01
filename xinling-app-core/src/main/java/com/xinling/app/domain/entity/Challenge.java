package com.xinling.app.domain.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 挑战活动实体
 */
@Data
public class Challenge implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String title;
    private String description;
    private String cover;
    private String type;
    private Integer duration;
    private Integer conditionValue;
    private Integer pointsReward;
    private Integer vipDaysReward;
    private Long badgeId;
    private Date startTime;
    private Date endTime;
    private Integer status;
    private Date createTime;

}
