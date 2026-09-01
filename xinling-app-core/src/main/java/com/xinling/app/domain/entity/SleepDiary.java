package com.xinling.app.domain.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 睡眠日记实体
 */
@Data
public class SleepDiary implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long userId;
    private Date date;
    private String bedtimeActivity;
    private Integer caffeineIntake;
    private Integer exercise;
    private String emotion;
    private String note;
    private Date createTime;

}
