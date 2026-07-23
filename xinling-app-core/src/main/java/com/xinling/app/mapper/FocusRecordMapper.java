package com.xinling.app.mapper;

import com.xinling.app.domain.entity.FocusRecord;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 专注记录 Mapper
 */
public interface FocusRecordMapper {

    FocusRecord selectById(@Param("id") Long id);

    List<FocusRecord> selectByUserId(@Param("userId") Long userId);

    List<FocusRecord> selectByUserIdAndDateRange(@Param("userId") Long userId,
                                                  @Param("startDate") Date startDate,
                                                  @Param("endDate") Date endDate);

    FocusRecord selectCurrentByUserId(@Param("userId") Long userId);

    int selectTodayTotalMinutes(@Param("userId") Long userId);

    List<FocusRecord> selectList(@Param("userId") Long userId,
                                 @Param("beginTime") String beginTime,
                                 @Param("endTime") String endTime);

    int insert(FocusRecord record);

    int updateById(FocusRecord record);

    int deleteById(@Param("id") Long id);
}
