package com.xinling.stock.mapper;

import com.xinling.stock.domain.entity.FundHolding;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 基金持仓 Mapper
 */
public interface FundHoldingMapper {

    List<FundHolding> selectByFundAndReport(@Param("fundCode") String fundCode,
                                             @Param("reportDate") Date reportDate);

    List<FundHolding> selectLatestByFund(@Param("fundCode") String fundCode);

    List<FundHolding> selectByStockCode(@Param("stockCode") String stockCode);

    int insert(FundHolding holding);

    int insertBatch(@Param("list") List<FundHolding> list);

    int deleteByFundAndReport(@Param("fundCode") String fundCode, @Param("reportDate") Date reportDate);
}
