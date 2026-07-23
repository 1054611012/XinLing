package com.xinling.app.mapper;

import com.xinling.app.domain.entity.UserFollow;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户关注 Mapper
 */
public interface UserFollowMapper {

    UserFollow selectByFollowerAndFollowing(@Param("followerId") Long followerId, @Param("followingId") Long followingId);

    List<UserFollow> selectFollowers(@Param("userId") Long userId);

    List<UserFollow> selectFollowing(@Param("userId") Long userId);

    int insert(UserFollow userFollow);

    int deleteByFollowerAndFollowing(@Param("followerId") Long followerId, @Param("followingId") Long followingId);

    int countFollowers(@Param("userId") Long userId);

    int countFollowing(@Param("userId") Long userId);
}
