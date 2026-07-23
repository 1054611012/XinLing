package com.xinling.app.mapper;

import com.xinling.app.domain.entity.SleepItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 睡眠内容 Mapper
 *
 * @author xinling
 */
public interface SleepItemMapper {
    SleepItem selectById(@Param("id") Long id);
    List<SleepItem> selectList(@Param("keyword") String keyword, @Param("status") Integer status);
    int insert(SleepItem item);
    int updateById(SleepItem item);
    int deleteById(@Param("id") Long id);
}
