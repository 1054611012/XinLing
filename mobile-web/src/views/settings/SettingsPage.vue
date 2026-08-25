<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showLoadingToast, closeToast } from 'vant'
import { getSettings, updateSettings } from '@/api/user'
import { isDark, toggleDarkMode } from '@/hooks/useDarkMode'
import type { UserSettings } from '@/types/api'

const router = useRouter()
const settings = ref<UserSettings>({
  defaultFocusTime: 25,
  defaultBreakTime: 5,
  defaultAudioId: null,
  darkMode: 1,
  notification: 1,
  volume: 70,
  aiVoiceId: ''
})
const loading = ref(false)

// 监听深色模式切换，实时生效
watch(() => settings.value.darkMode, (val) => {
  toggleDarkMode(val === 1 || val === 2)
}, { immediate: false })

async function loadSettings() {
  loading.value = true
  try {
    const res = await getSettings()
    settings.value = { ...settings.value, ...res.data }
    // 加载后应用已保存的深色模式
    toggleDarkMode(res.data.darkMode === 1 || res.data.darkMode === 2)
  } catch {
    // use defaults
  } finally {
    loading.value = false
  }
}

async function saveSettings() {
  const toast = showLoadingToast({ message: '保存中...', forbidClick: true, duration: 0 })
  try {
    await updateSettings(settings.value)
    closeToast()
    showToast('设置已保存')
  } catch {
    closeToast()
    showToast('保存失败')
  }
}

onMounted(() => { loadSettings() })
</script>

<template>
  <div class="page settings-page">
    <van-nav-bar title="偏好设置" left-arrow @click-left="router.back()" />

    <div class="settings-content">
      <!-- Focus Settings -->
      <van-cell-group :border="false" class="settings-group">
        <template #title>
          <span class="group-title">专注设置</span>
        </template>
        <van-cell center title="默认专注时长" :border="false">
          <template #value>
            <van-stepper
              v-model="settings.defaultFocusTime"
              min="5"
              max="120"
              step="5"
              button-size="28"
              input-width="50"
              theme="round"
            />
          </template>
        </van-cell>
        <van-cell center title="默认休息时长" :border="false">
          <template #value>
            <van-stepper
              v-model="settings.defaultBreakTime"
              min="1"
              max="30"
              step="1"
              button-size="28"
              input-width="50"
              theme="round"
            />
          </template>
        </van-cell>
      </van-cell-group>

      <!-- Sound Settings -->
      <van-cell-group :border="false" class="settings-group">
        <template #title>
          <span class="group-title">声音设置</span>
        </template>
        <van-cell center title="默认音量" :border="false">
          <template #value>
            <div class="volume-control">
              <span class="volume-icon"><AppIcon name="volume-o" size="16" /></span>
              <van-slider
                v-model="settings.volume"
                :min="0"
                :max="100"
                bar-color="#7c5cff"
                active-color="#7c5cff"
                style="width: 120px;"
              />
              <span class="volume-text">{{ settings.volume }}%</span>
            </div>
          </template>
        </van-cell>
      </van-cell-group>

      <!-- Display Settings -->
      <van-cell-group :border="false" class="settings-group">
        <template #title>
          <span class="group-title">显示设置</span>
        </template>
        <van-cell center title="深色模式" :border="false">
          <template #value>
            <van-switch v-model="settings.darkMode" :active-value="1" :inactive-value="0" active-color="#7c5cff" />
          </template>
        </van-cell>
      </van-cell-group>

      <!-- Notification Settings -->
      <van-cell-group :border="false" class="settings-group">
        <template #title>
          <span class="group-title">通知设置</span>
        </template>
        <van-cell center title="消息通知" :border="false">
          <template #value>
            <van-switch v-model="settings.notification" active-value="1" inactive-value="0" active-color="#7c5cff" />
          </template>
        </van-cell>
      </van-cell-group>

      <!-- Save Button -->
      <div class="save-section">
        <van-button
          round
          block
          type="primary"
          @click="saveSettings"
        >
          保存设置
        </van-button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.settings-page {
  min-height: 100vh;
  min-height: 100dvh;
}

.settings-content {
  padding-bottom: 40px;
}

.settings-group {
  margin: 12px 16px;
  border-radius: var(--app-radius-md);
  overflow: hidden;
  background: var(--app-glass-bg);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid var(--app-glass-border);
  box-shadow: var(--app-glass-shadow);
}

.settings-group :deep(.van-cell-group__title) {
  padding: 16px 16px 8px;
}

.group-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--app-accent);
}

.settings-group :deep(.van-cell) {
  background: transparent;
}

.settings-group :deep(.van-cell__title) {
  color: var(--app-text-primary);
}

.settings-group :deep(.van-cell__value) {
  color: var(--app-text-secondary);
}

.volume-control {
  display: flex;
  align-items: center;
  gap: 8px;
}

.volume-icon {
  color: var(--app-text-secondary);
  display: flex;
}

.volume-text {
  font-size: 12px;
  color: var(--app-text-secondary);
  min-width: 30px;
  text-align: right;
}

.save-section {
  padding: 24px 16px;
}

</style>
