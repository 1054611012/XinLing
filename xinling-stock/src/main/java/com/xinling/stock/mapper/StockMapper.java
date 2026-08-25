package com.xinling.stock.mapper;

import com.xinling.stock.domain.entity.Stock;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 股票基本信息 Mapper
 */
public interface StockMapper {

    Stock selectById(@Param("id") Long id);

    Stock selectByCode(@Param("stockCode") String stockCode);

    List<Stock> selectBatchByCodes(@Param("codes") List<String> codes);

    List<Stock> selectList(@Param("stockName") String stockName,
                           @Param("market") String market,
                           @Param("sectorId") Long sectorId,
                           @Param("status") Integer status);

    List<Stock> selectBySectorId(@Param("sectorId") Long sectorId);

    int insert(Stock stock);

    int insertBatch(@Param("list") List<Stock> list);

    int updateById(Stock stock);

    int deleteById(@Param("id") Long id);
}
