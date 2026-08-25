package com.xinling.app.wechat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 微信登录业务编排
 * <p>
 * 接收前端传来的授权 code，完成「code → access_token/openid → 用户信息」的闭环，
 * 对外仅暴露 {@link #exchange(String)} 返回归一化后的 {@link WechatUserProfile}。
 * </p>
 */
@Service
public class WechatAuthService {

    private static final Logger log = LoggerFactory.getLogger(WechatAuthService.class);

    private final WechatApiClient apiClient;

    public WechatAuthService(WechatApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * 通过前端回调拿到的 code 换取微信用户身份
     *
     * @param code 微信授权 code（一次性，5 分钟内有效）
     * @return 归一化后的微信用户档案
     */
    public WechatUserProfile exchange(String code) {
        WechatApiClient.OAuthTokenResponse token = apiClient.exchangeCode(code);
        String openid = token.getOpenid();
        String unionid = token.getUnionid();

        String nickname = null;
        String avatar = null;
        Integer gender = 0;

        // 仅 snsapi_userinfo 授权能拉到昵称/头像；snsapi_base 静默授权拿不到
        if (token.getAccess_token() != null) {
            try {
                WechatApiClient.UserInfoResponse info = apiClient.getUserInfo(token.getAccess_token(), openid);
                nickname = info.getNickname();
                avatar = info.getHeadimgurl();
                if (info.getSex() != null) {
                    gender = info.getSex();
                }
            } catch (Exception e) {
                log.warn("拉取微信用户信息失败（将使用默认资料）: {}", e.getMessage());
            }
        }

        WechatUserProfile profile = new WechatUserProfile();
        profile.setOpenid(openid);
        profile.setUnionid(unionid);
        profile.setNickname(nickname);
        profile.setAvatar(avatar);
        profile.setGender(gender);
        return profile;
    }
}
