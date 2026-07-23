package com.xinling.app.mapper;

import com.xinling.app.domain.entity.OrderRefund;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 退款记录 Mapper
 */
public interface OrderRefundMapper {

    OrderRefund selectById(@Param("id") Long id);

    List<OrderRefund> selectByOrderNo(@Param("orderNo") String orderNo);

    int insert(OrderRefund orderRefund);

    int updateById(OrderRefund orderRefund);

    int updateStatus(@Param("id") Long id, @Param("refundStatus") Integer refundStatus);
}
