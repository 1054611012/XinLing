package com.xinling.app.domain.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 专注设置实体
 */
@Data
public class FocusSettings implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long userId;
    private Integer strictMode;
    private Integer appBlock;
    private String allowedApps;
    private Integer notificationBlock;
    private Integer aiEncouragement;
    private Integer encouragementInterval;
    private Date createTime;
    private Date updateTime;

}
