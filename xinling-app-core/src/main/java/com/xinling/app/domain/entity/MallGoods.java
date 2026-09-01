package com.xinling.app.domain.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 积分商城商品实体
 */
@Data
public class MallGoods implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String description;
    private String cover;
    private String type;
    private Integer price;
    private Integer stock;
    private Integer status;
    private Date createTime;
    private Date updateTime;

}
