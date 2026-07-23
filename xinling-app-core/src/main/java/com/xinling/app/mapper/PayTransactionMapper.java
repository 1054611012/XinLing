package com.xinling.app.mapper;

import com.xinling.app.domain.entity.PayTransaction;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 交易记录 Mapper
 */
public interface PayTransactionMapper {

    PayTransaction selectById(@Param("id") Long id);

    PayTransaction selectByOrderNo(@Param("orderNo") String orderNo);

    PayTransaction selectByTransactionId(@Param("transactionId") String transactionId);

    List<PayTransaction> selectListByOrderNo(@Param("orderNo") String orderNo);

    List<PayTransaction> selectList(@Param("orderNo") String orderNo,
                                    @Param("payType") String payType,
                                    @Param("beginTime") String beginTime,
                                    @Param("endTime") String endTime);

    int insert(PayTransaction payTransaction);

    int updateById(PayTransaction payTransaction);

    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
}
