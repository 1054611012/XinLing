package com.xinling.app.mapper;

import com.xinling.app.domain.entity.FocusSettings;
import org.apache.ibatis.annotations.Param;

/**
 * 专注设置 Mapper
 */
public interface FocusSettingsMapper {

    FocusSettings selectByUserId(@Param("userId") Long userId);

    int insert(FocusSettings settings);

    int updateByUserId(FocusSettings settings);
}
