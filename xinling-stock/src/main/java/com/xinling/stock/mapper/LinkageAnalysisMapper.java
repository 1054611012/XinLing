package com.xinling.stock.mapper;

import com.xinling.stock.domain.entity.LinkageAnalysis;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 期货联动分析结果 Mapper
 */
public interface LinkageAnalysisMapper {

    LinkageAnalysis selectBySectorAndDate(@Param("sectorId") Long sectorId,
                                           @Param("tradeDate") Date tradeDate);

    List<LinkageAnalysis> selectByDate(@Param("tradeDate") Date tradeDate);

    List<LinkageAnalysis> selectBySectorId(@Param("sectorId") Long sectorId,
                                            @Param("limit") int limit);

    int insert(LinkageAnalysis record);

    int updateBySectorAndDate(LinkageAnalysis record);
}
