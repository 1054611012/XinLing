package com.xinling.app.mapper;

import com.xinling.app.domain.entity.PayConfig;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 支付配置 Mapper
 */
public interface PayConfigMapper {

    PayConfig selectById(@Param("id") Long id);

    List<PayConfig> selectByPayType(@Param("payType") String payType);

    List<PayConfig> selectEnabledList();

    List<PayConfig> selectList();

    int insert(PayConfig payConfig);

    int updateById(PayConfig payConfig);
}
