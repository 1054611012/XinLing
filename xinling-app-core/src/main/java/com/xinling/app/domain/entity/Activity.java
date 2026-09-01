package com.xinling.app.domain.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 活动实体类
 * 用于存储活动信息，包括活动标题、描述、封面、类型、规则、时间范围等
 */
@Data
public class Activity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 活动标题
     */
    private String title;

    /**
     * 活动描述
     */
    private String description;

    /**
     * 活动封面图片路径
     */
    private String cover;

    /**
     * 活动类型
     */
    private String type;

    /**
     * 活动规则
     */
    private String rule;

    /**
     * 活动开始时间
     */
    private Date startTime;

    /**
     * 活动结束时间
     */
    private Date endTime;

    /**
     * 优先级（数字越小优先级越高）
     */
    private Integer priority;

    /**
     * 状态（0-禁用，1-启用）
     */
    private Integer status;

    /**
     * 参与人数
     */
    private Integer joinCount;

    /**
     * 订单数量
     */
    private Integer orderCount;

    /**
     * 总金额
     */
    private BigDecimal totalAmount;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

}
