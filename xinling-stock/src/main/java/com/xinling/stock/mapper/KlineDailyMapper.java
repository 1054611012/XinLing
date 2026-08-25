package com.xinling.stock.mapper;

import com.xinling.stock.domain.entity.KlineDaily;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 日K线数据 Mapper
 */
public interface KlineDailyMapper {

    KlineDaily selectById(@Param("id") Long id);

    KlineDaily selectBySymbolAndDate(@Param("symbolType") String symbolType,
                                     @Param("symbolCode") String symbolCode,
                                     @Param("tradeDate") Date tradeDate);

    List<KlineDaily> selectBySymbolAndDateRange(@Param("symbolType") String symbolType,
                                                 @Param("symbolCode") String symbolCode,
                                                 @Param("startDate") Date startDate,
                                                 @Param("endDate") Date endDate);

    List<KlineDaily> selectLatestBySymbol(@Param("symbolType") String symbolType,
                                           @Param("symbolCode") String symbolCode,
                                           @Param("limit") int limit);

    KlineDaily selectLatestOne(@Param("symbolType") String symbolType,
                                @Param("symbolCode") String symbolCode);

    int insert(KlineDaily record);

    int insertBatch(@Param("list") List<KlineDaily> list);

    int updateBySymbolAndDate(KlineDaily record);

    int deleteBySymbolAndDate(@Param("symbolType") String symbolType,
                               @Param("symbolCode") String symbolCode,
                               @Param("tradeDate") Date tradeDate);

    Date selectMaxTradeDate(@Param("symbolType") String symbolType,
                             @Param("symbolCode") String symbolCode);

    long countBySymbol(@Param("symbolType") String symbolType,
                       @Param("symbolCode") String symbolCode);
}
