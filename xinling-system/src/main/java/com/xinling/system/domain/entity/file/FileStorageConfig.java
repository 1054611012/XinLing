package com.xinling.system.domain.entity.file;

import com.xinling.common.annotation.Excel;
import com.xinling.common.core.domain.BaseEntity;

/**
 * 文件存储配置表 sys_file_storage_config
 *
 * @author xinling
 */
public class FileStorageConfig extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 配置ID */
    private Long id;

    /** 配置名称 */
    @Excel(name = "配置名称")
    private String name;

    /** 存储类型：local/aliyun-oss/tencent-cos/qiniu/minio */
    @Excel(name = "存储类型")
    private String storageType;

    /** 是否主配置：0否 1是 */
    @Excel(name = "是否主配置")
    private String isMaster;

    /** 服务端点（OSS/COS/MinIO） */
    @Excel(name = "服务端点")
    private String endpoint;

    /** 存储桶名称 */
    @Excel(name = "存储桶名称")
    private String bucketName;

    /** 访问密钥ID */
    @Excel(name = "访问密钥ID")
    private String accessKeyId;

    /** 访问密钥Secret */
    @Excel(name = "访问密钥Secret")
    private String accessKeySecret;

    /** 地域（如：cn-hangzhou） */
    @Excel(name = "地域")
    private String region;

    /** 自定义域名（CDN加速） */
    @Excel(name = "自定义域名")
    private String customDomain;

    /** 基础路径（如：uploads/） */
    @Excel(name = "基础路径")
    private String basePath;

    /** 最大文件大小（字节） */
    @Excel(name = "最大文件大小(字节)")
    private Long maxFileSize;

    /** 允许的文件扩展名 */
    @Excel(name = "允许的文件扩展名")
    private String allowedExtensions;

    /** 状态：0正常 1停用 */
    @Excel(name = "状态")
    private String status;

    /** 排序 */
    @Excel(name = "排序")
    private Integer sortOrder;

    /** 备注 */
    @Excel(name = "备注")
    private String remark;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStorageType() {
        return storageType;
    }

    public void setStorageType(String storageType) {
        this.storageType = storageType;
    }

    public String getIsMaster() {
        return isMaster;
    }

    public void setIsMaster(String isMaster) {
        this.isMaster = isMaster;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    public String getAccessKeySecret() {
        return accessKeySecret;
    }

    public void setAccessKeySecret(String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCustomDomain() {
        return customDomain;
    }

    public void setCustomDomain(String customDomain) {
        this.customDomain = customDomain;
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public Long getMaxFileSize() {
        return maxFileSize;
    }

    public void setMaxFileSize(Long maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    public String getAllowedExtensions() {
        return allowedExtensions;
    }

    public void setAllowedExtensions(String allowedExtensions) {
        this.allowedExtensions = allowedExtensions;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    @Override
    public String getRemark() {
        return remark;
    }

    @Override
    public void setRemark(String remark) {
        this.remark = remark;
    }
}
