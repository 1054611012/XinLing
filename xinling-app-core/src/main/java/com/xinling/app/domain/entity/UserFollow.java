package com.xinling.app.domain.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户关注实体
 */
@Data
public class UserFollow implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long followerId;
    private Long followingId;
    private Date createTime;

}
