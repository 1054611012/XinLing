package com.xinling.stock.mapper;

import com.xinling.stock.domain.entity.FundNav;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 基金净值历史 Mapper
 */
public interface FundNavMapper {

    FundNav selectByFundAndDate(@Param("fundCode") String fundCode, @Param("navDate") Date navDate);

    List<FundNav> selectByFundAndDateRange(@Param("fundCode") String fundCode,
                                            @Param("startDate") Date startDate,
                                            @Param("endDate") Date endDate);

    List<FundNav> selectLatestByFund(@Param("fundCode") String fundCode, @Param("limit") int limit);

    FundNav selectLatestOne(@Param("fundCode") String fundCode);

    Date selectMaxNavDate(@Param("fundCode") String fundCode);

    int insert(FundNav nav);

    int insertBatch(@Param("list") List<FundNav> list);
}
