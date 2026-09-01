package com.xinling.app.domain.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 支付配置实体
 */
@Data
public class PayConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String payType;
    private String appId;
    private String merchantId;
    private String privateKey;
    private String publicKey;
    private String notifyUrl;
    private Integer status;
    private Date createTime;
    private Date updateTime;

}
