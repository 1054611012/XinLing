package com.xinling.app.mapper;

import com.xinling.app.domain.entity.AppUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * APP用户 Mapper
 */
public interface AppUserMapper {

    AppUser selectById(@Param("id") Long id);

    AppUser selectByPhone(@Param("phone") String phone);

    AppUser selectByWxUnionId(@Param("wxUnionid") String wxUnionid);

    AppUser selectByWxOpenid(@Param("wxOpenid") String wxOpenid);

    AppUser selectByEmail(@Param("email") String email);

    List<AppUser> selectUserList(@Param("nickname") String nickname,
                                 @Param("phone") String phone,
                                 @Param("status") Integer status,
                                 @Param("beginTime") String beginTime,
                                 @Param("endTime") String endTime);

    int insert(AppUser user);

    int updateById(AppUser user);

    int deleteById(@Param("id") Long id);

    int updateLoginInfo(@Param("id") Long id,
                        @Param("lastLoginIp") String lastLoginIp,
                        @Param("lastLoginTime") java.util.Date lastLoginTime);

    int countByPhone(@Param("phone") String phone);

    // ========== 管理后台（通过 IAppUserService 暴露给 admin） ==========

    /**
     * 管理后台：查询用户列表（含动态数、评论数、邀请人昵称）
     */
    List<com.xinling.common.core.domain.model.AppUserAdminVO> selectAdminUserList(
            @Param("nickname") String nickname,
            @Param("phone") String phone,
            @Param("status") Integer status,
            @Param("vipStatus") Integer vipStatus,
            @Param("beginTime") String beginTime,
            @Param("endTime") String endTime);

    /**
     * 管理后台：查询用户详情
     */
    com.xinling.common.core.domain.model.AppUserAdminVO selectAdminUserDetail(@Param("id") Long id);

    /**
     * 管理后台：获取用户总数
     */
    long countAll();

    /**
     * 管理后台：更新状态
     */
    int updateUserStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 管理后台：更新 VIP
     */
    int updateUserVip(@Param("id") Long id,
                       @Param("vipStatus") Integer vipStatus,
                       @Param("vipEndTime") java.util.Date vipEndTime);

    /**
     * 管理后台：软删除用户
     */
    int softDeleteUser(@Param("id") Long id);
}