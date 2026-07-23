package com.xinling.app.mapper;

import com.xinling.app.domain.entity.VipGiftRule;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 会员赠送规则 Mapper
 */
public interface VipGiftRuleMapper {

    VipGiftRule selectById(@Param("id") Long id);

    List<VipGiftRule> selectList(@Param("ruleType") String ruleType,
                                 @Param("status") Integer status);

    int insert(VipGiftRule rule);

    int updateById(VipGiftRule rule);

    int deleteById(@Param("id") Long id);

    int incrementGrantedCount(@Param("id") Long id);

    int countTodayGranted(@Param("ruleId") Long ruleId);
}
