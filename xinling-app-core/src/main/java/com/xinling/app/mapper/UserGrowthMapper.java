package com.xinling.app.mapper;

import com.xinling.app.domain.entity.UserGrowth;
import org.apache.ibatis.annotations.Param;

/**
 * 用户成长 Mapper
 */
public interface UserGrowthMapper {

    UserGrowth selectByUserId(@Param("userId") Long userId);

    int insert(UserGrowth userGrowth);

    int updateById(UserGrowth userGrowth);
}
