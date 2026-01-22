package com.xinling.system.domain.vo;

import java.time.LocalDateTime;

public class FileRespVO {

    /**
     * 文件编号
     */
    private Long id;

    /**
     * 冗余字段，用于前端展示使用（配置编号）
     */
    private Long configId;

    /**
     * 冗余字段，用于前端展示使用
     */
    private String path;

    /** 文件名 */
    private String name;

    /** 文件 URL */
    private String url;

    /**
     * 文件MIME类型
     */
    private String type;

    /**文件大小 单位：字节 */
    private Integer size;

    /** 时间格式：2021-01-01 00:00:00 */
    private LocalDateTime createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getConfigId() {
        return configId;
    }

    public void setConfigId(Long configId) {
        this.configId = configId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
