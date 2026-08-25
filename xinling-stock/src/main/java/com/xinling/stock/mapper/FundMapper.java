package com.xinling.stock.mapper;

import com.xinling.stock.domain.entity.Fund;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 基金基本信息 Mapper
 */
public interface FundMapper {

    Fund selectById(@Param("id") Long id);

    Fund selectByCode(@Param("fundCode") String fundCode);

    List<Fund> selectByType(@Param("fundType") String fundType);

    List<Fund> searchByName(@Param("keyword") String keyword);

    List<Fund> selectAll();

    int insert(Fund fund);

    int insertBatch(@Param("list") List<Fund> list);

    int updateById(Fund fund);
}
