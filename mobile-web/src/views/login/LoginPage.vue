<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showLoadingToast, closeToast } from 'vant'
import { sendCode } from '@/api/user'
import { useAuthStore } from '@/stores/auth'
import { useCountdown } from '@/hooks/useCountdown'

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
            <svg width="26" height="26" viewBox="0 0 24 24" fill="#07c160">
              <path d="M8.5 11a1 1 0 1 1 0-2 1 1 0 0 1 0 2zm5 0a1 1 0 1 1 0-2 1 1 0 0 1 0 2zM12 2C6.48 2 2 5.58 2 10c0 2.35 1.23 4.5 3.22 5.96L4.5 20l3.78-2.07c.54.15 1.1.23 1.72.23.41 0 .8-.03 1.18-.08C13.2 20.4 16.3 22 20 22c1.1 0 2.16-.12 3.16-.34L26 23l-.73-2.33C27.27 19.33 28 17.5 28 15.5 28 11.36 23.52 8 18 8c-.54 0-1.07.04-1.58.12C15.22 6.1 12.5 4.5 9.5 4.5c-.52 0-1.03.05-1.52.14C7.68 3.36 6.18 2.5 4.5 2.5c-.55 0-1.08.06-1.58.18C3.56 1.6 5.4.5 7.5.5c2.3 0 4.38.94 5.97 2.48C14.27 2.34 15.12 2 16 2c1.66 0 3.2.54 4.48 1.44C19.8 3.16 19 3 18.17 3c-1.22 0-2.38.3-3.4.82C13.6 3.3 12.32 3 11 3c-.48 0-.95.04-1.4.12C9.78 2.4 8.94 2 8 2c-.33 0-.65.05-.95.14C7.06 2.06 7.03 2 7 2c.5-.86 1.5-1.5 2.5-1.5.76 0 1.47.28 2 .74C12.23.48 13.1 0 14 0c2.2 0 4.17.94 5.64 2.47C20.88 1.57 22.25 1 23.75 1c2.9 0 5.25 2.35 5.25 5.25 0 1.88-1 3.55-2.5 4.5.16.57.25 1.17.25 1.78 0 3.45-3.15 6.25-7.25 6.25-.54 0-1.07-.04-1.58-.12C16.17 20.3 14.2 22 12 22c-.56 0-1.1-.06-1.63-.16C11.57 20.47 12 19.2 12 18c0-4.42 3.58-8 8-8 .41 0 .82.04 1.22.1C20.2 9.47 19.6 9 19 8.5c-.9-.74-2-1.5-3.5-1.5-.57 0-1.12.08-1.62.23C14.3 6.5 15.2 6 16.5 6c1.1 0 2.1.35 2.98.9C18.6 5.9 17.3 5 15.75 5c-.7 0-1.38.14-2 .4C13.26 4.54 12 4 10.5 4c-.27 0-.54.02-.8.06C10.17 3.04 11.07 2.5 12 2.5c.65 0 1.26.2 1.77.55z"/>
            </svg>
          </button>
          <button class="social-btn" @click="handleThirdLogin('QQ')">
            <svg width="26" height="26" viewBox="0 0 24 24" fill="#12b7f5">
              <path d="M12 2C6.5 2 2 5.5 2 9.5c0 2.3 1.3 4.3 3.3 5.6-.2.8-.5 1.5-.8 2.1-.4.7-.6 1.1-.6 1.1s.3.1.8.2c.5.1 1.2.2 2.1.1.9-.1 1.8-.4 2.7-.8.5.2 1 .3 1.5.4-1.3 1.1-2.1 2.5-2.1 4 0 3.1 3.1 5.3 7 5.3s7-2.2 7-5.3c0-1.5-.8-2.9-2.1-4 .5-.1 1-.2 1.5-.4.9.4 1.8.7 2.7.8.9.1 1.6 0 2.1-.1.5-.1.8-.2.8-.2s-.2-.4-.6-1.1c-.3-.6-.6-1.3-.8-2.1 2-1.3 3.3-3.3 3.3-5.6C22 5.5 17.5 2 12 2z"/>
            </svg>
          </button>
          <button class="social-btn" @click="handleThirdLogin('Apple')">
            <svg width="26" height="26" viewBox="0 0 24 24" fill="white">
              <path d="M17.05 20.28c-.98.95-2.05.8-3.08.35-1.09-.46-2.09-.48-3.24 0-1.44.62-2.2.44-3.06-.35C2.79 15.25 3.51 7.59 9.05 7.31c1.35.07 2.29.74 3.08.8 1.18-.24 2.31-.93 3.57-.84 1.51.12 2.65.72 3.4 1.8-3.12 1.87-2.6 6 .53 7.16-.62.93-1.42 1.82-2.58 2.05zM12.03 7.25c-.15-2.23 1.66-4.07 3.74-4.25.29 2.58-2.34 4.5-3.74 4.25z"/>
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
