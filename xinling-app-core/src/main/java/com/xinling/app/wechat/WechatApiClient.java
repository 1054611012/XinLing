package com.xinling.app.wechat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * 微信开放接口客户端
 * <p>
 * 负责与微信服务器通信：用授权 code 换取 access_token/openid，
 * 以及拉取用户基本信息。仅依赖 JDK HttpClient + Jackson，无需额外组件。
 * </p>
 */
@Component
public class WechatApiClient {

    private static final Logger log = LoggerFactory.getLogger(WechatApiClient.class);

    /** 公众号/网站应用：code 换 access_token */
    private static final String ACCESS_TOKEN_URL = "https://api.weixin.qq.com/sns/oauth2/access_token";

    /** 拉取用户信息（scope=snsapi_userinfo 时可用） */
    private static final String USERINFO_URL = "https://api.weixin.qq.com/sns/userinfo";

    private final WechatProperties properties;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public WechatApiClient(WechatProperties properties) {
        this.properties = properties;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
    }

    /**
     * 用授权 code 换取 access_token + openid(+unionid)
     */
    public OAuthTokenResponse exchangeCode(String code) {
        String url = ACCESS_TOKEN_URL
                + "?appid=" + properties.getAppid()
                + "&secret=" + properties.getSecret()
                + "&code=" + code
                + "&grant_type=authorization_code";
        String body = doGet(url);
        OAuthTokenResponse resp = parse(body, OAuthTokenResponse.class);
        if (resp.getErrcode() != null && resp.getErrcode() != 0) {
            throw new RuntimeException("微信换取 access_token 失败: errcode=" + resp.getErrcode()
                    + ", errmsg=" + resp.getErrmsg());
        }
        return resp;
    }

    /**
     * 拉取用户信息（仅 snsapi_userinfo 授权后可用）
     */
    public UserInfoResponse getUserInfo(String accessToken, String openid) {
        String url = USERINFO_URL
                + "?access_token=" + accessToken
                + "&openid=" + openid
                + "&lang=zh_CN";
        String body = doGet(url);
        UserInfoResponse resp = parse(body, UserInfoResponse.class);
        if (resp.getErrcode() != null && resp.getErrcode() != 0) {
            throw new RuntimeException("微信获取用户信息失败: errcode=" + resp.getErrcode()
                    + ", errmsg=" + resp.getErrmsg());
        }
        return resp;
    }

    private String doGet(String url) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(5))
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            return response.body();
        } catch (Exception e) {
            log.error("调用微信接口异常, url={}", url, e);
            throw new RuntimeException("调用微信接口失败: " + e.getMessage(), e);
        }
    }

    private <T> T parse(String json, Class<T> type) {
        try {
            return objectMapper.readValue(json, type);
        } catch (Exception e) {
            throw new RuntimeException("解析微信返回失败: " + json, e);
        }
    }

    // ===================== DTO =====================

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OAuthTokenResponse {
        private String access_token;
        private Integer expires_in;
        private String refresh_token;
        private String openid;
        private String scope;
        private String unionid;
        private Integer errcode;
        private String errmsg;

        public String getAccess_token() { return access_token; }
        public void setAccess_token(String access_token) { this.access_token = access_token; }
        public Integer getExpires_in() { return expires_in; }
        public void setExpires_in(Integer expires_in) { this.expires_in = expires_in; }
        public String getRefresh_token() { return refresh_token; }
        public void setRefresh_token(String refresh_token) { this.refresh_token = refresh_token; }
        public String getOpenid() { return openid; }
        public void setOpenid(String openid) { this.openid = openid; }
        public String getScope() { return scope; }
        public void setScope(String scope) { this.scope = scope; }
        public String getUnionid() { return unionid; }
        public void setUnionid(String unionid) { this.unionid = unionid; }
        public Integer getErrcode() { return errcode; }
        public void setErrcode(Integer errcode) { this.errcode = errcode; }
        public String getErrmsg() { return errmsg; }
        public void setErrmsg(String errmsg) { this.errmsg = errmsg; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class UserInfoResponse {
        private String openid;
        private String nickname;
        private Integer sex;          // 1-男 2-女 0-未知
        private String headimgurl;
        private String unionid;
        private String country;
        private String province;
        private String city;
        private Integer errcode;
        private String errmsg;

        public String getOpenid() { return openid; }
        public void setOpenid(String openid) { this.openid = openid; }
        public String getNickname() { return nickname; }
        public void setNickname(String nickname) { this.nickname = nickname; }
        public Integer getSex() { return sex; }
        public void setSex(Integer sex) { this.sex = sex; }
        public String getHeadimgurl() { return headimgurl; }
        public void setHeadimgurl(String headimgurl) { this.headimgurl = headimgurl; }
        public String getUnionid() { return unionid; }
        public void setUnionid(String unionid) { this.unionid = unionid; }
        public String getCountry() { return country; }
        public void setCountry(String country) { this.country = country; }
        public String getProvince() { return province; }
        public void setProvince(String province) { this.province = province; }
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
        public Integer getErrcode() { return errcode; }
        public void setErrcode(Integer errcode) { this.errcode = errcode; }
        public String getErrmsg() { return errmsg; }
        public void setErrmsg(String errmsg) { this.errmsg = errmsg; }
    }
}
