package com.xinling.app.mapper;

import com.xinling.app.domain.entity.Distributor;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * 分销员 Mapper
 */
public interface DistributorMapper {

    Distributor selectById(@Param("id") Long id);

    Distributor selectByUserId(@Param("userId") Long userId);

    List<Distributor> selectList(@Param("level") Integer level,
                                 @Param("status") Integer status);

    int insert(Distributor distributor);

    int updateById(Distributor distributor);

    int updateCommission(@Param("id") Long id,
                         @Param("totalCommission") BigDecimal totalCommission,
                         @Param("availableCommission") BigDecimal availableCommission,
                         @Param("frozenCommission") BigDecimal frozenCommission);

    int updateWithdrawInfo(@Param("id") Long id,
                           @Param("availableCommission") BigDecimal availableCommission,
                           @Param("frozenCommission") BigDecimal frozenCommission,
                           @Param("totalWithdraw") BigDecimal totalWithdraw);
}
