package com.xinling.app.mapper;

import com.xinling.app.domain.entity.MomentComment;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 评论 Mapper
 */
public interface MomentCommentMapper {

    MomentComment selectById(@Param("id") Long id);

    List<MomentComment> selectByMomentId(@Param("momentId") Long momentId);

    int insert(MomentComment momentComment);

    int deleteById(@Param("id") Long id);
}
