package com.xinling.app.domain.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 通知消息实体
 */
@Data
public class Notification implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long userId;
    private String type;
    private String title;
    private String content;
    private String targetType;
    private Long targetId;
    private Integer isRead;
    private Date createTime;

}
