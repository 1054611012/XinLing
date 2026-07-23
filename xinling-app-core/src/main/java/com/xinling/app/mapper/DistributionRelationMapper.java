package com.xinling.app.mapper;

import com.xinling.app.domain.entity.DistributionRelation;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 分销关系 Mapper
 */
public interface DistributionRelationMapper {

    DistributionRelation selectById(@Param("id") Long id);

    DistributionRelation selectByUserId(@Param("userId") Long userId);

    List<DistributionRelation> selectByParentId(@Param("parentId") Long parentId);

    List<DistributionRelation> selectByGrandparentId(@Param("grandparentId") Long grandparentId);

    int insert(DistributionRelation relation);

    int countByParentId(@Param("parentId") Long parentId);

    int countByGrandparentId(@Param("grandparentId") Long grandparentId);
}
