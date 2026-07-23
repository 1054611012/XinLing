package com.xinling.app.mapper;

import com.xinling.app.domain.entity.UserCoupon;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户优惠券 Mapper
 */
public interface UserCouponMapper {

    UserCoupon selectById(@Param("id") Long id);

    List<UserCoupon> selectByUserId(@Param("userId") Long userId);

    UserCoupon selectByUserIdAndCouponId(@Param("userId") Long userId,
                                          @Param("couponId") Long couponId);

    List<UserCoupon> selectUsableByUserId(@Param("userId") Long userId);

    int countByUserIdAndCouponId(@Param("userId") Long userId,
                                  @Param("couponId") Long couponId);

    int insert(UserCoupon userCoupon);

    int updateById(UserCoupon userCoupon);

    int markUsed(@Param("id") Long id, @Param("orderNo") String orderNo);

    int markExpired(@Param("id") Long id);
}
