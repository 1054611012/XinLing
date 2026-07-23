package com.xinling.system.domain.entity.file;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.xinling.common.annotation.Excel;
import com.xinling.common.core.domain.BaseEntity;

import java.util.Date;

/**
 * 文件记录表 sys_file_record
 *
 * @author xinling
 */
public class FileRecord extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 文件ID */
    private Long fileId;

    /** 文件唯一标识(UUID) */
    @Excel(name = "文件UUID")
    private String fileUuid;

    /** 存储配置ID */
    @Excel(name = "存储配置ID")
    private Long configId;

    /** 原始文件名 */
    @Excel(name = "原始文件名")
    private String fileName;

    /** 存储文件名 */
    @Excel(name = "存储文件名")
    private String storedName;

    /** 文件存储路径 */
    @Excel(name = "文件存储路径")
    private String filePath;

    /** 访问URL */
    @Excel(name = "访问URL")
    private String fileUrl;

    /** 文件大小（字节） */
    @Excel(name = "文件大小(字节)")
    private Long fileSize;

    /** 文件MIME类型 */
    @Excel(name = "文件类型")
    private String fileType;

    /** 文件扩展名 */
    @Excel(name = "文件扩展名")
    private String fileExtension;

    /** 文件哈希值（MD5/SHA256） */
    @Excel(name = "文件哈希值")
    private String fileHash;

    /** 存储类型 */
    @Excel(name = "存储类型")
    private String storageType;

    /** 存储桶名称 */
    @Excel(name = "存储桶名称")
    private String bucketName;

    /** 对象存储Key */
    @Excel(name = "对象存储Key")
    private String objectKey;

    /** 缩略图URL */
    @Excel(name = "缩略图URL")
    private String thumbnailUrl;

    /** 图片宽度 */
    @Excel(name = "图片宽度")
    private Integer imageWidth;

    /** 图片高度 */
    @Excel(name = "图片高度")
    private Integer imageHeight;

    /** 媒体时长(秒) */
    @Excel(name = "媒体时长(秒)")
    private Integer duration;

    /** 业务类型（如：avatar/document/attachment） */
    @Excel(name = "业务类型")
    private String businessType;

    /** 业务关联ID */
    @Excel(name = "业务ID")
    private Long businessId;

    /** 业务表名 */
    @Excel(name = "业务表名")
    private String businessTable;

    /** 业务字段 */
    @Excel(name = "业务字段")
    private String businessField;

    /** 来源：UPLOAD/IMPORT/EXPORT/AI/SYSTEM */
    @Excel(name = "来源", readConverterExp = "UPLOAD=上传,IMPORT=导入,EXPORT=导出,AI=AI生成,SYSTEM=系统")
    private String sourceType;

    /** 是否公开访问 */
    @Excel(name = "是否公开", readConverterExp = "0=否,1=是")
    private Integer isPublic;

    /** 权限级别：PUBLIC/PRIVATE/ROLE/DEPT */
    @Excel(name = "权限级别")
    private String accessLevel;

    /** 上传者用户ID */
    @Excel(name = "上传者ID")
    private Long uploaderId;

    /** 上传者用户名 */
    @Excel(name = "上传者")
    private String uploaderName;

    /** 上传者IP地址 */
    @Excel(name = "上传IP")
    private String uploaderIp;

    /** 下载次数 */
    @Excel(name = "下载次数")
    private Integer downloadCount;

    /** 引用次数 */
    @Excel(name = "引用次数")
    private Integer referenceCount;

    /** 状态：0正常 1已删除 */
    @Excel(name = "状态", readConverterExp = "0=正常,1=已删除")
    private String status;

    /** 删除人 */
    private Long deleteBy;

    /** 删除时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date deleteTime;

    /** 过期时间（用于临时文件） */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "过期时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date expireTime;

    /** 租户ID */
    @Excel(name = "租户ID")
    private Long tenantId;

    /** 备注 */
    @Excel(name = "备注")
    private String remark;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;


    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    public String getFileUuid() {
        return fileUuid;
    }

    public void setFileUuid(String fileUuid) {
        this.fileUuid = fileUuid;
    }

    public Long getConfigId() {
        return configId;
    }

    public void setConfigId(Long configId) {
        this.configId = configId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getStoredName() {
        return storedName;
    }

    public void setStoredName(String storedName) {
        this.storedName = storedName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public String getFileHash() {
        return fileHash;
    }

    public void setFileHash(String fileHash) {
        this.fileHash = fileHash;
    }

    public String getStorageType() {
        return storageType;
    }

    public void setStorageType(String storageType) {
        this.storageType = storageType;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getObjectKey() {
        return objectKey;
    }

    public void setObjectKey(String objectKey) {
        this.objectKey = objectKey;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public Integer getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(Integer imageWidth) {
        this.imageWidth = imageWidth;
    }

    public Integer getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(Integer imageHeight) {
        this.imageHeight = imageHeight;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public Long getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Long businessId) {
        this.businessId = businessId;
    }

    public String getBusinessTable() {
        return businessTable;
    }

    public void setBusinessTable(String businessTable) {
        this.businessTable = businessTable;
    }

    public String getBusinessField() {
        return businessField;
    }

    public void setBusinessField(String businessField) {
        this.businessField = businessField;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public Integer getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(Integer isPublic) {
        this.isPublic = isPublic;
    }

    public String getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(String accessLevel) {
        this.accessLevel = accessLevel;
    }

    public Long getUploaderId() {
        return uploaderId;
    }

    public void setUploaderId(Long uploaderId) {
        this.uploaderId = uploaderId;
    }

    public String getUploaderName() {
        return uploaderName;
    }

    public void setUploaderName(String uploaderName) {
        this.uploaderName = uploaderName;
    }

    public String getUploaderIp() {
        return uploaderIp;
    }

    public void setUploaderIp(String uploaderIp) {
        this.uploaderIp = uploaderIp;
    }

    public Integer getDownloadCount() {
        return downloadCount;
    }

    public void setDownloadCount(Integer downloadCount) {
        this.downloadCount = downloadCount;
    }

    public Integer getReferenceCount() {
        return referenceCount;
    }

    public void setReferenceCount(Integer referenceCount) {
        this.referenceCount = referenceCount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getDeleteBy() {
        return deleteBy;
    }

    public void setDeleteBy(Long deleteBy) {
        this.deleteBy = deleteBy;
    }

    public Date getDeleteTime() {
        return deleteTime;
    }

    public void setDeleteTime(Date deleteTime) {
        this.deleteTime = deleteTime;
    }

    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    @Override
    public String getRemark() {
        return remark;
    }

    @Override
    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public Date getCreateTime() {
        return createTime;
    }

    @Override
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public Date getUpdateTime() {
        return updateTime;
    }

    @Override
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
