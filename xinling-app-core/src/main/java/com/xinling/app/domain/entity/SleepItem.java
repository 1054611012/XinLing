package com.xinling.app.domain.entity;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 睡眠内容
 *
 * @author xinling
 */
@Data
public class SleepItem {
    private Long id;
    private String title;
    private String description;
    private String coverUrl;
    private Long audioItemId;
    private Integer status;
    private Integer sortOrder;
    private Date createTime;
    private Date updateTime;

    /** 关联的素材详情 */
    private AudioItem audioItem;
    /** 多张背景图 */
    private List<ContentBg> backgroundImages;

}
