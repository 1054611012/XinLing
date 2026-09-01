package com.xinling.app.domain.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户兑换记录实体
 */
@Data
public class UserExchange implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long userId;
    private Long goodsId;
    private Integer points;
    private Date createTime;

}
