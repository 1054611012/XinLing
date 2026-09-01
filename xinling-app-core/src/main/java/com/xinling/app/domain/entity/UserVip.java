package com.xinling.app.domain.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户会员实体
 */
@Data
public class UserVip implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long userId;
    private Long packageId;
    private String packageName;
    private Date startTime;
    private Date endTime;
    private Integer autoRenew;
    private Date createTime;
    private Date updateTime;

}
