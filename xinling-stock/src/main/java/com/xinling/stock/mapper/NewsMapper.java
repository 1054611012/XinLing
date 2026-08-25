package com.xinling.stock.mapper;

import com.xinling.stock.domain.entity.News;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 新闻/资讯 Mapper
 */
public interface NewsMapper {

    News selectById(@Param("id") Long id);

    List<News> selectByStockCode(@Param("stockCode") String stockCode,
                                  @Param("limit") int limit);

    List<News> selectBySectorId(@Param("sectorId") Long sectorId,
                                 @Param("limit") int limit);

    List<News> selectByDateRange(@Param("startTime") Date startTime,
                                  @Param("endTime") Date endTime,
                                  @Param("newsType") String newsType,
                                  @Param("importance") Integer importance);

    List<News> selectRecentImportant(@Param("limit") int limit);

    int insert(News news);

    int insertBatch(@Param("list") List<News> list);

    int updateSentiment(@Param("id") Long id,
                         @Param("sentiment") Integer sentiment,
                         @Param("sentimentScore") java.math.BigDecimal sentimentScore);
}
