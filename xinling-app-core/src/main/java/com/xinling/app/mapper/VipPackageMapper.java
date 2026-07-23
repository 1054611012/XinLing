package com.xinling.app.mapper;

import com.xinling.app.domain.entity.VipPackage;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 会员套餐 Mapper
 */
public interface VipPackageMapper {

    VipPackage selectById(@Param("id") Long id);

    List<VipPackage> selectList(@Param("status") Integer status);

    List<VipPackage> selectAll();

    int insert(VipPackage vipPackage);

    int updateById(VipPackage vipPackage);
}
