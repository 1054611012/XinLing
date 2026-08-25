package com.xinling.stock.domain.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 板块定义实体
 */
public class Sector implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String sectorCode;
    private String sectorName;
    private String sectorType;
    private Long parentId;
    private String description;
    private Integer sortOrder;
    private Integer status;
    private Date createTime;
    private Date updateTime;
    private Integer isDeleted;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSectorCode() { return sectorCode; }
    public void setSectorCode(String sectorCode) { this.sectorCode = sectorCode; }

    public String getSectorName() { return sectorName; }
    public void setSectorName(String sectorName) { this.sectorName = sectorName; }

    public String getSectorType() { return sectorType; }
    public void setSectorType(String sectorType) { this.sectorType = sectorType; }

    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }

    public Integer getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Integer isDeleted) { this.isDeleted = isDeleted; }
}
