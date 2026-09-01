package com.xinling.app.domain.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户挑战实体
 */
@Data
public class UserChallenge implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long userId;
    private Long challengeId;
    private Date joinTime;
    private Integer currentDay;
    private Integer completedDays;
    private Integer status;
    private Date createTime;
    private Date updateTime;

}
