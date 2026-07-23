<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { showToast, showLoadingToast, closeToast } from 'vant'
import { getChallengeProgress, dailyCheckin } from '@/api/social'

const router = useRouter()
const route = useRoute()

const challengeId = Number(route.params.id)
const progress = ref<any>(null)
const loading = ref(false)
const checkining = ref(false)

const totalDays = computed(() => progress.value?.totalDays || progress.value?.duration || 1)
const completed = computed(() => progress.value?.completedDays || 0)
const percent = computed(() => Math.min(100, Math.round(completed.value / totalDays.value * 100)))
const isCompleted = computed(() => progress.value?.userStatus === 1)
const canCheckin = computed(() => !isCompleted.value && !checkining.value)

const CIRCUMFERENCE = 2 * Math.PI * 54

async function loadData() {
  loading.value = true
  const toast = showLoadingToast({ message: '加载中...', forbidClick: true, duration: 0 })
  try {
    const res = await getChallengeProgress(challengeId)
    progress.value = res.data
  } catch {
    showToast('加载失败')
  } finally {
    closeToast()
    loading.value = false
  }
}

async function handleCheckin() {
  if (!canCheckin.value) return
  checkining.value = true
  const toast = showLoadingToast({ message: '打卡中...', forbidClick: true, duration: 0 })
  try {
    await dailyCheckin(challengeId)
    closeToast()
    showToast('✅ 打卡成功')
    loadData()
  } catch (e: any) {
    closeToast()
    showToast(e?.response?.data?.message || e?.message || '打卡失败')
  } finally {
    checkining.value = false
  }
}

onMounted(() => { loadData() })
</script>

<template>
  <div class="page challenge-detail-page">
    <van-nav-bar title="挑战详情" left-arrow @click-left="router.back()" />

    <div class="detail-content" v-if="progress">
      <!-- Progress ring card -->
      <div class="glass-card progress-card">
        <div class="ring-section">
          <svg width="140" height="140" viewBox="0 0 140 140">
            <circle cx="70" cy="70" r="54" fill="none" stroke="var(--app-glass-border)" stroke-width="6" />
            <circle
              cx="70" cy="70" r="54"
              fill="none"
              stroke="var(--app-accent)"
              stroke-width="6"
              stroke-linecap="round"
              :stroke-dasharray="CIRCUMFERENCE"
              :stroke-dashoffset="CIRCUMFERENCE * (1 - percent / 100)"
              transform="rotate(-90 70 70)"
              class="progress-arc"
            />
          </svg>
          <div class="ring-center">
            <div class="ring-pct">{{ percent }}%</div>
            <div class="ring-lbl">完成</div>
          </div>
        </div>
        <div class="ring-title">{{ progress.title }}</div>
        <div class="ring-desc">{{ progress.description }}</div>
        <div class="stats-row">
          <div class="stat">
            <span class="stat-val">{{ completed }}</span>
            <span class="stat-lbl">已完成</span>
          </div>
          <div class="stat-divider" />
          <div class="stat">
            <span class="stat-val">{{ totalDays }}</span>
            <span class="stat-lbl">总天数</span>
          </div>
          <div class="stat-divider" />
          <div class="stat">
            <span class="stat-val">{{ totalDays - completed }}</span>
            <span class="stat-lbl">剩余</span>
          </div>
        </div>
      </div>

      <!-- Checkin button -->
      <van-button
        round
        block
        :type="isCompleted ? 'default' : 'primary'"
        :disabled="!canCheckin"
        :loading="checkining"
        @click="handleCheckin"
        class="checkin-btn"
      >
        {{ isCompleted ? '🎉 挑战已完成' : '📅 今日打卡' }}
      </van-button>

      <!-- Rewards -->
      <div class="glass-card reward-card">
        <div class="reward-title">🏆 挑战奖励</div>
        <div class="reward-list">
          <div class="reward-item" v-if="progress.pointsReward">
            <div class="reward-icon reward-icon-points">⭐</div>
            <div class="reward-info">
              <div class="reward-name">{{ progress.pointsReward }} 积分</div>
              <div class="reward-hint">挑战完成后自动发放</div>
            </div>
          </div>
          <div class="reward-item" v-if="progress.vipDaysReward">
            <div class="reward-icon reward-icon-vip">👑</div>
            <div class="reward-info">
              <div class="reward-name">VIP {{ progress.vipDaysReward }} 天</div>
              <div class="reward-hint">挑战完成后自动发放</div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <van-empty v-else-if="!loading" description="挑战不存在或已结束" />
  </div>
</template>

<style scoped>
.challenge-detail-page {
  min-height: 100vh;
  min-height: 100dvh;
}
.detail-content {
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 14px;
}

/* Ring card */
.progress-card {
  padding: 28px 20px 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  animation: fade-up 0.5s ease both;
}
.ring-section {
  position: relative;
  width: 140px;
  height: 140px;
  margin-bottom: 20px;
}
.progress-arc {
  transition: stroke-dashoffset 0.8s ease;
  filter: drop-shadow(0 0 6px rgba(124, 92, 255, 0.2));
}
.ring-center {
  position: absolute;
  inset: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}
.ring-pct {
  font-size: 28px;
  font-weight: 700;
  background: var(--app-gradient-2);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}
.ring-lbl {
  font-size: 11px;
  color: var(--app-text-secondary);
  margin-top: 2px;
}
.ring-title {
  font-size: 18px;
  font-weight: 700;
  margin-bottom: 6px;
  text-align: center;
}
.ring-desc {
  font-size: 13px;
  color: var(--app-text-secondary);
  text-align: center;
  margin-bottom: 20px;
  line-height: 1.5;
}
.stats-row {
  display: flex;
  align-items: center;
  gap: 20px;
  width: 100%;
  justify-content: center;
}
.stat { text-align: center; }
.stat-val {
  display: block;
  font-size: 18px;
  font-weight: 700;
}
.stat-lbl {
  display: block;
  font-size: 11px;
  color: var(--app-text-secondary);
  margin-top: 2px;
}
.stat-divider {
  width: 1px;
  height: 28px;
  background: var(--app-glass-border);
}

/* Checkin button */
.checkin-btn {
  height: 46px;
  font-size: 16px;
  font-weight: 500;
  animation: fade-up 0.5s 0.08s ease both;
}

/* Reward card */
.reward-card {
  padding: 20px;
  animation: fade-up 0.5s 0.15s ease both;
}
.reward-title {
  font-size: 15px;
  font-weight: 600;
  margin-bottom: 14px;
}
.reward-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.reward-item {
  display: flex;
  align-items: center;
  gap: 12px;
}
.reward-icon {
  width: 40px;
  height: 40px;
  border-radius: var(--app-radius-sm);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  flex-shrink: 0;
}
.reward-icon-points { background: rgba(124, 92, 255, 0.08); }
.reward-icon-vip { background: rgba(230, 162, 60, 0.08); }
.reward-info { flex: 1; }
.reward-name { font-size: 14px; font-weight: 500; }
.reward-hint { font-size: 11px; color: var(--app-text-secondary); margin-top: 2px; }

@keyframes fade-up {
  from { opacity: 0; transform: translateY(14px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>
