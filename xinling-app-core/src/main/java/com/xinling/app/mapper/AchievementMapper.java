package com.xinling.app.mapper;

import com.xinling.app.domain.entity.Achievement;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 成就 Mapper
 */
public interface AchievementMapper {

    Achievement selectById(@Param("id") Long id);

    List<Achievement> selectAll();

    List<Achievement> selectByConditionType(@Param("conditionType") String conditionType);

    int insert(Achievement achievement);
}
