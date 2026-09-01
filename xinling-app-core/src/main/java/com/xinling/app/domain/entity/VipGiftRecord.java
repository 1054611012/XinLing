package com.xinling.app.domain.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 会员赠送记录实体
 */
@Data
public class VipGiftRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long ruleId;
    private Long userId;
    private String userNickname;
    private String grantType;
    private Integer vipDays;
    private String reason;
    private Long operatorId;
    private String operatorName;
    private Date expireTime;
    private Integer status;
    private Date createTime;

}
