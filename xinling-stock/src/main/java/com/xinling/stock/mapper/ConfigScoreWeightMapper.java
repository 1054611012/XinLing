package com.xinling.stock.mapper;

import com.xinling.stock.domain.entity.ConfigScoreWeight;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 评分权重配置 Mapper
 */
public interface ConfigScoreWeightMapper {

    List<ConfigScoreWeight> selectAllEnabled();

    ConfigScoreWeight selectByCode(@Param("dimensionCode") String dimensionCode);

    List<ConfigScoreWeight> selectAll();

    int insert(ConfigScoreWeight config);

    int updateById(ConfigScoreWeight config);
}
