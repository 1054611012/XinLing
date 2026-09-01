package com.xinling.app.domain.entity;

import lombok.Data;

import java.util.Date;

/**
 * 内容背景图（冥想/睡眠/白噪音共用）
 *
 * @author xinling
 */
@Data
public class ContentBg {
    private Long id;
    private String contentType;  // meditation / sleep / white_noise
    private Long contentId;
    private String url;
    private Integer sortOrder;
    private Date createTime;

}
