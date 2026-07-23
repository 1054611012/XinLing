package com.xinling.app.mapper;

import com.xinling.app.domain.entity.VipGiftRecord;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 会员赠送记录 Mapper
 */
public interface VipGiftRecordMapper {

    VipGiftRecord selectById(@Param("id") Long id);

    List<VipGiftRecord> selectList(@Param("ruleId") Long ruleId,
                                   @Param("userId") Long userId,
                                   @Param("grantType") String grantType);

    int insert(VipGiftRecord record);

    int countByUserAndRule(@Param("userId") Long userId,
                           @Param("ruleId") Long ruleId);
}
