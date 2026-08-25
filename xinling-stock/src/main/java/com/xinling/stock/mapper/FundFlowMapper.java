package com.xinling.stock.mapper;

import com.xinling.stock.domain.entity.FundFlow;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 个股资金流向 Mapper
 */
public interface FundFlowMapper {

    FundFlow selectByStockAndDate(@Param("stockCode") String stockCode,
                                   @Param("tradeDate") Date tradeDate);

    List<FundFlow> selectByStockAndDateRange(@Param("stockCode") String stockCode,
                                              @Param("startDate") Date startDate,
                                              @Param("endDate") Date endDate);

    List<FundFlow> selectByDateOrderByMainInflow(@Param("tradeDate") Date tradeDate,
                                                  @Param("limit") int limit);

    int insert(FundFlow record);

    int insertBatch(@Param("list") List<FundFlow> list);
}
