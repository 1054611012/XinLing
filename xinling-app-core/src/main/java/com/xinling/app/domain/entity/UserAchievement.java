package com.xinling.app.domain.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户成就实体
 */
@Data
public class UserAchievement implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long userId;
    private Long achievementId;
    private Date obtainTime;

}
