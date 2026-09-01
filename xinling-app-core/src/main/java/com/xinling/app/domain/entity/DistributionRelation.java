package com.xinling.app.domain.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 分销关系实体
 */
@Data
public class DistributionRelation implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long userId;
    private Long parentId;
    private Long grandparentId;
    private Date bindTime;
    private Date createTime;

}
