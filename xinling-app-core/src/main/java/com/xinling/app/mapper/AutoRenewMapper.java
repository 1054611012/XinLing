package com.xinling.app.mapper;

import com.xinling.app.domain.entity.AutoRenew;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 自动续费 Mapper
 */
public interface AutoRenewMapper {

    AutoRenew selectById(@Param("id") Long id);

    AutoRenew selectByUserId(@Param("userId") Long userId);

    AutoRenew selectByUserIdAndPackageId(@Param("userId") Long userId,
                                          @Param("packageId") Long packageId);

    List<AutoRenew> selectActiveList();

    int insert(AutoRenew autoRenew);

    int updateById(AutoRenew autoRenew);

    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    int deleteById(@Param("id") Long id);
}
