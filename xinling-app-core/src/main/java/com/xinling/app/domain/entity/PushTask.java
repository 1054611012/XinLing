package com.xinling.app.domain.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 推送任务实体
 */
@Data
public class PushTask implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String title;
    private String content;
    private String targetType;
    private String targetIds;
    private Date pushTime;
    private Integer status;
    private Integer successCount;
    private Integer failCount;
    private Date createTime;
    private Date updateTime;

}
