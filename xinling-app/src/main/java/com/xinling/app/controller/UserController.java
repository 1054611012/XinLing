package com.xinling.app.controller;

import com.xinling.app.domain.entity.UserDevice;
import com.xinling.app.domain.entity.UserSettings;
import com.xinling.app.domain.model.*;
import com.xinling.app.domain.entity.UserFollow;
import com.xinling.app.service.IAppUserService;
import com.xinling.app.service.ICommunityService;
import com.xinling.app.service.IUserDeviceService;
import com.xinling.app.service.IUserSettingsService;
import com.xinling.app.service.IVerificationCodeService;
import com.xinling.app.token.AppTokenService;
import com.xinling.app.utils.AppContextUtil;
import com.xinling.common.core.domain.R;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/app/user")
public class UserController {

    private final IAppUserService appUserService;
    private final IVerificationCodeService verificationCodeService;
    private final AppTokenService appTokenService;
    private final IUserDeviceService userDeviceService;
    private final IUserSettingsService userSettingsService;
    private final ICommunityService communityService;

    public UserController(IAppUserService appUserService,
                          IVerificationCodeService verificationCodeService,
                          AppTokenService appTokenService,
                          IUserDeviceService userDeviceService,
                          IUserSettingsService userSettingsService,
                          ICommunityService communityService) {
        this.appUserService = appUserService;
        this.verificationCodeService = verificationCodeService;
        this.appTokenService = appTokenService;
        this.userDeviceService = userDeviceService;
        this.userSettingsService = userSettingsService;
        this.communityService = communityService;
    }

    /**
     * 发送验证码
     */
    @PostMapping("/sendCode")
    public R<?> sendCode(@Valid @RequestBody SendCodeBody body) {
        verificationCodeService.sendCode(body.getPhone(), body.getScene());
        return R.ok("验证码已发送");
    }

    /**
     * 手机号验证码登录
     */
    @PostMapping("/login")
    public R<LoginResponseVO> login(@Valid @RequestBody AppLoginBody body, HttpServletRequest request) {
        String clientIp = request.getRemoteAddr();
        LoginResponseVO result = appUserService.loginByPhone(body, clientIp);
        return R.ok(result);
    }

    /**
     * 第三方登录
     */
    @PostMapping("/thirdLogin")
    public R<LoginResponseVO> thirdLogin(@Valid @RequestBody ThirdLoginBody body, HttpServletRequest request) {
        String clientIp = request.getRemoteAddr();
        LoginResponseVO result = appUserService.loginByThird(body, clientIp);
        return R.ok(result);
    }

    /**
     * 退出登录
     */
    @PostMapping("/logout")
    public R<?> logout() {
        AppLoginUser loginUser = AppContextUtil.getLoginUser();
        if (loginUser != null) {
            appTokenService.delLoginUser(loginUser.getToken());
            appUserService.logout(loginUser.getToken());
        }
        return R.ok();
    }

    /**
     * 获取用户信息
     */
    @GetMapping("/info")
    public R<AppUserInfoVO> info() {
        Long userId = AppContextUtil.getUserId();
        AppUserInfoVO userInfo = appUserService.getUserInfo(userId);
        return R.ok(userInfo);
    }

    /**
     * 更新用户信息
     */
    @PostMapping("/update")
    public R<?> update(@Valid @RequestBody UpdateUserBody body) {
        Long userId = AppContextUtil.getUserId();
        appUserService.updateUser(userId, body.getNickname(), body.getGender(), body.getBirthday());
        return R.ok();
    }

    /**
     * 上传/更新头像
     */
    @PostMapping("/updateAvatar")
    public R<?> updateAvatar(@RequestParam String avatarUrl) {
        Long userId = AppContextUtil.getUserId();
        appUserService.updateAvatar(userId, avatarUrl);
        return R.ok();
    }

    /**
     * 获取设备列表
     */
    @GetMapping("/devices")
    public R<List<UserDevice>> devices() {
        Long userId = AppContextUtil.getUserId();
        List<UserDevice> list = userDeviceService.getUserDevices(userId);
        return R.ok(list);
    }

    /**
     * 登出指定设备
     */
    @PostMapping("/logoutDevice/{deviceId}")
    public R<?> logoutDevice(@PathVariable Long deviceId) {
        Long userId = AppContextUtil.getUserId();
        userDeviceService.logoutDevice(userId, deviceId);
        return R.ok();
    }

    /**
     * 注销账号
     */
    @PostMapping("/deleteAccount")
    public R<?> deleteAccount() {
        Long userId = AppContextUtil.getUserId();
        appUserService.deleteAccount(userId);
        return R.ok();
    }

    /**
     * 获取用户设置
     */
    @GetMapping("/settings")
    public R<UserSettings> settings() {
        Long userId = AppContextUtil.getUserId();
        UserSettings result = userSettingsService.getOrCreate(userId);
        return R.ok(result);
    }

    /**
     * 更新用户设置
     */
    @PostMapping("/settings/update")
    public R<?> updateSettings(@RequestBody UserSettings settings) {
        Long userId = AppContextUtil.getUserId();
        userSettingsService.update(userId, settings);
        return R.ok();
    }

    /**
     * 获取邀请人信息
     */
    @GetMapping("/inviter")
    public R<AppUserInfoVO> inviter() {
        Long userId = AppContextUtil.getUserId();
        AppUserInfoVO inviter = appUserService.getInviter(userId);
        return R.ok(inviter);
    }

    /**
     * 刷新Token
     */
    @PostMapping("/refreshToken")
    public R<LoginResponseVO> refreshToken(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader;
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        LoginResponseVO result = appUserService.refreshToken(token);
        return R.ok(result);
    }

    /**
     * 导出用户数据
     */
    @GetMapping("/exportData")
    public R<?> exportData() {
        Long userId = AppContextUtil.getUserId();
        // 基础实现：返回用户信息 JSON 下载地址，后续可扩展为完整的数据导出
        AppUserInfoVO userInfo = appUserService.getUserInfo(userId);
        return R.ok(new java.util.HashMap<>() {{
            put("downloadUrl", "/api/app/user/exportData/download");
        }});
    }

    // ========== 关注/粉丝（原 AppSocialController 路径对齐） ==========

    @PostMapping("/follow/{userId}")
    public R<?> follow(@PathVariable Long userId) {
        Long followerId = AppContextUtil.getUserId();
        if (followerId.equals(userId)) {
            return R.fail("不能关注自己");
        }
        communityService.followUser(followerId, userId);
        return R.ok();
    }

    @PostMapping("/unfollow/{userId}")
    public R<?> unfollow(@PathVariable Long userId) {
        Long followerId = AppContextUtil.getUserId();
        communityService.unfollowUser(followerId, userId);
        return R.ok();
    }

    @GetMapping({"/followers", "/followers/{userId}"})
    public R<List<UserFollow>> followers(@PathVariable(required = false) Long userId) {
        Long targetId = userId != null ? userId : AppContextUtil.getUserId();
        List<UserFollow> list = communityService.getFollowers(targetId);
        return R.ok(list);
    }

    @GetMapping({"/following", "/following/{userId}"})
    public R<List<UserFollow>> following(@PathVariable(required = false) Long userId) {
        Long targetId = userId != null ? userId : AppContextUtil.getUserId();
        List<UserFollow> list = communityService.getFollowing(targetId);
        return R.ok(list);
    }
}
