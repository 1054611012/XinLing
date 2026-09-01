package com.xinling.app.domain.entity;

import lombok.Data;

import java.util.Date;

/**
 * 冥想作者
 *
 * @author xinling
 */
@Data
public class MeditationAuthor {
    private Long id;
    private Long meditationId;
    /** 关联的音频素材ID（作者可以关联到冥想下的具体某个音频） */
    private Long audioItemId;
    /** 关联的音频素材详情（非DB字段，用于前端展示） */
    private AudioItem audioItem;
    private String name;
    private String avatar;
    private Integer sortOrder;
    private Date createTime;

}
