package com.xinling.stock.mapper;

import com.xinling.stock.domain.entity.SectorStock;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 板块成分股关联 Mapper
 */
public interface SectorStockMapper {

    List<SectorStock> selectBySectorId(@Param("sectorId") Long sectorId);

    List<SectorStock> selectByStockCode(@Param("stockCode") String stockCode);

    List<SectorStock> selectLeadersBySectorId(@Param("sectorId") Long sectorId);

    int insert(SectorStock sectorStock);

    int insertBatch(@Param("list") List<SectorStock> list);

    int deleteBySectorId(@Param("sectorId") Long sectorId);
}
