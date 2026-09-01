package com.xinling.app.domain.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 会员赠送规则实体
 */
@Data
public class VipGiftRule implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String ruleName;
    private String ruleType;
    private String conditionValue;
    private Integer vipDays;
    private Integer autoGrant;
    private Integer totalLimit;
    private Integer grantedCount;
    private Integer dailyLimit;
    private Integer status;
    private String description;
    private Integer sortOrder;
    private Date createTime;
    private Date updateTime;

}
