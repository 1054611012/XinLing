package com.xinling.app.mapper;

import com.xinling.app.domain.entity.Coupon;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 优惠券 Mapper
 */
public interface CouponMapper {

    Coupon selectById(@Param("id") Long id);

    List<Coupon> selectList(@Param("status") Integer status);

    List<Coupon> selectUsableList();

    int insert(Coupon coupon);

    int updateById(Coupon coupon);

    int incrementUsedCount(@Param("id") Long id);

    int decrementUsedCount(@Param("id") Long id);
}
