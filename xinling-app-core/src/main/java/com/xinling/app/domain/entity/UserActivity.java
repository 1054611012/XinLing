package com.xinling.app.domain.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户活动参与实体
 */
@Data
public class UserActivity implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long userId;
    private Long activityId;
    private Date joinTime;
    private String orderNo;
    private Date createTime;

}
