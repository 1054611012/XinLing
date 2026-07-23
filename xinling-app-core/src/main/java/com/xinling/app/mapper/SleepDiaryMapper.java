package com.xinling.app.mapper;

import com.xinling.app.domain.entity.SleepDiary;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 睡眠日记 Mapper
 */
public interface SleepDiaryMapper {

    SleepDiary selectById(@Param("id") Long id);

    SleepDiary selectByUserIdAndDate(@Param("userId") Long userId, @Param("date") Date date);

    List<SleepDiary> selectByUserId(@Param("userId") Long userId);

    /**
     * 管理后台查询所有睡眠日记列表
     */
    List<SleepDiary> selectAll(@Param("userId") Long userId);

    int insert(SleepDiary diary);

    int updateById(SleepDiary diary);

    int upsert(SleepDiary diary);
}
