<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showLoadingToast, closeToast } from 'vant'
import { sendCode } from '@/api/user'
import { useAuthStore } from '@/stores/auth'
import { useCountdown } from '@/hooks/useCountdown'
import { buildWechatAuthUrl, WECHAT_STATE, WECHAT_APPID } from '@/utils/constants'

const router = useRouter()
const authStore = useAuthStore()

const phone = ref('')
const code = ref('')
const agreePolicy = ref(true)
const { count, isRunning: isCounting, start: startCountdown } = useCountdown(60)

const canSendCode = computed(() => phone.value.length === 11 && !isCounting.value)
const canLogin = computed(() => phone.value.length === 11 && code.value.length >= 4 && agreePolicy.value)

function formatPhone(v: string) {
  return v.replace(/\D/g, '').slice(0, 11)
}

watch(phone, (val) => {
  phone.value = formatPhone(val)
})

/**
 * 微信授权回跳处理：
 * 用户点击微信登录后跳转至微信授权页，微信携带 ?code=...&state=... 回跳到本页，
 * 此处拿到 code 调后端 thirdLogin 完成登录，并清理 URL 中的临时参数。
 */
onMounted(async () => {
  if (authStore.isLoggedIn) return
  const params = new URLSearchParams(window.location.search)
  const code = params.get('code')
  const state = params.get('state')
  if (code && state === WECHAT_STATE) {
    // 先清理 URL，避免刷新重复登录
    const cleanUrl = window.location.origin + window.location.pathname
    window.history.replaceState({}, '', cleanUrl)
    const toast = showLoadingToast({ message: '微信登录中...', forbidClick: true, duration: 0 })
    try {
      await authStore.loginByWechat(code)
      closeToast()
      router.replace('/')
    } catch {
      closeToast()
      showToast('微信登录失败，请重试')
    }
  }
})

async function handleSendCode() {
  if (!canSendCode.value) return
  try {
    await sendCode({ phone: phone.value, scene: 'login' })
    showToast('验证码已发送')
    startCountdown()
  } catch {
    showToast('发送失败，请重试')
  }
}

async function handleLogin() {
  if (!canLogin.value) return
  const toast = showLoadingToast({
    message: '登录中...',
    forbidClick: true,
    duration: 0
  })
  try {
    await authStore.login(phone.value, code.value)
    closeToast()
    router.replace('/')
  } catch {
    closeToast()
    showToast('验证码错误，请重试')
  }
}

function handleThirdLogin(provider: string) {
  if (provider === '微信') {
    if (!WECHAT_APPID) {
      showToast('微信登录未配置 AppID')
      return
    }
    // 当前页面作为回调地址（需与微信公众平台配置的网页授权回调域名一致）
    const redirectUri = window.location.origin + window.location.pathname
    const authUrl = buildWechatAuthUrl(redirectUri)
    window.location.href = authUrl
    return
  }
  showToast(`${provider}登录开发中`)
}
</script>

<template>
  <div class="login-page">
    <!-- Dark background ornaments -->
    <div class="login-bg-gradient" />
    <div class="login-bg-glow login-bg-glow-1" />
    <div class="login-bg-glow login-bg-glow-2" />
    <div class="login-bg-ornament login-bg-ornament-1" />
    <div class="login-bg-ornament login-bg-ornament-2" />

    <div class="login-content">
      <!-- Brand header -->
      <div class="brand-section">
        <div class="logo-container">
          <svg width="80" height="80" viewBox="0 0 80 80" fill="none" class="logo-svg">
            <!-- Outer glow ring -->
            <circle cx="40" cy="40" r="39" stroke="url(#logoGrad)" stroke-width="1.5" opacity="0.3" />
            <!-- Inner gradient circle -->
            <circle cx="40" cy="40" r="32" fill="url(#logoBg)" opacity="0.15" />
            <!-- Orbiting glow dots -->
            <circle cx="40" cy="8" r="2" fill="url(#logoGrad)" opacity="0.6">
              <animate attributeName="opacity" values="0.6;0.2;0.6" dur="3s" repeatCount="indefinite" />
            </circle>
            <circle cx="62.8" cy="20" r="1.5" fill="url(#logoGrad)" opacity="0.4">
              <animate attributeName="opacity" values="0.4;0.1;0.4" dur="4s" repeatCount="indefinite" />
            </circle>
            <circle cx="68" cy="44" r="1.8" fill="url(#logoGrad)" opacity="0.5">
              <animate attributeName="opacity" values="0.5;0.15;0.5" dur="3.5s" repeatCount="indefinite" />
            </circle>
            <!-- Stylized lotus/heart petals -->
            <path d="M40 18C40 18 28 28 28 38C28 44.6 33.4 50 40 50C46.6 50 52 44.6 52 38C52 28 40 18 40 18Z"
                  fill="url(#logoGrad)" opacity="0.9">
              <animate attributeName="opacity" values="0.9;0.7;0.9" dur="4s" repeatCount="indefinite" />
            </path>
            <!-- Inner highlight -->
            <path d="M40 24C40 24 32 31 32 38C32 42.4 35.6 46 40 46C44.4 46 48 42.4 48 38C48 31 40 24 40 24Z"
                  fill="white" opacity="0.25">
              <animate attributeName="opacity" values="0.25;0.15;0.25" dur="4s" repeatCount="indefinite" />
            </path>
            <!-- Sound wave hint -->
            <path d="M28 42C28 42 30 44 34 44C38 44 40 40 40 40C40 40 42 44 46 44C50 44 52 42 52 42"
                  stroke="white" stroke-width="1.5" stroke-linecap="round" fill="none" opacity="0.5" />
            <defs>
              <linearGradient id="logoGrad" x1="0" y1="0" x2="80" y2="80">
                <stop offset="0%" stop-color="#7c5cff" />
                <stop offset="50%" stop-color="#8b83ff" />
                <stop offset="100%" stop-color="#00c896" />
              </linearGradient>
              <radialGradient id="logoBg" cx="50%" cy="50%" r="50%">
                <stop offset="0%" stop-color="#7c5cff" />
                <stop offset="100%" stop-color="#00c896" />
              </radialGradient>
            </defs>
          </svg>
        </div>
        <h1 class="brand-title">心聆</h1>
        <p class="brand-subtitle">沉浸式专注与睡眠助手</p>
      </div>

      <!-- Login form -->
      <div class="login-card">
        <div class="form-header">
          <h2 class="form-title">欢迎回来</h2>
          <p class="form-desc">登录以继续你的专注之旅</p>
        </div>

        <div class="form-body">
          <!-- Phone input -->
          <div class="input-group">
            <div class="input-icon">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round">
                <rect x="5" y="2" width="14" height="20" rx="2" />
                <line x1="12" y1="18" x2="12.01" y2="18" />
              </svg>
            </div>
            <input
              v-model="phone"
              type="tel"
              maxlength="11"
              placeholder="请输入手机号"
              class="text-input"
            />
          </div>

          <!-- Code input -->
          <div class="input-group">
            <div class="input-icon">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round">
                <rect x="3" y="11" width="18" height="11" rx="2" />
                <path d="M7 11V7a5 5 0 0 1 10 0v4" />
              </svg>
            </div>
            <input
              v-model="code"
              type="tel"
              maxlength="6"
              placeholder="请输入验证码"
              class="text-input"
            />
            <button
              class="send-code-btn"
              :class="{ active: canSendCode }"
              :disabled="!canSendCode"
              @click="handleSendCode"
            >
              {{ isCounting ? `${count}s` : '获取验证码' }}
            </button>
          </div>

          <!-- Agreement -->
          <div class="agreement-row">
            <label class="checkbox-label">
              <input type="checkbox" v-model="agreePolicy" class="checkbox-input" />
              <span class="checkbox-custom">
                <svg v-if="agreePolicy" width="10" height="10" viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="4" stroke-linecap="round" stroke-linejoin="round">
                  <polyline points="4 12 10 18 20 6" />
                </svg>
              </span>
              <span class="checkbox-text">已阅读并同意</span>
            </label>
            <span class="policy-link">《用户协议》</span>
            <span class="policy-link">《隐私政策》</span>
          </div>

          <!-- Login button -->
          <button
            class="login-btn"
            :class="{ active: canLogin }"
            :disabled="!canLogin"
            @click="handleLogin"
          >
            登录
          </button>
        </div>
      </div>

      <!-- Third-party login -->
      <div class="third-party-section">
        <div class="divider-row">
          <span class="divider-line" />
          <span class="divider-text">其他登录方式</span>
          <span class="divider-line" />
        </div>
        <div class="social-icons">
          <button class="social-btn" @click="handleThirdLogin('微信')">
            <svg width="26" height="26" viewBox="0 0 24 24" fill="none" stroke="#07c160" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round">
              <path d="M9 14.4a4.4 4.4 0 1 1 4.3-5.6c.3-.5.8-.9 1.4-1.1A5 5 0 1 0 14 14.4c-.2 0-.4 0-.6-.05A4.6 4.6 0 0 1 9 14.4z" />
              <path d="M8.5 14.2 7 16.4l.9-1.7z" fill="#07c160" stroke="none" />
              <circle cx="7.3" cy="11" r=".9" fill="#07c160" stroke="none" />
              <circle cx="10.7" cy="11" r=".9" fill="#07c160" stroke="none" />
              <path d="M14.6 5.2a3 3 0 1 1 2.6 3.4" />
              <circle cx="14.3" cy="6.8" r=".6" fill="#07c160" stroke="none" />
              <circle cx="16.6" cy="6.8" r=".6" fill="#07c160" stroke="none" />
            </svg>
          </button>
          <button class="social-btn" @click="handleThirdLogin('QQ')">
            <svg width="26" height="26" viewBox="0 0 24 24" fill="none" stroke="#12b7f5" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round">
              <path d="M12 3.2c-2.8 0-4.5 2-4.5 4.4 0 1-.5 1.7-1 2.6-.4.8-.7 1.4-.7 2.1 0 1.2.9 2.1 2 2.5.3.9.8 1.6 1.5 2.1h6.6c.7-.5 1.2-1.2 1.5-2.1 1.1-.4 2-1.3 2-2.5 0-.7-.3-1.3-.7-2.1-.5-.9-1-1.6-1-2.6 0-2.4-1.7-4.4-4.5-4.4z" />
              <circle cx="9.8" cy="9" r=".9" fill="#12b7f5" stroke="none" />
              <circle cx="14.2" cy="9" r=".9" fill="#12b7f5" stroke="none" />
            </svg>
          </button>
          <button class="social-btn" @click="handleThirdLogin('Apple')">
            <svg width="26" height="26" viewBox="0 0 24 24" fill="none" stroke="#fff" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round">
              <path d="M16.3 12.6c0-1.8 1.4-2.7 1.5-2.8-.8-1.2-2.1-1.3-2.6-1.3-1.1-.1-2.1.6-2.7.6-.6 0-1.4-.6-2.3-.6-1.1 0-2.2.6-2.8 1.7-1.2 2.1-.3 5.2 1 6.8.6 1 1.3 1.9 2.2 1.9.9 0 1.2-.6 2.3-.6 1 0 1.3.6 2.2.6 1 0 1.5-.9 2.1-1.9.6-.8.9-1.6.9-1.7-.1-.1-1.7-.6-1.8-2.7z" />
              <path d="M14.3 6.4c.5-.6.8-1.3.8-2.2-.7 0-1.5.5-2.1 1.1-.5.5-.8 1.2-.8 2.1.7 0 1.6-.4 2.1-1z" />
            </svg>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.login-page {
  position: relative;
  min-height: 100vh;
  min-height: 100dvh;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  background: #0a0a14;
}

/* ===== Dark Background ===== */
.login-bg-gradient {
  position: fixed;
  inset: 0;
  background: linear-gradient(180deg, #0f0b24 0%, #0a0a14 40%, #06060d 100%);
  z-index: 0;
}

.login-bg-glow {
  position: fixed;
  border-radius: 50%;
  pointer-events: none;
  z-index: 0;
}

.login-bg-glow-1 {
  top: -100px;
  left: -80px;
  width: 300px;
  height: 300px;
  background: radial-gradient(circle, rgba(108, 99, 255, 0.12), transparent 70%);
  animation: bg-glow-drift 8s ease-in-out infinite alternate;
}

.login-bg-glow-2 {
  bottom: -60px;
  right: -60px;
  width: 250px;
  height: 250px;
  background: radial-gradient(circle, rgba(0, 200, 150, 0.08), transparent 70%);
  animation: bg-glow-drift 10s ease-in-out infinite alternate-reverse;
}

@keyframes bg-glow-drift {
  0% { transform: translate(0, 0); }
  100% { transform: translate(30px, -20px); }
}

.login-bg-ornament {
  position: fixed;
  border-radius: 50%;
  border: 1px solid rgba(108, 99, 255, 0.03);
  pointer-events: none;
  z-index: 0;
}

.login-bg-ornament-1 {
  top: 20%;
  right: -100px;
  width: 400px;
  height: 400px;
  animation: rotate-slow 30s linear infinite;
}

.login-bg-ornament-2 {
  bottom: 10%;
  left: -80px;
  width: 300px;
  height: 300px;
  animation: rotate-slow 40s linear infinite reverse;
}

@keyframes rotate-slow {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

/* ===== Content ===== */
.login-content {
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  flex: 1;
  padding: 40px 24px 60px;
  overflow-y: auto;
}

/* ===== Brand / Logo ===== */
.brand-section {
  text-align: center;
  margin-bottom: 36px;
  animation: fade-up 0.8s ease both;
}

.logo-container {
  width: 80px;
  height: 80px;
  margin: 0 auto 20px;
  position: relative;
  filter: drop-shadow(0 0 30px rgba(108, 99, 255, 0.3));
}

.logo-svg {
  width: 100%;
  height: 100%;
}

.brand-title {
  font-size: 34px;
  font-weight: 800;
  background: linear-gradient(135deg, #8b83ff 0%, #7c5cff 40%, #00c896 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin-bottom: 8px;
  letter-spacing: 3px;
}

.brand-subtitle {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.35);
  letter-spacing: 2px;
  font-weight: 300;
}

/* ===== Login Card ===== */
.login-card {
  width: 100%;
  max-width: 360px;
  background: rgba(255, 255, 255, 0.04);
  backdrop-filter: blur(24px);
  -webkit-backdrop-filter: blur(24px);
  border-radius: 24px;
  border: 1px solid rgba(255, 255, 255, 0.06);
  padding: 32px 28px 28px;
  animation: fade-up 0.8s 0.15s ease both;
  box-shadow: 0 8px 40px rgba(0, 0, 0, 0.3);
}

.form-header {
  margin-bottom: 24px;
}

.form-title {
  font-size: 20px;
  font-weight: 700;
  color: rgba(255, 255, 255, 0.9);
  margin-bottom: 4px;
}

.form-desc {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.35);
}

.form-body {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* ===== Input Groups ===== */
.input-group {
  display: flex;
  align-items: center;
  gap: 12px;
  background: rgba(255, 255, 255, 0.04);
  border: 1px solid rgba(255, 255, 255, 0.06);
  border-radius: 14px;
  padding: 0 16px;
  height: 50px;
  transition: border-color 0.3s, background 0.3s;
}

.input-group:focus-within {
  border-color: rgba(108, 99, 255, 0.4);
  background: rgba(108, 99, 255, 0.06);
}

.input-icon {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  color: rgba(255, 255, 255, 0.25);
}

.text-input {
  flex: 1;
  background: none;
  border: none;
  outline: none;
  color: rgba(255, 255, 255, 0.9);
  font-size: 15px;
  height: 100%;
  width: 0;
  min-width: 0;
}

.text-input::placeholder {
  color: rgba(255, 255, 255, 0.2);
}

/* ===== Send Code Button ===== */
.send-code-btn {
  flex-shrink: 0;
  height: 32px;
  padding: 0 14px;
  border: none;
  border-radius: 8px;
  font-size: 12px;
  font-weight: 500;
  background: rgba(255, 255, 255, 0.06);
  color: rgba(255, 255, 255, 0.25);
  cursor: not-allowed;
  white-space: nowrap;
  transition: all 0.3s;
}

.send-code-btn.active {
  background: rgba(108, 99, 255, 0.2);
  color: #7c5cff;
  cursor: pointer;
}

.send-code-btn.active:active {
  background: rgba(108, 99, 255, 0.3);
  transform: scale(0.97);
}

/* ===== Agreement ===== */
.agreement-row {
  display: flex;
  align-items: center;
  gap: 2px;
  font-size: 12px;
  flex-wrap: wrap;
}

.checkbox-label {
  display: flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
}

.checkbox-input {
  display: none;
}

.checkbox-custom {
  width: 16px;
  height: 16px;
  border-radius: 4px;
  border: 1.5px solid rgba(255, 255, 255, 0.15);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
  flex-shrink: 0;
}

.checkbox-input:checked + .checkbox-custom {
  background: #7c5cff;
  border-color: #7c5cff;
}

.checkbox-text {
  color: rgba(255, 255, 255, 0.35);
}

.policy-link {
  color: #7c5cff;
  cursor: pointer;
}

/* ===== Login Button ===== */
.login-btn {
  width: 100%;
  height: 48px;
  border: none;
  border-radius: 24px;
  font-size: 16px;
  font-weight: 600;
  color: rgba(255, 255, 255, 0.25);
  background: rgba(108, 99, 255, 0.15);
  cursor: not-allowed;
  transition: all 0.3s;
  margin-top: 4px;
}

.login-btn.active {
  color: white;
  background: linear-gradient(135deg, #7c5cff, #00c896);
  cursor: pointer;
  box-shadow: 0 4px 24px rgba(108, 99, 255, 0.3);
}

.login-btn.active:active {
  transform: scale(0.98);
}

/* ===== Third-party ===== */
.third-party-section {
  width: 100%;
  max-width: 360px;
  margin-top: 28px;
  animation: fade-up 0.8s 0.3s ease both;
}

.divider-row {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
}

.divider-line {
  flex: 1;
  height: 1px;
  background: rgba(255, 255, 255, 0.06);
}

.divider-text {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.25);
  flex-shrink: 0;
}

.social-icons {
  display: flex;
  justify-content: center;
  gap: 20px;
}

.social-btn {
  width: 52px;
  height: 52px;
  border-radius: 50%;
  border: 1px solid rgba(255, 255, 255, 0.06);
  background: rgba(255, 255, 255, 0.03);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s;
  backdrop-filter: blur(10px);
}

.social-btn:active {
  transform: scale(0.92);
  background: rgba(255, 255, 255, 0.06);
  border-color: rgba(255, 255, 255, 0.1);
}

/* ===== Animations ===== */
@keyframes fade-up {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>
