package com.xinling.app.domain.entity;

import lombok.Data;

/**
 * 冥想-素材关联（含关联老师）
 *
 * @author xinling
 */
@Data
public class MeditationAudio {
    private Long id;
    private Long meditationId;
    private Long audioItemId;
    /** 关联老师ID（null=纯背景音乐，无指定老师） */
    private Long authorId;
    private Integer sortOrder;

    /** 关联的素材详情（非 DB 字段，关联查询填充） */
    private AudioItem audioItem;
    /** 关联的老师信息（非 DB 字段，关联查询填充） */
    private Teacher teacher;

}
