package com.xinling.app.mapper;

import com.xinling.app.domain.entity.UserTask;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 用户任务 Mapper
 */
public interface UserTaskMapper {

    UserTask selectByUserTaskAndDate(@Param("userId") Long userId, @Param("taskId") Long taskId, @Param("date") Date date);

    List<UserTask> selectByUserIdAndDate(@Param("userId") Long userId, @Param("date") Date date);

    int insert(UserTask userTask);

    int updateById(UserTask userTask);
}
