/** Default avatar SVG (simple user silhouette) */
export const DEFAULT_AVATAR =
  'data:image/svg+xml,' +
  encodeURIComponent(
    '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100">' +
    '<rect width="100" height="100" rx="50" fill="#2d2d5e"/>' +
    '<circle cx="50" cy="38" r="18" fill="#8888aa"/>' +
    '<path d="M18 82c0-18 14-32 32-32s32 14 32 32" fill="#8888aa"/>' +
    '</svg>'
  )

/** Default cover image for audio cards */
export const DEFAULT_COVER =
  'data:image/svg+xml,' +
  encodeURIComponent(
    '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 120 120">' +
    '<rect width="120" height="120" fill="#1a1a3e"/>' +
    '<text x="60" y="65" text-anchor="middle" fill="#7c5cff" font-size="32">🎵</text>' +
    '</svg>'
  )

// ===== 微信快捷登录配置 =====
// 在 .env 中配置 VITE_WECHAT_APPID（公众号或微信开放平台·网站应用 AppID）。
// 未配置时使用空串占位，联调时替换为真实 AppID 即可。
const viteEnv = import.meta.env as Record<string, string | undefined>
export const WECHAT_APPID = viteEnv.VITE_WECHAT_APPID ?? ''
/** 公众号授权作用域：snsapi_base（静默）/ snsapi_userinfo（弹窗拿昵称头像） */
export const WECHAT_SCOPE = viteEnv.VITE_WECHAT_SCOPE ?? 'snsapi_userinfo'
/** 网页授权回调域名需与微信公众平台配置一致，此处 state 用于回调后识别来源 */
export const WECHAT_STATE = 'xinling_wxlogin'

/** 当前是否处于微信内置浏览器 */
export function isWechatBrowser(): boolean {
  return /micromessenger/i.test(navigator.userAgent)
}

/**
 * 构造微信授权跳转地址。
 * - 微信内：connect/oauth2/authorize（网页授权）
 * - 微信外：connect/qrconnect（网站应用扫码登录）
 * 两者最终都回调 redirectUri 并携带 ?code=...&state=...，后端统一走 code 换 openid。
 */
export function buildWechatAuthUrl(redirectUri: string): string {
  const encoded = encodeURIComponent(redirectUri)
  if (isWechatBrowser()) {
    return `https://open.weixin.qq.com/connect/oauth2/authorize?appid=${WECHAT_APPID}&redirect_uri=${encoded}&response_type=code&scope=${WECHAT_SCOPE}&state=${WECHAT_STATE}#wechat_redirect`
  }
  return `https://open.weixin.qq.com/connect/qrconnect?appid=${WECHAT_APPID}&redirect_uri=${encoded}&response_type=code&scope=snsapi_login&state=${WECHAT_STATE}#wechat_redirect`
}
