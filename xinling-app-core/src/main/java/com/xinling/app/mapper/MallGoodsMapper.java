package com.xinling.app.mapper;

import com.xinling.app.domain.entity.MallGoods;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 积分商城商品 Mapper
 */
public interface MallGoodsMapper {

    MallGoods selectById(@Param("id") Long id);

    List<MallGoods> selectAvailable();

    List<MallGoods> selectAll();

    int insert(MallGoods mallGoods);

    int updateById(MallGoods mallGoods);
}
