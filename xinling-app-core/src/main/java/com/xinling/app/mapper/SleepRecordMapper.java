package com.xinling.app.mapper;

import com.xinling.app.domain.entity.SleepRecord;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 睡眠记录 Mapper
 */
public interface SleepRecordMapper {

    SleepRecord selectById(@Param("id") Long id);

    List<SleepRecord> selectByUserId(@Param("userId") Long userId);

    SleepRecord selectCurrentByUserId(@Param("userId") Long userId);

    SleepRecord selectByUserIdAndDate(@Param("userId") Long userId, @Param("date") Date date);

    int selectTodayTotalMinutes(@Param("userId") Long userId);

    List<SleepRecord> selectList(@Param("userId") Long userId,
                                 @Param("beginTime") String beginTime,
                                 @Param("endTime") String endTime);

    int insert(SleepRecord record);

    int updateById(SleepRecord record);

    int deleteById(@Param("id") Long id);
}
