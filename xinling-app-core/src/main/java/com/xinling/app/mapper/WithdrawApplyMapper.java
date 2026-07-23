package com.xinling.app.mapper;

import com.xinling.app.domain.entity.WithdrawApply;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 提现申请 Mapper
 */
public interface WithdrawApplyMapper {

    WithdrawApply selectById(@Param("id") Long id);

    List<WithdrawApply> selectByDistributorId(@Param("distributorId") Long distributorId);

    List<WithdrawApply> selectList();

    int insert(WithdrawApply withdrawApply);
}
