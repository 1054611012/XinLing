<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showLoadingToast, closeToast } from 'vant'
import { startSleep, getSleepRecords } from '@/api/sleep'
import type { SleepRecord } from '@/types/api'
import { usePersistedState } from '@/hooks/usePersistedState'

const router = useRouter()
const isSleeping = usePersistedState<boolean>('sleep_isSleeping', false)
const lastRecord = ref<SleepRecord | null>(null)
const loading = ref(false)

async function loadLastRecord() {
  try {
    loading.value = true
    const res = await getSleepRecords(1, 1)
    if (res.data.records.length > 0) {
      lastRecord.value = res.data.records[0]
    }
  } catch {
    // no records yet
  } finally {
    loading.value = false
  }
}

async function handleStartSleep() {
  const toast = showLoadingToast({
    message: '准备中...',
    forbidClick: true,
    duration: 0
  })
  try {
    await startSleep()
    closeToast()
    isSleeping.value = true
    showToast('开始记录睡眠')
  } catch {
    closeToast()
    showToast('启动失败')
  }
}

function handleWakeUp() {
  isSleeping.value = false
  showToast('起床了，新的一天开始啦')
}

function goAudio() {
  router.push('/audio')
}

onMounted(() => {
  loadLastRecord()
})
</script>

<template>
  <div class="sleep-page">
    <!-- Header -->
    <div class="sleep-header">
      <h1 class="sleep-title">助眠</h1>
      <p class="sleep-subtitle">放松身心，进入深度睡眠</p>
    </div>

    <div class="sleep-content">
      <!-- Start Button -->
      <div class="start-sleep-section" v-if="!isSleeping">
        <div class="sleep-button" @click="handleStartSleep">
          <div class="sleep-button-circle">
            <van-icon name="star-o" size="48" color="#666" />
          </div>
          <div class="sleep-button-text">开始睡眠</div>
        </div>
      </div>

      <!-- Sleeping State -->
      <div class="sleeping-section" v-else>
        <div class="sleeping-icon">
          <van-icon name="clock-o" size="64" color="#666" />
        </div>
        <div class="sleeping-text">正在记录睡眠...</div>
        <button class="wake-btn" @click="handleWakeUp">起床了</button>
      </div>

      <!-- Last Record -->
      <div class="last-record" v-if="lastRecord && !loading">
        <div class="record-header">上次睡眠</div>
        <div class="record-score">
          <span class="score-value">{{ lastRecord.sleepScore || '--' }}</span>
          <span class="score-label">分</span>
        </div>
        <div class="record-details">
          <div class="detail-item">
            <span class="detail-label">时长</span>
            <span class="detail-value">{{ Math.round((lastRecord.duration || 0) / 60) }}<span class="detail-unit">分钟</span></span>
          </div>
          <div class="detail-item">
            <span class="detail-label">深睡</span>
            <span class="detail-value">{{ lastRecord.deepSleepMinutes || 0 }}<span class="detail-unit">分钟</span></span>
          </div>
          <div class="detail-item">
            <span class="detail-label">浅睡</span>
            <span class="detail-value">{{ lastRecord.lightSleepMinutes || 0 }}<span class="detail-unit">分钟</span></span>
          </div>
          <div class="detail-item">
            <span class="detail-label">REM</span>
            <span class="detail-value">{{ lastRecord.remSleepMinutes || 0 }}<span class="detail-unit">分钟</span></span>
          </div>
        </div>
        <div class="sleep-bar">
          <div class="sleep-bar-segment deep" :style="{ flex: lastRecord.deepSleepMinutes || 1 }" />
          <div class="sleep-bar-segment light" :style="{ flex: lastRecord.lightSleepMinutes || 1 }" />
          <div class="sleep-bar-segment rem" :style="{ flex: lastRecord.remSleepMinutes || 1 }" />
        </div>
      </div>

      <!-- Quick Audio -->
      <div class="quick-audio-section">
        <div class="section-label">助眠音频</div>
        <div class="audio-chips">
          <div class="audio-chip" @click="goAudio">
            <van-icon name="cloud-o" size="16" color="#666" />
            <span>雨声</span>
          </div>
          <div class="audio-chip" @click="goAudio">
            <van-icon name="water-o" size="16" color="#666" />
            <span>海浪</span>
          </div>
          <div class="audio-chip" @click="goAudio">
            <van-icon name="volume-o" size="16" color="#666" />
            <span>白噪音</span>
          </div>
          <div class="audio-chip" @click="goAudio">
            <van-icon name="bulb-o" size="16" color="#666" />
            <span>冥想</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.sleep-page {
  min-height: 100vh;
  min-height: 100dvh;
  background: #1a1a2e;
  background: linear-gradient(180deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%);
  padding-bottom: 40px;
}

/* ===== Header ===== */
.sleep-header {
  text-align: center;
  padding: 60px 20px 32px;
}

.sleep-title {
  font-size: 32px;
  font-weight: 300;
  letter-spacing: 4px;
  margin-bottom: 12px;
  color: rgba(255, 255, 255, 0.9);
}

.sleep-subtitle {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.5);
  font-weight: 300;
  letter-spacing: 1px;
}

.sleep-content {
  padding: 0 20px;
}

/* ===== Sleep Button ===== */
.start-sleep-section {
  display: flex;
  justify-content: center;
  padding: 40px 0;
}

.sleep-button {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
  cursor: pointer;
}

.sleep-button-circle {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.08);
  backdrop-filter: blur(10px);
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid rgba(255, 255, 255, 0.12);
  transition: all 0.3s;
}

.sleep-button:active .sleep-button-circle {
  transform: scale(0.95);
  background: rgba(255, 255, 255, 0.15);
}

.sleep-button-text {
  font-size: 16px;
  font-weight: 400;
  color: rgba(255, 255, 255, 0.7);
  letter-spacing: 2px;
}

/* ===== Sleeping State ===== */
.sleeping-section {
  text-align: center;
  padding: 60px 0;
}

.sleeping-icon {
  margin-bottom: 20px;
}

.sleeping-icon .van-icon {
  color: rgba(255, 255, 255, 0.6) !important;
}

.sleeping-text {
  font-size: 18px;
  font-weight: 300;
  color: rgba(255, 255, 255, 0.7);
  margin-bottom: 32px;
  letter-spacing: 2px;
}

.wake-btn {
  padding: 14px 44px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 24px;
  background: rgba(255, 255, 255, 0.06);
  color: rgba(255, 255, 255, 0.8);
  font-size: 15px;
  cursor: pointer;
  transition: all 0.2s;
  font-weight: 300;
  letter-spacing: 1px;
}

.wake-btn:active {
  transform: scale(0.96);
  background: rgba(255, 255, 255, 0.12);
}

/* ===== Last Record ===== */
.last-record {
  background: rgba(255, 255, 255, 0.06);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 16px;
  padding: 24px;
  margin: 24px 0;
}

.record-header {
  font-size: 15px;
  font-weight: 500;
  color: rgba(255, 255, 255, 0.7);
  margin-bottom: 20px;
  letter-spacing: 1px;
}

.record-score {
  text-align: center;
  margin-bottom: 24px;
}

.score-value {
  font-size: 56px;
  font-weight: 200;
  line-height: 1;
  color: rgba(255, 255, 255, 0.9);
}

.score-label {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.4);
  margin-left: 4px;
  font-weight: 300;
}

.record-details {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
  margin-bottom: 20px;
}

.detail-item {
  text-align: center;
}

.detail-label {
  display: block;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.4);
  margin-bottom: 8px;
  font-weight: 300;
}

.detail-value {
  font-size: 18px;
  font-weight: 500;
  color: rgba(255, 255, 255, 0.8);
}

.detail-unit {
  font-size: 11px;
  color: rgba(255, 255, 255, 0.4);
  margin-left: 2px;
  font-weight: 300;
}

.sleep-bar {
  display: flex;
  height: 6px;
  border-radius: 3px;
  overflow: hidden;
  background: rgba(255, 255, 255, 0.06);
}

.sleep-bar-segment.deep {
  background: rgba(108, 99, 255, 0.6);
}

.sleep-bar-segment.light {
  background: rgba(255, 255, 255, 0.3);
}

.sleep-bar-segment.rem {
  background: rgba(255, 255, 255, 0.15);
}

/* ===== Quick Audio ===== */
.quick-audio-section {
  margin-top: 32px;
}

.section-label {
  font-size: 15px;
  font-weight: 500;
  color: rgba(255, 255, 255, 0.7);
  margin-bottom: 16px;
  letter-spacing: 1px;
}

.audio-chips {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.audio-chip {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 18px;
  background: rgba(255, 255, 255, 0.06);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.2s;
  font-size: 14px;
  font-weight: 400;
  color: rgba(255, 255, 255, 0.7);
}

.audio-chip:active {
  transform: scale(0.96);
  background: rgba(255, 255, 255, 0.12);
}
</style>
