package com.xinling.app.domain.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户任务实体
 */
@Data
public class UserTask implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long userId;
    private Long taskId;
    private Date date;
    private Integer progress;
    private Integer status;
    private Date createTime;

}
