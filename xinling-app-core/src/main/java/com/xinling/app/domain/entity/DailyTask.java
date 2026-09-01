package com.xinling.app.domain.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 每日任务实体
 */
@Data
public class DailyTask implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String description;
    private String conditionType;
    private Integer conditionValue;
    private Integer pointsReward;
    private String icon;
    private Date createTime;

}
