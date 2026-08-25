package com.xinling.stock.mapper;

import com.xinling.stock.domain.entity.MarketSentiment;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 市场情绪指标 Mapper
 */
public interface MarketSentimentMapper {

    MarketSentiment selectByDate(@Param("tradeDate") Date tradeDate);

    List<MarketSentiment> selectByDateRange(@Param("startDate") Date startDate,
                                             @Param("endDate") Date endDate);

    MarketSentiment selectLatestOne();

    int insert(MarketSentiment record);

    int updateByDate(MarketSentiment record);
}
