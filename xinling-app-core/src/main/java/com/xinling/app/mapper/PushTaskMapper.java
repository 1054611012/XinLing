package com.xinling.app.mapper;

import com.xinling.app.domain.entity.PushTask;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 推送任务 Mapper
 */
public interface PushTaskMapper {

    PushTask selectById(@Param("id") Long id);

    List<PushTask> selectList();

    int insert(PushTask pushTask);

    int updateById(PushTask pushTask);
}
