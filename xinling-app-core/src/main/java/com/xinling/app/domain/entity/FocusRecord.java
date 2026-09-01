package com.xinling.app.domain.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 专注记录实体
 */
@Data
public class FocusRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long userId;
    private Date startTime;
    private Date endTime;
    private Integer duration;
    private Integer status;
    private String mode;
    private String tag;
    private Integer interruptCount;
    private String note;
    private Long audioMixId;
    private Date createTime;
    private Date updateTime;
    private Integer isDeleted;

}
