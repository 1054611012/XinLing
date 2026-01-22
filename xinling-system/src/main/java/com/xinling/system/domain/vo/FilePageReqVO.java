package com.xinling.system.domain.vo;

import com.xinling.common.core.domain.BaseEntity;

import java.time.LocalDateTime;


public class FilePageReqVO extends BaseEntity {

    /**
     * 文件路径，模糊匹配
     */
    private String path;

    /**
     * 文件类型，模糊匹配
     */
    private String type;

    /**
     * 创建时间
     */
    private LocalDateTime[] createTime;

}
