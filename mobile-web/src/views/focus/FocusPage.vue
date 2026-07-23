<script setup lang="ts">
import { ref, computed, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showConfirmDialog, Popup, Slider } from 'vant'
import { useFocusStore } from '@/stores/focus'

const router = useRouter()
const focusStore = useFocusStore()

// ===== 场景氛围 =====
const currentScene = ref('🌲')

// ===== 模式配置 =====
const modes = [
  {
    key: 'tomato',
    label: '番茄专注',
    icon: '🍅',
    desc: '经典番茄工作法',
    defaultDuration: 25
  },
  {
    key: 'deep',
    label: '深度专注',
    icon: '🧠',
    desc: '沉浸式深度工作',
    defaultDuration: 90
  },
  {
    key: 'free',
    label: '自由专注',
    icon: '⏱️',
    desc: '自定义专注时长',
    defaultDuration: 60
  }
]

// ===== 时间调整弹窗 =====
const showDurationPicker = ref(false)
const editingModeKey = ref<string>('')
const editingDuration = ref(25)

interface ModeConfig {
  label: string
  min: number
  max: number
  step: number
}

const editingModeConfig: Record<string, ModeConfig> = {
  tomato: { label: '番茄专注', min: 5, max: 60, step: 5 },
  deep:   { label: '深度专注', min: 30, max: 180, step: 10 },
  free:   { label: '自由专注', min: 5, max: 180, step: 5 }
}

function openDurationPicker(modeKey: string) {
  if (focusStore.isRunning || focusStore.isPaused) return
  editingModeKey.value = modeKey
  editingDuration.value = (focusStore.customDurations as Record<string, number>)[modeKey] ?? 
    modes.find(m => m.key === modeKey)?.defaultDuration ?? 25
  showDurationPicker.value = true
}

function confirmDuration() {
  if (editingModeKey.value) {
    focusStore.setCustomDuration(editingModeKey.value, editingDuration.value)
  }
  showDurationPicker.value = false
}

function getDuration(modeKey: string) {
  const d = focusStore.customDurations as Record<string, number>
  const mins = d[modeKey] ?? modes.find(m => m.key === modeKey)?.defaultDuration ?? 25
  return `${mins}分钟`
}

const note = ref('')

async function handleStart(modeKey: string) {
  await focusStore.start(modeKey)
}

async function handlePause() {
  await focusStore.pause()
}

async function handleResume() {
  await focusStore.resume()
}

async function handleEnd() {
  try {
    await showConfirmDialog({
      title: '结束专注',
      message: '确定要结束本次专注吗？',
      confirmButtonColor: '#ee0a24'
    })
    await focusStore.end(note.value)
    showToast('专注结束')
  } catch {
    // cancelled
  }
}

function handleBack() {
  focusStore.reset()
  router.back()
}

const isIdle = () => !focusStore.isRunning && !focusStore.isPaused && focusStore.totalSeconds === 0
const isActive = () => focusStore.isRunning || focusStore.isPaused || (focusStore.totalSeconds > 0 && focusStore.remainingSeconds > 0)
const isComplete = () => !focusStore.isRunning && focusStore.totalSeconds > 0 && focusStore.remainingSeconds <= 0

const activeMode = () => modes.find(m => m.key === focusStore.mode) || modes[0]

// SVG 环形进度
const circleRadius = 120
const circleStroke = 6
const circumference = 2 * Math.PI * circleRadius
const dashOffset = computed(() => {
  const pct = focusStore.progress / 100
  return circumference * (1 - pct)
})

onUnmounted(() => {
  // 不自动 reset，保留持久化状态让页面恢复时继续
})
</script>

<template>
  <div class="focus-page">
    <!-- Header -->
    <div class="focus-header">
      <button v-if="isIdle()" class="header-btn" @click="handleBack">
        <van-icon name="arrow-left" size="20" color="rgba(255,255,255,0.7)" />
      </button>
      <div v-else style="width: 36px;"></div>
      <span class="header-title">{{ isActive() ? activeMode().label : '专注' }}</span>
      <div style="width: 36px;"></div>
    </div>

    <!-- ===== Idle State ===== -->
    <template v-if="isIdle()">
      <div class="idle-content">
        <div class="scene-label">
          <span class="scene-icon">{{ currentScene }}</span>
          <span class="scene-text">选择一个模式开始专注</span>
        </div>

        <div class="mode-list">
          <div
            v-for="m in modes"
            :key="m.key"
            :class="['mode-card', { active: focusStore.mode === m.key }]"
            @click="focusStore.mode = m.key"
          >
            <div class="mode-icon">{{ m.icon }}</div>
            <div class="mode-info">
              <div class="mode-name">{{ m.label }}</div>
              <div class="mode-desc">{{ m.desc }}</div>
            </div>
            <div 
              class="mode-duration" 
              @click.stop="openDurationPicker(m.key)"
            >
              {{ getDuration(m.key) }}
            </div>
          </div>
        </div>

        <!-- 开始按钮 -->
        <div class="start-area">
          <div class="start-button" @click="handleStart(focusStore.mode)">
            <svg width="72" height="72" viewBox="0 0 72 72" fill="none">
              <circle cx="36" cy="36" r="34" stroke="rgba(255,255,255,0.15)" stroke-width="1.5" />
              <path d="M48 36L28 24v24l20-12z" fill="rgba(255,255,255,0.4)" />
            </svg>
          </div>
          <div class="start-label">开始专注</div>
        </div>
      </div>
    </template>

    <!-- ===== Active State ===== -->
    <template v-if="isActive()">
      <div class="active-content">
        <!-- 环形计时器 -->
        <div class="timer-ring-area">
          <svg class="timer-svg" width="280" height="280" viewBox="0 0 280 280">
            <!-- 背景环 -->
            <circle cx="140" cy="140" :r="circleRadius" 
                    fill="none" 
                    stroke="rgba(255,255,255,0.08)" 
                    :stroke-width="circleStroke" />
            <!-- 进度环 -->
            <circle cx="140" cy="140" :r="circleRadius" 
                    fill="none" 
                    stroke="rgba(255,255,255,0.35)" 
                    :stroke-width="circleStroke"
                    stroke-linecap="round"
                    :stroke-dasharray="circumference"
                    :stroke-dashoffset="dashOffset"
                    transform="rotate(-90 140 140)"
                    style="transition: stroke-dashoffset 1s linear;" />
          </svg>
          <div class="timer-center">
            <div class="timer-display">{{ focusStore.formattedTime }}</div>
            <div class="timer-mode-label">{{ activeMode().icon }} {{ activeMode().label }}</div>
            <div class="timer-status" v-if="focusStore.isPaused">已暂停</div>
          </div>
        </div>

        <!-- 控制按钮 -->
        <div class="controls-row">
          <template v-if="focusStore.isRunning && !focusStore.isPaused">
            <button class="ctrl-btn pause" @click="handlePause">
              <van-icon name="pause" size="18" />
              <span>暂停</span>
            </button>
            <button class="ctrl-btn end" @click="handleEnd">
              <van-icon name="stop" size="18" />
              <span>结束</span>
            </button>
          </template>
          <template v-if="focusStore.isPaused">
            <button class="ctrl-btn resume" @click="handleResume">
              <van-icon name="play" size="18" />
              <span>继续</span>
            </button>
            <button class="ctrl-btn end" @click="handleEnd">
              <van-icon name="stop" size="18" />
              <span>结束</span>
            </button>
          </template>
        </div>
      </div>
    </template>

    <!-- ===== Complete State ===== -->
    <template v-if="isComplete()">
      <div class="complete-content">
        <div class="complete-icon">✓</div>
        <div class="complete-title">专注完成</div>
        <div class="complete-subtitle">保持节奏，持续进步</div>
        
        <div class="complete-details">
          <div class="detail-item">
            <span class="detail-label">模式</span>
            <span class="detail-value">{{ activeMode().icon }} {{ activeMode().label }}</span>
          </div>
          <div class="detail-item">
            <span class="detail-label">时长</span>
            <span class="detail-value">{{ Math.round(focusStore.totalSeconds / 60) }}分钟</span>
          </div>
        </div>
        
        <button class="complete-btn" @click="handleBack">
          完成
        </button>
      </div>
    </template>

    <!-- ===== Duration Picker Popup ===== -->
    <Popup
      v-model:show="showDurationPicker"
      position="bottom"
      round
      :style="{ height: '320px' }"
    >
      <div class="duration-popup-header">
        <div class="duration-popup-title">
          {{ editingModeConfig[editingModeKey]?.label }}
        </div>
        <div class="duration-popup-subtitle">调整专注时长</div>
      </div>
      
      <div class="duration-display">
        <span class="duration-value">{{ editingDuration }}</span>
        <span class="duration-unit">分钟</span>
      </div>
      
      <div class="duration-slider-area">
        <Slider
          v-model="editingDuration"
          :min="editingModeConfig[editingModeKey]?.min ?? 5"
          :max="editingModeConfig[editingModeKey]?.max ?? 180"
          :step="editingModeConfig[editingModeKey]?.step ?? 5"
          bar-height="4px"
          active-color="#7c5cff"
        />
        <div class="duration-slider-labels">
          <span>{{ editingModeConfig[editingModeKey]?.min ?? 5 }}分钟</span>
          <span>{{ editingModeConfig[editingModeKey]?.max ?? 180 }}分钟</span>
        </div>
      </div>
      
      <div class="duration-popup-actions">
        <button class="duration-btn cancel" @click="showDurationPicker = false">取消</button>
        <button class="duration-btn confirm" @click="confirmDuration">确定</button>
      </div>
    </Popup>
  </div>
</template>

<style scoped>
.focus-page {
  min-height: 100vh;
  min-height: 100dvh;
  background: linear-gradient(180deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%);
  display: flex;
  flex-direction: column;
  position: relative;
  overflow: hidden;
}

/* ===== 场景背景光晕 ===== */
.focus-page::before {
  content: '';
  position: fixed;
  top: -120px;
  left: 50%;
  transform: translateX(-50%);
  width: 400px;
  height: 400px;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(108, 99, 255, 0.08), transparent 70%);
  pointer-events: none;
}

/* ===== Header ===== */
.focus-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  position: relative;
  z-index: 2;
}

.header-btn {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  border: 1px solid rgba(255,255,255,0.1);
  background: rgba(255,255,255,0.06);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: background 0.2s;
}

.header-btn:active {
  background: rgba(255,255,255,0.12);
}

.header-title {
  font-size: 16px;
  font-weight: 400;
  color: rgba(255,255,255,0.8);
  letter-spacing: 2px;
}

/* ===== Idle State ===== */
.idle-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 0 24px;
  position: relative;
  z-index: 2;
}

.scene-label {
  text-align: center;
  padding: 24px 0 32px;
}

.scene-icon {
  font-size: 32px;
  display: block;
  margin-bottom: 8px;
}

.scene-text {
  font-size: 14px;
  color: rgba(255,255,255,0.4);
  font-weight: 300;
  letter-spacing: 1px;
}

/* Mode list */
.mode-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.mode-card {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 18px 20px;
  background: rgba(255,255,255,0.04);
  backdrop-filter: blur(8px);
  border: 1px solid rgba(255,255,255,0.06);
  border-radius: 16px;
  cursor: pointer;
  transition: all 0.25s;
}

.mode-card:active {
  transform: scale(0.98);
}

.mode-card.active {
  border-color: rgba(255,255,255,0.2);
  background: rgba(255,255,255,0.08);
}

.mode-icon {
  font-size: 28px;
  width: 48px;
  height: 48px;
  border-radius: 14px;
  background: rgba(255,255,255,0.06);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.mode-info {
  flex: 1;
  min-width: 0;
}

.mode-name {
  font-size: 15px;
  font-weight: 500;
  color: rgba(255,255,255,0.85);
  margin-bottom: 2px;
}

.mode-desc {
  font-size: 12px;
  color: rgba(255,255,255,0.35);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.mode-duration {
  flex-shrink: 0;
  padding: 6px 14px;
  border-radius: 20px;
  background: rgba(255,255,255,0.06);
  font-size: 13px;
  font-weight: 400;
  color: rgba(255,255,255,0.5);
  transition: all 0.2s;
  cursor: pointer;
}

.mode-card.active .mode-duration {
  background: rgba(255,255,255,0.12);
  color: rgba(255,255,255,0.8);
}

/* Start button */
.start-area {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 16px;
}

.start-button {
  cursor: pointer;
  transition: all 0.25s;
}

.start-button:active {
  transform: scale(0.9);
}

.start-button svg circle {
  transition: stroke 0.3s;
}

.start-button:active svg circle {
  stroke: rgba(255,255,255,0.3);
}

.start-label {
  font-size: 13px;
  color: rgba(255,255,255,0.35);
  letter-spacing: 3px;
  font-weight: 300;
}

/* ===== Active State ===== */
.active-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 0 20px;
  position: relative;
  z-index: 2;
}

/* 环形计时器 */
.timer-ring-area {
  position: relative;
  width: 280px;
  height: 280px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 24px;
}

.timer-svg {
  position: absolute;
  top: 0;
  left: 0;
}

.timer-center {
  text-align: center;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.timer-display {
  font-size: 64px;
  font-weight: 200;
  letter-spacing: 4px;
  font-variant-numeric: tabular-nums;
  color: rgba(255,255,255,0.9);
  line-height: 1;
  margin-bottom: 12px;
}

.timer-mode-label {
  font-size: 14px;
  color: rgba(255,255,255,0.4);
  font-weight: 300;
}

.timer-status {
  font-size: 14px;
  color: rgba(255, 200, 100, 0.8);
  margin-top: 12px;
  font-weight: 300;
  letter-spacing: 1px;
  animation: pulse 1.5s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 0.6; }
  50% { opacity: 1; }
}

/* Controls */
.controls-row {
  display: flex;
  gap: 16px;
  padding: 20px 20px 40px;
}

.ctrl-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 28px;
  border-radius: 24px;
  border: 1px solid rgba(255,255,255,0.12);
  background: rgba(255,255,255,0.06);
  color: rgba(255,255,255,0.7);
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
  font-weight: 300;
  letter-spacing: 1px;
}

.ctrl-btn:active {
  transform: scale(0.96);
  background: rgba(255,255,255,0.1);
}

.ctrl-btn.pause {
  color: rgba(255,255,255,0.6);
}

.ctrl-btn.resume {
  color: rgba(100, 200, 100, 0.7);
}

.ctrl-btn.end {
  color: rgba(255, 80, 80, 0.6);
}

/* ===== Complete State ===== */
.complete-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 12px;
  padding: 0 20px;
  position: relative;
  z-index: 2;
}

.complete-icon {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: rgba(100, 200, 100, 0.2);
  border: 2px solid rgba(100, 200, 100, 0.3);
  color: rgba(100, 200, 100, 0.8);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 40px;
  font-weight: 200;
  margin-bottom: 16px;
}

.complete-title {
  font-size: 24px;
  font-weight: 300;
  color: rgba(255,255,255,0.8);
  letter-spacing: 3px;
}

.complete-subtitle {
  font-size: 14px;
  color: rgba(255,255,255,0.35);
  font-weight: 300;
}

.complete-details {
  width: 100%;
  max-width: 280px;
  padding: 20px 24px;
  margin-top: 20px;
  background: rgba(255,255,255,0.04);
  border: 1px solid rgba(255,255,255,0.08);
  border-radius: 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.detail-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.detail-label {
  font-size: 14px;
  color: rgba(255,255,255,0.4);
  font-weight: 300;
}

.detail-value {
  font-size: 15px;
  font-weight: 400;
  color: rgba(255,255,255,0.7);
}

.complete-btn {
  margin-top: 24px;
  padding: 14px 56px;
  border-radius: 24px;
  border: 1px solid rgba(255,255,255,0.15);
  background: rgba(255,255,255,0.06);
  color: rgba(255,255,255,0.7);
  font-size: 15px;
  font-weight: 300;
  cursor: pointer;
  transition: all 0.2s;
  letter-spacing: 3px;
}

.complete-btn:active {
  transform: scale(0.96);
  background: rgba(255,255,255,0.1);
}

/* ===== Duration Picker Popup ===== */
.duration-popup-header {
  text-align: center;
  padding-top: 24px;
  padding-bottom: 16px;
}

.duration-popup-title {
  font-size: 18px;
  font-weight: 500;
  color: rgba(255,255,255,0.9);
}

.duration-popup-subtitle {
  font-size: 13px;
  color: rgba(255,255,255,0.4);
  margin-top: 4px;
}

.duration-display {
  display: flex;
  align-items: baseline;
  justify-content: center;
  gap: 4px;
  margin-bottom: 24px;
}

.duration-value {
  font-size: 56px;
  font-weight: 200;
  color: rgba(255,255,255,0.8);
  line-height: 1;
  font-variant-numeric: tabular-nums;
}

.duration-unit {
  font-size: 14px;
  color: rgba(255,255,255,0.4);
}

.duration-slider-area {
  padding: 0 24px;
  margin-bottom: 32px;
}

.duration-slider-labels {
  display: flex;
  justify-content: space-between;
  margin-top: 8px;
  font-size: 12px;
  color: rgba(255,255,255,0.3);
}

.duration-popup-actions {
  display: flex;
  gap: 12px;
  padding: 0 20px 32px;
}

.duration-btn {
  flex: 1;
  padding: 14px 0;
  border-radius: 24px;
  border: 1px solid rgba(255,255,255,0.1);
  background: rgba(255,255,255,0.04);
  color: rgba(255,255,255,0.6);
  font-size: 15px;
  cursor: pointer;
  transition: all 0.2s;
  font-weight: 300;
}

.duration-btn:active {
  transform: scale(0.96);
}

.duration-btn.confirm {
  background: rgba(108, 99, 255, 0.2);
  color: rgba(255,255,255,0.8);
  border: none;
}
</style>
