package com.xinling.stock.mapper;

import com.xinling.stock.domain.entity.SectorFuturesMapping;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 板块↔期货映射 Mapper
 */
public interface SectorFuturesMappingMapper {

    List<SectorFuturesMapping> selectBySectorId(@Param("sectorId") Long sectorId);

    List<SectorFuturesMapping> selectByFuturesId(@Param("futuresId") Long futuresId);

    List<SectorFuturesMapping> selectAll();

    int insert(SectorFuturesMapping mapping);

    int insertBatch(@Param("list") List<SectorFuturesMapping> list);

    int deleteBySectorId(@Param("sectorId") Long sectorId);

    int updateById(SectorFuturesMapping mapping);
}
