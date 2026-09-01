package com.xinling.app.domain.entity;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 冥想内容
 *
 * @author xinling
 */
@Data
public class Meditation {
    private Long id;
    private String title;
    private String description;
    private String coverUrl;
    private Integer status;
    private Integer sortOrder;
    private Date createTime;
    private Date updateTime;

    /** 关联的多条音频素材（含老师信息） */
    private List<MeditationAudio> audioItems;
    /** 多张背景图 */
    private List<ContentBg> backgroundImages;

}
