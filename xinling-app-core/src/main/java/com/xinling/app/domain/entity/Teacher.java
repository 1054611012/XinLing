package com.xinling.app.domain.entity;

import lombok.Data;

import java.util.Date;

/**
 * 老师（全局老师库，可跨冥想复用）
 *
 * @author xinling
 */
@Data
public class Teacher {
    private Long id;
    private String name;
    private String avatar;
    private Integer sortOrder;
    private Date createTime;
    private Date updateTime;

}
