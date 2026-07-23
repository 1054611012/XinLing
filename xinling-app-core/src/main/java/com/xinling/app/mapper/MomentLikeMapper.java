package com.xinling.app.mapper;

import com.xinling.app.domain.entity.MomentLike;
import org.apache.ibatis.annotations.Param;

/**
 * 点赞 Mapper
 */
public interface MomentLikeMapper {

    MomentLike selectByUserAndMoment(@Param("userId") Long userId, @Param("momentId") Long momentId);

    int insert(MomentLike momentLike);

    int deleteByUserAndMoment(@Param("userId") Long userId, @Param("momentId") Long momentId);
}
