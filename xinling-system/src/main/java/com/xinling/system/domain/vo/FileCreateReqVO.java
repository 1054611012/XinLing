package com.xinling.system.domain.vo;


import jakarta.validation.constraints.NotNull;

public class FileCreateReqVO {

    /**
     * 文件配置编号
     */
    @NotNull(message = "文件配置编号不能为空")
    private Long configId;

    /**
     * 文件路径
     */
    @NotNull(message = "文件路径不能为空")
    private String path;

    /**
     * 文件名
     */
    @NotNull(message = "原文件名不能为空")
    private String name;

    /**
     * 文件 URL
     */
    @NotNull(message = "文件 URL不能为空")
    private String url;

    /**
     * 文件MIME类型
     */
    private String type;

    /**
     * 文件大小 单位：字节
     */
    private Integer size;

}
