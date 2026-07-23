package com.xinling.app.mapper;

import com.xinling.app.domain.entity.UserChallenge;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户挑战 Mapper
 */
public interface UserChallengeMapper {

    UserChallenge selectByUserAndChallenge(@Param("userId") Long userId, @Param("challengeId") Long challengeId);

    List<UserChallenge> selectByUserId(@Param("userId") Long userId);

    int insert(UserChallenge userChallenge);

    int updateById(UserChallenge userChallenge);

    int countByChallengeId(@Param("challengeId") Long challengeId);
}
