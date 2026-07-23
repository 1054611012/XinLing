package com.xinling.app.mapper;

import com.xinling.app.domain.entity.UserVip;
import com.xinling.app.domain.model.UserVipVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户会员 Mapper
 */
public interface UserVipMapper {

    UserVip selectByUserId(@Param("userId") Long userId);

    List<UserVipVO> selectList(@Param("userId") Long userId,
                               @Param("beginTime") String beginTime,
                               @Param("endTime") String endTime);

    int insert(UserVip userVip);

    int updateById(UserVip userVip);

    int updateAutoRenew(@Param("userId") Long userId, @Param("autoRenew") Integer autoRenew);
}
