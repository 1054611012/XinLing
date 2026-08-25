package com.xinling.stock.mapper;

import com.xinling.stock.domain.entity.TechnicalIndicator;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 技术指标计算结果 Mapper
 */
public interface TechnicalIndicatorMapper {

    TechnicalIndicator selectBySymbolAndDate(@Param("symbolType") String symbolType,
                                              @Param("symbolCode") String symbolCode,
                                              @Param("tradeDate") Date tradeDate);

    List<TechnicalIndicator> selectBySymbolAndDateRange(@Param("symbolType") String symbolType,
                                                         @Param("symbolCode") String symbolCode,
                                                         @Param("startDate") Date startDate,
                                                         @Param("endDate") Date endDate);

    List<TechnicalIndicator> selectLatestBySymbol(@Param("symbolType") String symbolType,
                                                   @Param("symbolCode") String symbolCode,
                                                   @Param("limit") int limit);

    TechnicalIndicator selectLatestOne(@Param("symbolType") String symbolType,
                                        @Param("symbolCode") String symbolCode);

    int insert(TechnicalIndicator record);

    int insertBatch(@Param("list") List<TechnicalIndicator> list);

    int updateBySymbolAndDate(TechnicalIndicator record);

    int deleteBySymbolAndDate(@Param("symbolType") String symbolType,
                               @Param("symbolCode") String symbolCode,
                               @Param("tradeDate") Date tradeDate);
}
