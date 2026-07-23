package com.xinling.app.mapper;

import com.xinling.app.domain.entity.DistributionSettings;
import org.apache.ibatis.annotations.Param;

/**
 * 分销设置 Mapper
 */
public interface DistributionSettingsMapper {

    DistributionSettings selectById(@Param("id") Long id);

    DistributionSettings selectFirst();

    int updateById(DistributionSettings settings);
}
