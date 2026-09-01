package com.xinling.app.domain.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户成长实体
 */
@Data
public class UserGrowth implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long userId;
    private Integer level;
    private Integer exp;
    private Integer points;
    private Integer continuousFocusDays;
    private Integer continuousSleepDays;
    private Date createTime;
    private Date updateTime;

}
