package com.xinling.app.domain.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 分销关系实体
 */
public class DistributionRelation implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long userId;
    private Long parentId;
    private Long grandparentId;
    private Date bindTime;
    private Date createTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }

    public Long getGrandparentId() { return grandparentId; }
    public void setGrandparentId(Long grandparentId) { this.grandparentId = grandparentId; }

    public Date getBindTime() { return bindTime; }
    public void setBindTime(Date bindTime) { this.bindTime = bindTime; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
}
