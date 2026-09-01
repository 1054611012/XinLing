package com.xinling.app.domain.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 白名单实体
 */
@Data
public class Whitelist implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String type;
    private String identifier;
    private String description;
    private Integer status;
    private Date expireTime;
    private Date createTime;
    private Date updateTime;

}
