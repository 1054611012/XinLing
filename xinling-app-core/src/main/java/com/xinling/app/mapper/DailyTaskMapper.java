package com.xinling.app.mapper;

import com.xinling.app.domain.entity.DailyTask;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 每日任务 Mapper
 */
public interface DailyTaskMapper {

    DailyTask selectById(@Param("id") Long id);

    List<DailyTask> selectAll();

    int insert(DailyTask dailyTask);
}
