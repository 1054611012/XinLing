package com.xinling.app.mapper;

import com.xinling.app.domain.entity.Moment;
import com.xinling.app.domain.model.MomentVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 动态 Mapper
 */

/**
 * 动态 Mapper
 */
public interface MomentMapper {

    Moment selectById(@Param("id") Long id);

    List<Moment> selectFeed(@Param("type") String type, @Param("currentUserId") Long currentUserId, @Param("offset") int offset, @Param("limit") int limit);

    List<Moment> selectHot(@Param("limit") int limit);

    List<Moment> selectList(@Param("userId") Long userId,
                            @Param("type") String type,
                            @Param("beginTime") String beginTime,
                            @Param("endTime") String endTime);

    int insert(Moment moment);

    int updateById(Moment moment);

    int deleteById(@Param("id") Long id);

    int incrementLikeCount(@Param("id") Long id);

    int decrementLikeCount(@Param("id") Long id);

    int incrementCommentCount(@Param("id") Long id);

    int decrementCommentCount(@Param("id") Long id);

    int incrementShareCount(@Param("id") Long id);

    // ========== 管理后台 ==========

    /**
     * 管理后台：查询动态列表（含用户昵称/头像，含已删除）
     */
    List<MomentVO> selectAdminList(@Param("userId") Long userId,
                                   @Param("content") String content,
                                   @Param("type") String type,
                                   @Param("visibility") Integer visibility,
                                   @Param("isDeleted") Integer isDeleted,
                                   @Param("beginTime") String beginTime,
                                   @Param("endTime") String endTime);

    /**
     * 管理后台：根据ID查询动态详情（含用户昵称/头像，不排除已删除）
     */
    MomentVO selectByIdRaw(@Param("id") Long id);

    /**
     * 管理后台：物理删除
     */
    int deleteByIdForce(@Param("id") Long id);

    /**
     * 管理后台：恢复（取消软删除）
     */
    int restoreById(@Param("id") Long id);

    /**
     * 管理后台：编辑动态（不限制 is_deleted）
     */
    int updateByIdForce(Moment moment);
}
