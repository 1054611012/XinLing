package com.xinling.app.mapper;

import com.xinling.app.domain.entity.PayOrder;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 支付订单 Mapper
 */
public interface PayOrderMapper {

    PayOrder selectByOrderNo(@Param("orderNo") String orderNo);

    List<PayOrder> selectByUserId(@Param("userId") Long userId,
                                   @Param("status") Integer status,
                                   @Param("offset") int offset,
                                   @Param("limit") int limit);

    List<PayOrder> selectList(@Param("orderNo") String orderNo,
                              @Param("userId") Long userId,
                              @Param("orderStatus") Integer orderStatus,
                              @Param("beginTime") String beginTime,
                              @Param("endTime") String endTime);

    int countByUserId(@Param("userId") Long userId,
                      @Param("status") Integer status);

    int insert(PayOrder payOrder);

    int updateById(PayOrder payOrder);

    int updateStatus(@Param("orderNo") String orderNo,
                     @Param("orderStatus") Integer orderStatus);

    int updatePayInfo(PayOrder payOrder);
}
