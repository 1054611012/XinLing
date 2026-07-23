package com.xinling.app.service;

import com.xinling.app.domain.entity.AppUser;
import com.xinling.app.domain.model.AppLoginBody;
import com.xinling.app.domain.model.AppUserInfoVO;
import com.xinling.app.domain.model.LoginResponseVO;
import com.xinling.app.domain.model.ThirdLoginBody;
import com.xinling.common.core.domain.model.AppUserAdminVO;

import java.util.List;

/**
 * APP用户服务
 */
public interface IAppUserService {

    /**
     * 手机号验证码登录（用户不存在则自动注册）
     */
    LoginResponseVO loginByPhone(AppLoginBody body, String clientIp);

    /**
     * 第三方登录
     */
    LoginResponseVO loginByThird(ThirdLoginBody body, String clientIp);

    /**
     * Token续期
     */
    LoginResponseVO refreshToken(String token);

    /**
     * 登出
     */
    void logout(String token);

    /**
     * 获取用户信息
     */
    AppUserInfoVO getUserInfo(Long userId);

    /**
     * 更新用户信息
     */
    void updateUser(Long userId, String nickname, Integer gender, String birthday);

    /**
     * 更新头像
     */
    void updateAvatar(Long userId, String avatarUrl);

    /**
     * 获取用户实体
     */
    AppUser getById(Long userId);

    /**
     * 获取邀请人
     */
    AppUserInfoVO getInviter(Long userId);

    /**
     * 注销账号
     */
    void deleteAccount(Long userId);

    // ========== 管理后台方法（通过服务接口暴露给 admin，未来可转为 Feign） ==========

    AppUser selectById(Long id);

    List<AppUser> selectUserList(AppUser user);

    int updateById(AppUser user);

    int deleteById(Long id);

    /** 管理后台：查询用户列表（含统计数据） */
    List<AppUserAdminVO> selectAdminUserList(
            String nickname, String phone, Integer status, Integer vipStatus,
            String beginTime, String endTime);

    /** 管理后台：查询用户详情 */
    com.xinling.common.core.domain.model.AppUserAdminVO selectAdminUserDetail(Long id);

    /** 管理后台：获取用户总数 */
    long countAll();

    /** 管理后台：更新用户状态 */
    int updateUserStatus(Long id, Integer status);

    /** 管理后台：更新 VIP */
    int updateUserVip(Long id, Integer vipStatus, java.util.Date vipEndTime);

    /** 管理后台：延长 VIP */
    int extendUserVip(Long id, int days);

    /** 管理后台：软删除用户 */
    int deleteByAdmin(Long id);
}
