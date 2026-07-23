package com.xinling.app.mapper;

import com.xinling.app.domain.entity.Challenge;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 挑战活动 Mapper
 */
public interface ChallengeMapper {

    Challenge selectById(@Param("id") Long id);

    List<Challenge> selectAvailable();
}
