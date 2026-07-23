package com.xinling.app.mapper;

import com.xinling.app.domain.entity.Meditation;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 冥想内容 Mapper
 *
 * @author xinling
 */
public interface MeditationMapper {
    Meditation selectById(@Param("id") Long id);
    List<Meditation> selectList(@Param("keyword") String keyword, @Param("status") Integer status);
    int insert(Meditation meditation);
    int updateById(Meditation meditation);
    int deleteById(@Param("id") Long id);
}
