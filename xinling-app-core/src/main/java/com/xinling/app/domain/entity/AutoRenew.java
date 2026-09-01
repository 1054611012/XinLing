package com.xinling.app.domain.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 自动续费实体
 */
@Data
public class AutoRenew implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long userId;
    private Long packageId;
    private String payType;
    private String agreementId;
    private Date nextPayTime;
    private Integer status;
    private Date createTime;
    private Date updateTime;

}
