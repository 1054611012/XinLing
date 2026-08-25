package com.xinling.stock.mapper;

import com.xinling.stock.domain.entity.FundPerformance;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 基金阶段表现 Mapper
 */
public interface FundPerformanceMapper {

    FundPerformance selectByFundAndDate(@Param("fundCode") String fundCode, @Param("statDate") Date statDate);

    FundPerformance selectLatestByFund(@Param("fundCode") String fundCode);

    List<FundPerformance> selectTopByReturn(@Param("period") String period,
                                             @Param("limit") int limit);

    int insert(FundPerformance record);

    int updateByFundAndDate(FundPerformance record);
}
