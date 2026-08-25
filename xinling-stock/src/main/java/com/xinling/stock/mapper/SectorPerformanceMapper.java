package com.xinling.stock.mapper;

import com.xinling.stock.domain.entity.SectorPerformance;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 板块每日表现 Mapper
 */
public interface SectorPerformanceMapper {

    SectorPerformance selectBySectorAndDate(@Param("sectorId") Long sectorId,
                                             @Param("tradeDate") Date tradeDate);

    List<SectorPerformance> selectBySectorAndDateRange(@Param("sectorId") Long sectorId,
                                                        @Param("startDate") Date startDate,
                                                        @Param("endDate") Date endDate);

    List<SectorPerformance> selectByDateOrderByChange(@Param("tradeDate") Date tradeDate,
                                                       @Param("limit") int limit);

    List<SectorPerformance> selectTopRankByDate(@Param("tradeDate") Date tradeDate,
                                                 @Param("limit") int limit);

    int insert(SectorPerformance record);

    int insertBatch(@Param("list") List<SectorPerformance> list);

    int updateBySectorAndDate(SectorPerformance record);
}
