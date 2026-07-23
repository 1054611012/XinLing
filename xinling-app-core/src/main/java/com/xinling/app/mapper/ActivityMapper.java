package com.xinling.app.mapper;

import com.xinling.app.domain.entity.Activity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 活动 Mapper
 */
public interface ActivityMapper {

    Activity selectById(@Param("id") Long id);

    List<Activity> selectList(@Param("title") String title,
                              @Param("type") String type,
                              @Param("status") Integer status);

    List<Activity> selectActiveList();

    int insert(Activity activity);

    int updateById(Activity activity);

    int incrementJoinCount(@Param("id") Long id);
}
