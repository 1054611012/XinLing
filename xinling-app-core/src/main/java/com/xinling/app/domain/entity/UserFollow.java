package com.xinling.app.domain.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户关注实体
 */
public class UserFollow implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long followerId;
    private Long followingId;
    private Date createTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getFollowerId() { return followerId; }
    public void setFollowerId(Long followerId) { this.followerId = followerId; }

    public Long getFollowingId() { return followingId; }
    public void setFollowingId(Long followingId) { this.followingId = followingId; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
}
