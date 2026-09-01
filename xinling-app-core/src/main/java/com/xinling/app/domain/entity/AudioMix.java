package com.xinling.app.domain.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 音频合集实体类
 * 用于存储音频合集信息，包含多个音频的组合
 */
@Data
public class AudioMix implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 合集名称
     */
    private String name;

    /**
     * 合集描述
     */
    private String description;

    /**
     * 封面图片URL
     */
    private String coverUrl;

    /**
     * 音频ID列表（逗号分隔）
     */
    private String audioIds;

    /**
     * 是否默认合集（0-否，1-是）
     */
    private Integer isDefault;

    /**
     * 排序顺序（数字越小越靠前）
     */
    private Integer sortOrder;

    /**
     * 状态（0-禁用，1-启用）
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除（0-未删除，1-已删除）
     */
    private Integer isDeleted;

}
