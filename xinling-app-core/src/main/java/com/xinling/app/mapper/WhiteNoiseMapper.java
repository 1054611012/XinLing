package com.xinling.app.mapper;

import com.xinling.app.domain.entity.WhiteNoise;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 白噪音内容 Mapper
 *
 * @author xinling
 */
public interface WhiteNoiseMapper {
    WhiteNoise selectById(@Param("id") Long id);
    List<WhiteNoise> selectList(@Param("keyword") String keyword, @Param("status") Integer status);
    int insert(WhiteNoise whiteNoise);
    int updateById(WhiteNoise whiteNoise);
    int deleteById(@Param("id") Long id);
}
