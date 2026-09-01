package com.xinling.app.domain.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 评论实体
 */
@Data
public class MomentComment implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long userId;
    private Long momentId;
    private Long parentId;
    private String content;
    private String nickname;
    private String avatar;
    private Integer likeCount;
    private Date createTime;
    private Integer isDeleted;

}
