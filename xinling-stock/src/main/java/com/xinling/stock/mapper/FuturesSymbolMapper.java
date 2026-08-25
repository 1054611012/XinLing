package com.xinling.stock.mapper;

import com.xinling.stock.domain.entity.FuturesSymbol;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 期货品种定义 Mapper
 */
public interface FuturesSymbolMapper {

    FuturesSymbol selectById(@Param("id") Long id);

    FuturesSymbol selectByCode(@Param("futuresCode") String futuresCode);

    List<FuturesSymbol> selectByCategory(@Param("category") String category);

    List<FuturesSymbol> selectAll();

    int insert(FuturesSymbol symbol);

    int updateById(FuturesSymbol symbol);
}
