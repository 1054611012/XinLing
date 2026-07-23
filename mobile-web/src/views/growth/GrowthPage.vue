<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showLoadingToast, closeToast } from 'vant'
import { getGrowthInfo } from '@/api/growth'
import type { GrowthInfo } from '@/api/growth'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()
const growthInfo = ref<GrowthInfo | null>(null)
const loading = ref(false)

async function loadData() {
  loading.value = true
  const toast = showLoadingToast({ message: '加载中...', forbidClick: true, duration: 0 })
  try {
    const res = await getGrowthInfo()
    growthInfo.value = res.data
  } catch {
    // use defaults
  } finally {
    closeToast()
    loading.value = false
  }
}

function goTo(path: string) {
  router.push(path)
}

onMounted(() => { loadData() })
</script>

<template>
  <div class="page growth-page">
    <!-- Header -->
    <van-nav-bar title="成长中心" left-arrow @click-left="router.back()" />

    <!-- Level Card -->
    <div class="level-card glass-card">
      <div class="level-header">
        <div class="level-icon">
          <svg width="48" height="48" viewBox="0 0 48 48" fill="none">
            <circle cx="24" cy="10" r="8" fill="url(#g-grad)" opacity="0.3" />
            <path d="M12 38V28l12-6 12 6v10" stroke="url(#g-grad)" stroke-width="2" fill="none" stroke-linecap="round" />
            <defs>
              <linearGradient id="g-grad" x1="0" y1="0" x2="48" y2="48">
                <stop stop-color="#7c5cff" /><stop offset="1" stop-color="#00c896" />
              </linearGradient>
            </defs>
          </svg>
        </div>
        <div class="level-info">
          <div class="level-value">Lv.{{ growthInfo?.level || 1 }}</div>
          <div class="level-name">{{ growthInfo?.levelName || '初心者' }}</div>
        </div>
      </div>
      <div class="exp-bar-wrapper">
        <div class="exp-bar">
          <div class="exp-fill" :style="{ width: ((growthInfo?.exp ?? 0) / (growthInfo?.nextLevelExp ?? 100) * 100) + '%' }" />
        </div>
        <div class="exp-text">{{ growthInfo?.exp || 0 }}/{{ growthInfo?.nextLevelExp || 100 }}</div>
      </div>
      <div class="level-points">
        <div class="points-item">
          <span class="points-value gradient-text">{{ growthInfo?.points || 0 }}</span>
          <span class="points-label">积分</span>
        </div>
        <div class="points-item">
          <span class="points-value gradient-text">{{ growthInfo?.continuousFocusDays || 0 }}</span>
          <span class="points-label">连续专注(天)</span>
        </div>
        <div class="points-item">
          <span class="points-value gradient-text">{{ growthInfo?.continuousSleepDays || 0 }}</span>
          <span class="points-label">连续睡眠(天)</span>
        </div>
      </div>
    </div>

    <!-- Menu Grid -->
    <div class="menu-grid">
      <div class="menu-item glass-card" @click="goTo('/achievement')">
        <svg class="menu-icon-svg" width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="#f5af19" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
          <circle cx="12" cy="8" r="6" />
          <path d="M15.477 12.89L17 22l-5-3-5 3 1.523-9.11" />
        </svg>
        <div class="menu-label">成就</div>
      </div>
      <div class="menu-item glass-card" @click="goTo('/task')">
        <svg class="menu-icon-svg" width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="#7c5cff" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
          <path d="M9 11l3 3L22 4" />
          <path d="M21 12v7a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11" />
        </svg>
        <div class="menu-label">每日任务</div>
      </div>
      <div class="menu-item glass-card" @click="goTo('/mall')">
        <svg class="menu-icon-svg" width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="#00c896" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
          <path d="M6 2L3 6v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V6l-3-4z" />
          <line x1="3" y1="6" x2="21" y2="6" />
          <path d="M16 10a4 4 0 0 1-8 0" />
        </svg>
        <div class="menu-label">积分商城</div>
      </div>
      <div class="menu-item glass-card" @click="goTo('/rank')">
        <svg class="menu-icon-svg" width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="#ff6b6b" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
          <line x1="18" y1="20" x2="18" y2="10" />
          <line x1="12" y1="20" x2="12" y2="4" />
          <line x1="6" y1="20" x2="6" y2="14" />
        </svg>
        <div class="menu-label">排行榜</div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.growth-page {
  min-height: 100vh;
  min-height: 100dvh;
}

.level-card {
  margin: 16px;
  padding: 24px 20px;
}

.level-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 20px;
}

.level-value {
  font-size: 28px;
  font-weight: 800;
  background: linear-gradient(135deg, #7c5cff, #00c896);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.level-name {
  font-size: 14px;
  color: var(--app-text-secondary);
  margin-top: 2px;
}

.exp-bar-wrapper {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
}

.exp-bar {
  flex: 1;
  height: 6px;
  background: rgba(255,255,255,0.06);
  border-radius: 3px;
  overflow: hidden;
}

.exp-fill {
  height: 100%;
  background: linear-gradient(90deg, #7c5cff, #00c896);
  border-radius: 3px;
  transition: width 0.5s ease;
}

.exp-text {
  font-size: 12px;
  color: var(--app-text-secondary);
  white-space: nowrap;
}

.level-points {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
  text-align: center;
}

.points-value {
  font-size: 20px;
  font-weight: 700;
}

.points-label {
  display: block;
  font-size: 11px;
  color: var(--app-text-secondary);
  margin-top: 2px;
}

.menu-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
  padding: 0 16px;
}

.menu-item {
  padding: 20px 12px;
  text-align: center;
  cursor: pointer;
}

.menu-icon-svg {
  margin-bottom: 8px;
  opacity: 0.9;
}

.menu-label {
  font-size: 12px;
  color: var(--app-text-secondary);
}
</style>
