package com.xinling.app.service.impl;

import com.xinling.app.constant.AppConstants;
import com.xinling.app.domain.entity.AppUser;
import com.xinling.app.domain.model.*;
import com.xinling.app.enums.AppUserStatus;
import com.xinling.app.mapper.AppUserMapper;
import com.xinling.app.service.IAppUserService;
import com.xinling.app.service.IVerificationCodeService;
import com.xinling.app.token.AppTokenService;
import com.xinling.app.wechat.WechatAuthService;
import com.xinling.app.wechat.WechatUserProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * APP用户服务实现
 */
@Service
public class AppUserServiceImpl implements IAppUserService {

    private static final Logger log = LoggerFactory.getLogger(AppUserServiceImpl.class);

    private final AppUserMapper appUserMapper;
    private final IVerificationCodeService verificationCodeService;
    private final AppTokenService appTokenService;
    private final WechatAuthService wechatAuthService;

    public AppUserServiceImpl(AppUserMapper appUserMapper,
                              IVerificationCodeService verificationCodeService,
                              AppTokenService appTokenService,
                              WechatAuthService wechatAuthService) {
        this.appUserMapper = appUserMapper;
        this.verificationCodeService = verificationCodeService;
        this.appTokenService = appTokenService;
        this.wechatAuthService = wechatAuthService;
    }

    @Override
    @Transactional
    public LoginResponseVO loginByPhone(AppLoginBody body, String clientIp) {
        String phone = body.getPhone();
        String code = body.getCode();

        // 校验验证码
        verificationCodeService.verifyCode(phone, code);

        // 查用户（不存则自动注册）
        AppUser user = appUserMapper.selectByPhone(phone);
        boolean isNewUser = false;
        if (user == null) {
            user = registerNewUser(phone, null, clientIp, body.getInviterId());
            isNewUser = true;
        } else {
            // 检查用户状态
            checkUserStatus(user);
        }

        // 更新登录信息
        appUserMapper.updateLoginInfo(user.getId(), clientIp, new Date());

        // 验证码使用后清除
        verificationCodeService.deleteCode(phone);

        // 生成Token
        LoginResponseVO response = buildLoginResponse(user);

        log.info("用户登录: id={}, phone={}, newUser={}", user.getId(), phone, isNewUser);
        return response;
    }

    @Override
    @Transactional
    public LoginResponseVO loginByThird(ThirdLoginBody body, String clientIp) {
        String platform = body.getPlatform();
        if (!"wechat".equalsIgnoreCase(platform)) {
            throw new UnsupportedOperationException("暂仅支持微信快捷登录，platform=" + platform);
        }
        return loginByWechat(body, clientIp);
    }

    /**
     * 微信快捷登录
     * <p>
     * 流程：前端拿到的授权 code → 后端调微信换取 openid/unionid 与用户资料 →
     * 优先用 unionid（同一开放平台统一）查找已有账号，其次 openid → 命中则刷新登录，
     * 未命中则自动创建账号，最后下发 token。
     * </p>
     */
    private LoginResponseVO loginByWechat(ThirdLoginBody body, String clientIp) {
        String code = body.getCode();
        if (code == null || code.isEmpty()) {
            throw new RuntimeException("微信授权码为空");
        }

        WechatUserProfile profile = wechatAuthService.exchange(code);
        if (profile == null || profile.getOpenid() == null) {
            throw new RuntimeException("微信授权失败：无法获取用户标识");
        }

        AppUser user = null;
        if (profile.getUnionid() != null && !profile.getUnionid().isEmpty()) {
            user = appUserMapper.selectByWxUnionId(profile.getUnionid());
        }
        if (user == null) {
            user = appUserMapper.selectByWxOpenid(profile.getOpenid());
        }

        boolean isNewUser = false;
        if (user == null) {
            user = registerByWechat(profile, clientIp, body.getInviterId());
            isNewUser = true;
        } else {
            checkUserStatus(user);
            // 补全可能缺失的微信标识（如先手机号注册、后微信登录）
            boolean needUpdate = false;
            if (user.getWxOpenid() == null && profile.getOpenid() != null) {
                user.setWxOpenid(profile.getOpenid());
                needUpdate = true;
            }
            if (user.getWxUnionid() == null && profile.getUnionid() != null) {
                user.setWxUnionid(profile.getUnionid());
                needUpdate = true;
            }
            if (needUpdate) {
                appUserMapper.updateById(user);
            }
        }

        // 更新登录信息
        appUserMapper.updateLoginInfo(user.getId(), clientIp, new Date());

        LoginResponseVO response = buildLoginResponse(user);
        log.info("微信登录: id={}, openid={}, unionid={}, newUser={}",
                user.getId(), profile.getOpenid(), profile.getUnionid(), isNewUser);
        return response;
    }

    /**
     * 根据微信资料创建新用户
     */
    private AppUser registerByWechat(WechatUserProfile profile, String registerIp, Long inviterId) {
        AppUser user = new AppUser();
        user.setNickname(profile.getNickname() != null && !profile.getNickname().isEmpty()
                ? profile.getNickname()
                : AppConstants.DEFAULT_NICKNAME_PREFIX + profile.getOpenid().substring(0, 6));
        user.setAvatar(profile.getAvatar());
        user.setGender(profile.getGender() != null ? profile.getGender() : 0);
        user.setStatus(AppUserStatus.NORMAL.getCode());
        user.setVipStatus(0);
        user.setWxOpenid(profile.getOpenid());
        user.setWxUnionid(profile.getUnionid());
        user.setInviterId(inviterId != null && inviterId > 0 ? inviterId : null);
        user.setRegisterIp(registerIp);
        appUserMapper.insert(user);
        return user;
    }

    @Override
    public LoginResponseVO refreshToken(String oldToken) {
        // Token续期由Controller层直接调用AppTokenService处理
        // 此处仅作占位，实际逻辑在Controller中实现
        throw new UnsupportedOperationException("请使用Controller层refreshToken");
    }

    @Override
    public void logout(String token) {
        appTokenService.delLoginUser(token);
    }

    @Override
    public AppUserInfoVO getUserInfo(Long userId) {
        AppUser user = appUserMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        return toUserInfoVO(user);
    }

    @Override
    @Transactional
    public void updateUser(Long userId, String nickname, Integer gender, String birthday) {
        AppUser user = new AppUser();
        user.setId(userId);
        user.setNickname(nickname);
        user.setGender(gender);
        if (birthday != null && !birthday.isEmpty()) {
            try {
                user.setBirthday(new SimpleDateFormat("yyyy-MM-dd").parse(birthday));
            } catch (Exception e) {
                throw new RuntimeException("生日格式错误，应为yyyy-MM-dd");
            }
        }
        appUserMapper.updateById(user);
    }

    @Override
    @Transactional
    public void updateAvatar(Long userId, String avatarUrl) {
        AppUser user = new AppUser();
        user.setId(userId);
        user.setAvatar(avatarUrl);
        appUserMapper.updateById(user);
    }

    @Override
    public AppUser getById(Long userId) {
        return appUserMapper.selectById(userId);
    }

    @Override
    public AppUserInfoVO getInviter(Long userId) {
        AppUser user = appUserMapper.selectById(userId);
        if (user == null || user.getInviterId() == null) {
            return null;
        }
        AppUser inviter = appUserMapper.selectById(user.getInviterId());
        if (inviter == null) {
            return null;
        }
        AppUserInfoVO vo = new AppUserInfoVO();
        vo.setId(inviter.getId());
        vo.setNickname(inviter.getNickname());
        vo.setAvatar(inviter.getAvatar());
        return vo;
    }

    @Override
    @Transactional
    public void deleteAccount(Long userId) {
        appUserMapper.deleteById(userId);
        log.info("用户注销: id={}", userId);
    }

    @Override
    public AppUser selectById(Long id) {
        return appUserMapper.selectById(id);
    }

    @Override
    public List<AppUser> selectUserList(AppUser user) {
        return appUserMapper.selectUserList(
                user.getNickname(), user.getPhone(),
                user.getStatus(), null, null);
    }

    @Override
    public int updateById(AppUser user) {
        return appUserMapper.updateById(user);
    }

    @Override
    public int deleteById(Long id) {
        return appUserMapper.deleteById(id);
    }

    // ========== 管理后台方法实现 ==========

    @Override
    public List<com.xinling.common.core.domain.model.AppUserAdminVO> selectAdminUserList(
            String nickname, String phone, Integer status, Integer vipStatus,
            String beginTime, String endTime) {
        return appUserMapper.selectAdminUserList(nickname, phone, status, vipStatus, beginTime, endTime);
    }

    @Override
    public com.xinling.common.core.domain.model.AppUserAdminVO selectAdminUserDetail(Long id) {
        return appUserMapper.selectAdminUserDetail(id);
    }

    @Override
    public long countAll() {
        return appUserMapper.countAll();
    }

    @Override
    public int updateUserStatus(Long id, Integer status) {
        return appUserMapper.updateUserStatus(id, status);
    }

    @Override
    public int updateUserVip(Long id, Integer vipStatus, java.util.Date vipEndTime) {
        return appUserMapper.updateUserVip(id, vipStatus, vipEndTime);
    }

    @Override
    @Transactional
    public int extendUserVip(Long id, int days) {
        com.xinling.common.core.domain.model.AppUserAdminVO user = appUserMapper.selectAdminUserDetail(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        java.util.Date baseTime = user.getVipEndTime();
        java.util.Date now = new java.util.Date();
        if (baseTime == null || baseTime.before(now)) {
            baseTime = now;
        }
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTime(baseTime);
        cal.add(java.util.Calendar.DAY_OF_YEAR, days);

        Integer vipStatus = user.getVipStatus();
        if (vipStatus == null || vipStatus == 0) {
            vipStatus = 1;
        }
        return appUserMapper.updateUserVip(id, vipStatus, cal.getTime());
    }

    @Override
    public int deleteByAdmin(Long id) {
        return appUserMapper.softDeleteUser(id);
    }

    // ========== 私有方法 ==========

    private AppUser registerNewUser(String phone, String email, String registerIp, Long inviterId) {
        AppUser user = new AppUser();
        user.setNickname(AppConstants.DEFAULT_NICKNAME_PREFIX + phone.substring(phone.length() - 4));
        user.setPhone(phone);
        user.setEmail(email);
        user.setGender(0);
        user.setStatus(AppUserStatus.NORMAL.getCode());
        user.setVipStatus(0);
        user.setInviterId(inviterId != null && inviterId > 0 ? inviterId : null);
        user.setRegisterIp(registerIp);
        appUserMapper.insert(user);
        return user;
    }

    private void checkUserStatus(AppUser user) {
        if (AppUserStatus.DISABLED.getCode() == user.getStatus()) {
            throw new RuntimeException("账号已被禁用");
        }
        if (AppUserStatus.FROZEN.getCode() == user.getStatus()) {
            throw new RuntimeException("账号已被冻结");
        }
    }

    private LoginResponseVO buildLoginResponse(AppUser user) {
        AppLoginUser loginUser = new AppLoginUser();
        loginUser.setUserId(user.getId());
        loginUser.setNickname(user.getNickname());
        loginUser.setLoginTime(System.currentTimeMillis());
        loginUser.setExpireTime(loginUser.getLoginTime() + AppConstants.TOKEN_EXPIRE * 60 * 1000L);

        String token = appTokenService.createToken(loginUser);

        LoginResponseVO response = new LoginResponseVO();
        response.setToken(token);
        response.setUserInfo(toUserInfoVO(user));
        return response;
    }

    private AppUserInfoVO toUserInfoVO(AppUser user) {
        AppUserInfoVO vo = new AppUserInfoVO();
        vo.setId(user.getId());
        vo.setNickname(user.getNickname());
        vo.setAvatar(user.getAvatar());
        vo.setPhone(maskPhone(user.getPhone()));
        vo.setEmail(user.getEmail());
        vo.setGender(user.getGender());
        vo.setVipStatus(user.getVipStatus());
        vo.setInviterId(user.getInviterId());
        if (user.getBirthday() != null) {
            vo.setBirthday(new SimpleDateFormat("yyyy-MM-dd").format(user.getBirthday()));
        }
        return vo;
    }

    /**
     * 手机号脱敏：138****1234
     */
    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 7) return phone;
        return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4);
    }
}
