<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showConfirmDialog, showLoadingToast, closeToast } from 'vant'
import { getDevices, logoutDevice } from '@/api/user'
import type { UserDevice } from '@/types/api'

const router = useRouter()
const devices = ref<UserDevice[]>([])
const loading = ref(false)
const currentDeviceId = ref<string | number>('')

async function loadData() {
  loading.value = true
  const toast = showLoadingToast({ message: '加载中...', forbidClick: true, duration: 0 })
  try {
    const res = await getDevices()
    devices.value = res.data || []
  } catch {
    showToast('加载失败')
  } finally {
    closeToast()
    loading.value = false
  }
}

async function handleLogout(deviceId: number | string, deviceName: string) {
  try {
    await showConfirmDialog({
      title: '提示',
      message: `确定要登出设备「${deviceName}」吗？`,
      confirmButtonColor: '#ee0a24'
    })
    const toast = showLoadingToast({ message: '处理中...', forbidClick: true, duration: 0 })
    await logoutDevice(deviceId)
    closeToast()
    showToast('已登出该设备')
    loadData()
  } catch {
    // cancelled
  }
}

function getDeviceIcon(type: string): string {
  const map: Record<string, string> = {
    Android: 'android',
    iOS: 'apple',
    Web: 'desktop-o'
  }
  return map[type] || 'phone-o'
}

onMounted(() => { loadData() })
</script>

<template>
  <div class="page device-page">
    <van-nav-bar title="设备管理" left-arrow @click-left="router.back()" />

    <div class="device-tip">
      <AppIcon name="info-o" color="#8888aa" size="16" />
      <span>当前设备无法登出</span>
    </div>

    <div class="device-list">
      <div
        v-for="device in devices"
        :key="device.id"
        class="device-card glass-card"
      >
        <div class="device-icon">
          <AppIcon :name="getDeviceIcon(device.deviceType)" size="32" color="#7c5cff" />
        </div>
        <div class="device-info">
          <div class="device-name">
            {{ device.deviceName }}
            <van-tag v-if="device.deviceId === currentDeviceId" color="#00c896" style="margin-left: 6px;">当前</van-tag>
          </div>
          <div class="device-meta">
            <span>{{ device.deviceType }}</span>
            <span>·</span>
            <span>{{ device.lastActiveTime || device.loginTime }}</span>
          </div>
        </div>
        <van-button
          v-if="device.deviceId !== currentDeviceId"
          round
          size="small"
          plain
          color="#ee0a24"
          @click="handleLogout(device.deviceId, device.deviceName)"
        >
          登出
        </van-button>
      </div>

      <van-empty v-if="!loading && devices.length === 0" description="暂无设备信息" />
    </div>
  </div>
</template>

<style scoped>
.device-page {
  min-height: 100vh;
  min-height: 100dvh;
}

.device-tip {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 12px 16px;
  font-size: 12px;
  color: var(--app-text-secondary);
  background: rgba(255, 255, 255, 0.03);
}

.device-list {
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.device-card {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 16px;
}

.device-icon {
  flex-shrink: 0;
}

.device-info {
  flex: 1;
  min-width: 0;
}

.device-name {
  font-size: 15px;
  font-weight: 500;
  display: flex;
  align-items: center;
  margin-bottom: 4px;
}

.device-meta {
  display: flex;
  gap: 6px;
  font-size: 12px;
  color: var(--app-text-secondary);
}
</style>
