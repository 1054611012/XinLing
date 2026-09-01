package com.xinling.app.domain.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 成就实体类
 * 用于存储用户成就信息，包括成就名称、描述、图标、解锁条件及奖励积分
 */
@Data
public class Achievement implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 成就名称
     */
    private String name;

    /**
     * 成就描述
     */
    private String description;

    /**
     * 成就图标路径
     */
    private String icon;

    /**
     * 条件类型（如：答题数、分享次数等）
     */
    private String conditionType;

    /**
     * 条件值（达到该值可解锁成就）
     */
    private Integer conditionValue;

    /**
     * 奖励积分
     */
    private Integer pointsReward;

    /**
     * 创建时间
     */
    private Date createTime;

}
