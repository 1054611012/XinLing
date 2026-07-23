package com.xinling.app.mapper;

import com.xinling.app.domain.entity.AudioMix;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AudioMixMapper {

    AudioMix selectById(@Param("id") Long id);

    List<AudioMix> selectList();

    /**
     * 管理后台查询混音列表（含所有状态）
     */
    List<AudioMix> selectAdminList(@Param("keyword") String keyword,
                                   @Param("status") Integer status);

    int insert(AudioMix mix);

    int updateById(AudioMix mix);

    int deleteById(@Param("id") Long id);
}
