package com.xinling.stock.mapper;

import com.xinling.stock.domain.entity.Sector;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 板块定义 Mapper
 */
public interface SectorMapper {

    Sector selectById(@Param("id") Long id);

    Sector selectByCode(@Param("sectorCode") String sectorCode);

    List<Sector> selectByType(@Param("sectorType") String sectorType);

    List<Sector> selectAll();

    List<Sector> searchByName(@Param("keyword") String keyword);

    int insert(Sector sector);

    int updateById(Sector sector);

    int deleteById(@Param("id") Long id);
}
