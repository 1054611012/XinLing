package com.xinling.app.mapper;

import com.xinling.app.domain.entity.CommissionRecord;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * 佣金记录 Mapper
 */
public interface CommissionRecordMapper {

    CommissionRecord selectById(@Param("id") Long id);

    List<CommissionRecord> selectByDistributorId(@Param("distributorId") Long distributorId,
                                                  @Param("offset") Integer offset,
                                                  @Param("limit") Integer limit);

    int countByDistributorId(@Param("distributorId") Long distributorId);

    int insert(CommissionRecord record);

    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    BigDecimal sumTodayCommission(@Param("distributorId") Long distributorId);

    int countTodayOrders(@Param("distributorId") Long distributorId);

    int countPendingSettle(@Param("distributorId") Long distributorId);

    BigDecimal sumPendingSettleAmount(@Param("distributorId") Long distributorId);
}
