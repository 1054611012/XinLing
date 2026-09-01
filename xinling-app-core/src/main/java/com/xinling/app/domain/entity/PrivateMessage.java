package com.xinling.app.domain.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 私信实体
 */
@Data
public class PrivateMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long fromUserId;
    private Long toUserId;
    private String content;
    private Integer isRead;
    private Date createTime;

}
