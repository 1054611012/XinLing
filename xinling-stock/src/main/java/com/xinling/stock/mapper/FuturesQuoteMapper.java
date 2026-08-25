package com.xinling.stock.mapper;

import com.xinling.stock.domain.entity.FuturesQuote;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 期货行情 Mapper
 */
public interface FuturesQuoteMapper {

    FuturesQuote selectByFuturesAndDate(@Param("futuresId") Long futuresId,
                                         @Param("tradeDate") Date tradeDate);

    List<FuturesQuote> selectByFuturesAndDateRange(@Param("futuresId") Long futuresId,
                                                    @Param("startDate") Date startDate,
                                                    @Param("endDate") Date endDate);

    List<FuturesQuote> selectBatchByDate(@Param("futuresIds") List<Long> futuresIds,
                                          @Param("tradeDate") Date tradeDate);

    FuturesQuote selectLatestOne(@Param("futuresId") Long futuresId);

    int insert(FuturesQuote record);

    int insertBatch(@Param("list") List<FuturesQuote> list);
}
