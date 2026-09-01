package com.xinling.app.domain.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 动态实体
 */
@Data
public class Moment implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long userId;
    private String content;
    private String images;
    private String type;
    private String source;
    private Long sourceId;
    private Integer isAnonymous;
    private Integer visibility;
    private Integer likeCount;
    private Integer commentCount;
    private Integer shareCount;
    private Date createTime;
    private Date updateTime;
    private Integer isDeleted;

}
