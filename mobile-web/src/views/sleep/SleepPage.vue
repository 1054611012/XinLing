<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showLoadingToast, closeToast } from 'vant'
import { startSleep, getSleepRecords } from '@/api/sleep'
import type { SleepRecord } from '@/types/api'
import { usePersistedState } from '@/hooks/usePersistedState'
import SleepTabBar from './SleepTabBar.vue'

const router = useRouter()
const isSleeping = usePersistedState<boolean>('sleep_isSleeping', false)
const lastRecord = ref<SleepRecord | null>(null)
const loading = ref(false)

const weekdayMap = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六']
const now = new Date()
const todayLabel = computed(() => {
  const m = now.getMonth() + 1
  const d = now.getDate()
  const w = weekdayMap[now.getDay()]
  return `${m} 月 ${d} 日 · ${w} · 夜色温柔`
})

// 睡眠分环：半径 54，周长 ≈ 339.29
const scoreRing = computed(() => {
  const r = 54
  const c = 2 * Math.PI * r
  const score = lastRecord.value?.sleepScore || 0
  const offset = c * (1 - Math.min(Math.max(score, 0), 100) / 100)
  return { r, c, offset }
})

// 睡眠结构条比例
const sleepBar = computed(() => {
  const rec = lastRecord.value
  const deep = rec?.deepSleepMinutes || 0
  const light = rec?.lightSleepMinutes || 0
  const rem = rec?.remSleepMinutes || 0
  const total = deep + light + rem || 1
  return {
    deep: (deep / total) * 100,
    light: (light / total) * 100,
    rem: (rem / total) * 100
  }
})

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

function goReport() {
  router.push('/sleep/report')
}

onMounted(() => {
  loadLastRecord()
})
</script>

<template>
  <div class="sleep-page">
    <!-- 真实极光生图背景 -->
    <div class="aurora" />
    <div class="scrim" />

    <div class="content">
      <!-- 问候 -->
      <div class="greet">
        <div class="hi">今夜，<br />让心慢慢沉下来</div>
        <div class="date">{{ todayLabel }}</div>
      </div>

      <!-- 睡眠中状态 / 今夜睡眠计划 -->
      <div v-if="!isSleeping" class="plan glass">
        <div class="pl-top">
          <span class="pl-label">今夜睡眠计划</span>
          <span class="pl-tag">AI 已为你定制</span>
        </div>
        <div class="timeline">
          <div class="node">
            <div class="dot" />
            <div class="t">22:30</div>
            <div class="sub">入睡</div>
          </div>
          <div class="line" />
          <div class="node">
            <div class="dot" />
            <div class="t">4 段</div>
            <div class="sub">引导冥想</div>
          </div>
          <div class="line" />
          <div class="node">
            <div class="dot" />
            <div class="t">06:15</div>
            <div class="sub">唤醒</div>
          </div>
        </div>
        <button class="start" @click="handleStartSleep">开始今夜仪式</button>
      </div>

      <div v-else class="plan glass sleeping">
        <div class="sleeping-moon">
          <svg viewBox="0 0 24 24" fill="none" stroke="#A78BFA" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round">
            <path d="M21 12.8A9 9 0 1 1 11.2 3a7 7 0 0 0 9.8 9.8Z" />
          </svg>
        </div>
        <div class="sleeping-title">正在记录睡眠…</div>
        <div class="sleeping-sub">愿你拥有温柔的好梦</div>
        <button class="start wake" @click="handleWakeUp">起床了</button>
      </div>

      <!-- 上次睡眠 · 报告卡 -->
      <template v-if="lastRecord && !loading">
        <div class="sec-title">昨晚睡眠 <span class="more" @click="goReport">查看趋势 ›</span></div>
        <div class="report glass">
          <div class="rep-top">
            <div class="rep-title">睡眠评分</div>
            <div class="rep-badge">状态良好</div>
          </div>
          <div class="rep-body">
            <div class="ring-wrap">
              <svg viewBox="0 0 120 120" class="ring">
                <defs>
                  <linearGradient id="scoreGrad" x1="0" y1="0" x2="1" y2="1">
                    <stop offset="0" stop-color="#A78BFA" />
                    <stop offset="1" stop-color="#6366F1" />
                  </linearGradient>
                </defs>
                <circle class="ring-bg" cx="60" cy="60" :r="scoreRing.r" />
                <circle
                  class="ring-fg"
                  cx="60"
                  cy="60"
                  :r="scoreRing.r"
                  :stroke-dasharray="scoreRing.c"
                  :stroke-dashoffset="scoreRing.offset"
                  transform="rotate(-90 60 60)"
                />
                <text class="ring-num" x="60" y="58">{{ lastRecord.sleepScore || '--' }}</text>
                <text class="ring-cap" x="60" y="76">睡眠分</text>
              </svg>
            </div>
            <div class="rep-metrics">
              <div class="rm">
                <div class="rmv">{{ Math.round((lastRecord.duration || 0) / 60) }}<span>分</span></div>
                <div class="rml">时长</div>
              </div>
              <div class="rm">
                <div class="rmv">{{ lastRecord.deepSleepMinutes || 0 }}<span>分</span></div>
                <div class="rml">深睡</div>
              </div>
              <div class="rm">
                <div class="rmv">{{ lastRecord.lightSleepMinutes || 0 }}<span>分</span></div>
                <div class="rml">浅睡</div>
              </div>
              <div class="rm">
                <div class="rmv">{{ lastRecord.remSleepMinutes || 0 }}<span>分</span></div>
                <div class="rml">REM</div>
              </div>
            </div>
          </div>
          <div class="stage-label">睡眠结构</div>
          <div class="sleep-bar">
            <div class="seg deep" :style="{ width: sleepBar.deep + '%' }" />
            <div class="seg light" :style="{ width: sleepBar.light + '%' }" />
            <div class="seg rem" :style="{ width: sleepBar.rem + '%' }" />
          </div>
          <div class="bar-legend">
            <span><i class="dot deep" />深睡</span>
            <span><i class="dot light" />浅睡</span>
            <span><i class="dot rem" />REM</span>
          </div>
        </div>
      </template>

      <!-- 为你推荐 -->
      <div class="sec-title">
        为你推荐 <span class="more" @click="goAudio">查看全部 ›</span>
      </div>
      <div class="rec-row">
        <div class="rec" @click="goAudio">
          <div class="thumb">
            <svg viewBox="0 0 110 74" preserveAspectRatio="none">
              <defs>
                <linearGradient id="r1" x1="0" y1="0" x2="1" y2="1">
                  <stop offset="0" stop-color="#7C3AED" />
                  <stop offset="1" stop-color="#4338CA" />
                </linearGradient>
              </defs>
              <rect width="110" height="74" fill="url(#r1)" />
              <circle cx="78" cy="20" r="22" fill="#A78BFA" opacity=".5" />
              <path d="M0 74 Q30 40 55 56 T110 44 V74Z" fill="#0F172A" opacity=".45" />
            </svg>
          </div>
          <div class="meta">
            <div class="rt">深度放松冥想</div>
            <div class="rd">12 分钟 · 舒缓</div>
          </div>
        </div>
        <div class="rec" @click="goAudio">
          <div class="thumb">
            <svg viewBox="0 0 110 74" preserveAspectRatio="none">
              <defs>
                <linearGradient id="r2" x1="0" y1="0" x2="1" y2="1">
                  <stop offset="0" stop-color="#6366F1" />
                  <stop offset="1" stop-color="#4338CA" />
                </linearGradient>
              </defs>
              <rect width="110" height="74" fill="url(#r2)" />
              <path d="M0 40 Q18 24 36 40 T72 40 T108 40 V74 H0Z" fill="#A78BFA" opacity=".45" />
              <path d="M0 54 Q18 38 36 54 T72 54 T108 54 V74 H0Z" fill="#fff" opacity=".22" />
            </svg>
          </div>
          <div class="meta">
            <div class="rt">雨落屋檐</div>
            <div class="rd">白噪音 · 循环</div>
          </div>
        </div>
        <div class="rec" @click="goAudio">
          <div class="thumb">
            <svg viewBox="0 0 110 74" preserveAspectRatio="none">
              <defs>
                <linearGradient id="r3" x1="0" y1="0" x2="1" y2="1">
                  <stop offset="0" stop-color="#A78BFA" />
                  <stop offset="1" stop-color="#5B21B6" />
                </linearGradient>
              </defs>
              <rect width="110" height="74" fill="url(#r3)" />
              <circle cx="30" cy="26" r="14" fill="#fff" opacity=".35" />
              <circle cx="82" cy="44" r="18" fill="#fff" opacity=".22" />
            </svg>
          </div>
          <div class="meta">
            <div class="rt">睡前故事·星河</div>
            <div class="rd">18 分钟 · 助眠</div>
          </div>
        </div>
      </div>

      <div class="spacer" />
    </div>

    <SleepTabBar />
  </div>
</template>

<style scoped>
.sleep-page {
  position: relative;
  min-height: 100vh;
  min-height: 100dvh;
  overflow: hidden;
  --indigo: #4338ca;
  --indigo2: #6366f1;
  --purple: #7c3aed;
  --lilac: #a78bfa;
  --card: #192134;
  --sub: #94a3b8;
  --glass: rgba(255, 255, 255, 0.08);
  --glass-stroke: rgba(255, 255, 255, 0.12);
  --font-title: 'Lora', 'Songti SC', 'Noto Serif SC', serif;
  --font-body: 'Raleway', -apple-system, 'PingFang SC', 'Microsoft YaHei', sans-serif;
}

/* ===== 极光生图背景 ===== */
.aurora {
  position: absolute;
  inset: 0;
  z-index: 0;
  background: url('/aurora.png') center / cover no-repeat;
}
.scrim {
  position: absolute;
  inset: 0;
  z-index: 1;
  background: linear-gradient(
    180deg,
    rgba(15, 23, 42, 0.12) 0%,
    rgba(15, 23, 42, 0.32) 38%,
    rgba(15, 23, 42, 0.85) 76%,
    rgba(15, 23, 42, 0.97) 100%
  );
}

.content {
  position: relative;
  z-index: 10;
  padding: calc(env(safe-area-inset-top, 0px) + 58px) 22px 100px;
  display: flex;
  flex-direction: column;
  min-height: 100dvh;
  box-sizing: border-box;
}

.glass {
  background: var(--glass);
  border: 1px solid var(--glass-stroke);
  backdrop-filter: blur(22px);
  -webkit-backdrop-filter: blur(22px);
}

/* ===== 问候 ===== */
.greet {
  margin-bottom: 18px;
}
.greet .hi {
  font-family: var(--font-title);
  font-weight: 600;
  font-size: 28px;
  line-height: 1.35;
  color: #fff;
}
.greet .date {
  color: var(--sub);
  font-size: 12.5px;
  margin-top: 8px;
  letter-spacing: 0.4px;
  font-family: var(--font-body);
}

/* ===== 今夜睡眠计划 ===== */
.plan {
  padding: 18px;
  border-radius: 22px;
  margin-bottom: 20px;
  background: linear-gradient(160deg, rgba(124, 58, 237, 0.16), rgba(255, 255, 255, 0.04));
}
.plan .pl-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.plan .pl-label {
  font-size: 12px;
  color: var(--lilac);
  letter-spacing: 2px;
  font-weight: 600;
}
.plan .pl-tag {
  font-size: 11px;
  color: var(--sub);
  border: 1px solid var(--glass-stroke);
  padding: 3px 9px;
  border-radius: 20px;
}
.timeline {
  display: flex;
  align-items: center;
  gap: 6px;
  margin: 18px 0 4px;
}
.timeline .node {
  flex: 1;
  text-align: center;
}
.timeline .dot {
  width: 9px;
  height: 9px;
  border-radius: 50%;
  background: var(--lilac);
  margin: 0 auto 7px;
  box-shadow: 0 0 10px rgba(167, 139, 250, 0.8);
}
.timeline .t {
  font-size: 11.5px;
  color: #fff;
  font-weight: 600;
}
.timeline .sub {
  font-size: 10px;
  color: var(--sub);
  margin-top: 2px;
}
.timeline .line {
  flex: 0 0 14px;
  height: 1px;
  background: linear-gradient(90deg, var(--lilac), transparent);
}
.plan .start {
  width: 100%;
  margin-top: 16px;
  background: linear-gradient(90deg, var(--purple), var(--indigo2));
  border: none;
  color: #fff;
  font-family: var(--font-body);
  font-weight: 600;
  font-size: 14px;
  padding: 13px;
  border-radius: 14px;
  letter-spacing: 0.5px;
  box-shadow: 0 10px 26px rgba(124, 58, 237, 0.35);
  cursor: pointer;
  transition: transform 0.15s ease, box-shadow 0.2s ease;
  -webkit-tap-highlight-color: transparent;
}
.plan .start:active {
  transform: scale(0.97);
  box-shadow: 0 6px 18px rgba(124, 58, 237, 0.45);
}

/* 睡眠中状态 */
.plan.sleeping {
  text-align: center;
  padding: 26px 18px 22px;
}
.sleeping-moon {
  width: 56px;
  height: 56px;
  margin: 0 auto 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background: rgba(167, 139, 250, 0.14);
  border: 1px solid rgba(167, 139, 250, 0.3);
}
.sleeping-moon svg {
  width: 28px;
  height: 28px;
}
.sleeping-title {
  font-family: var(--font-title);
  font-size: 19px;
  font-weight: 600;
  color: #fff;
}
.sleeping-sub {
  font-size: 12.5px;
  color: var(--sub);
  margin-top: 8px;
  letter-spacing: 0.5px;
}
.plan .start.wake {
  margin-top: 20px;
  background: rgba(255, 255, 255, 0.08);
  border: 1px solid var(--glass-stroke);
  box-shadow: none;
}

/* ===== 区块标题 ===== */
.sec-title {
  font-size: 13px;
  color: var(--sub);
  letter-spacing: 1.5px;
  font-weight: 600;
  margin: 4px 4px 12px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.sec-title .more {
  color: var(--lilac);
  cursor: pointer;
}

/* ===== 报告卡 ===== */
.report {
  padding: 18px;
  border-radius: 20px;
  margin-bottom: 4px;
  background: linear-gradient(150deg, rgba(124, 58, 237, 0.15), rgba(255, 255, 255, 0.03));
}
.rep-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.rep-title {
  font-size: 14px;
  font-weight: 600;
  color: #fff;
}
.rep-badge {
  font-size: 10.5px;
  color: var(--lilac);
  border: 1px solid rgba(167, 139, 250, 0.4);
  padding: 3px 9px;
  border-radius: 20px;
}
.rep-body {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-top: 14px;
}
.ring-wrap {
  width: 120px;
  height: 120px;
  flex: 0 0 auto;
}
.ring {
  width: 120px;
  height: 120px;
}
.ring-bg {
  fill: none;
  stroke: rgba(255, 255, 255, 0.1);
  stroke-width: 9;
}
.ring-fg {
  fill: none;
  stroke: url(#scoreGrad);
  stroke-width: 9;
  stroke-linecap: round;
  transition: stroke-dashoffset 0.8s ease;
}
.ring-num {
  font-family: var(--font-title);
  font-size: 34px;
  font-weight: 700;
  fill: #fff;
  text-anchor: middle;
}
.ring-cap {
  font-size: 11px;
  fill: var(--sub);
  text-anchor: middle;
}
.rep-metrics {
  flex: 1;
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 10px;
}
.rm {
  text-align: center;
  background: rgba(255, 255, 255, 0.04);
  border: 1px solid rgba(255, 255, 255, 0.04);
  border-radius: 14px;
  padding: 10px 4px;
}
.rmv {
  font-family: var(--font-title);
  font-size: 19px;
  font-weight: 700;
  color: #fff;
}
.rmv span {
  font-size: 11px;
  color: var(--sub);
  margin-left: 2px;
  font-weight: 500;
  font-family: var(--font-body);
}
.rml {
  font-size: 10px;
  color: var(--sub);
  margin-top: 4px;
}

.stage-label {
  font-size: 12px;
  color: #fff;
  font-weight: 600;
  letter-spacing: 0.5px;
  margin-top: 18px;
  margin-bottom: 10px;
}
.sleep-bar {
  display: flex;
  height: 8px;
  border-radius: 6px;
  overflow: hidden;
  background: rgba(255, 255, 255, 0.06);
}
.sleep-bar .seg {
  height: 100%;
}
.sleep-bar .seg.deep {
  background: linear-gradient(90deg, #8b5cf6, #a78bfa);
}
.sleep-bar .seg.light {
  background: rgba(255, 255, 255, 0.35);
}
.sleep-bar .seg.rem {
  background: rgba(255, 255, 255, 0.16);
}
.bar-legend {
  display: flex;
  gap: 16px;
  margin-top: 10px;
  font-size: 10.5px;
  color: var(--sub);
}
.bar-legend span {
  display: flex;
  align-items: center;
  gap: 6px;
}
.bar-legend .dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  display: inline-block;
}
.bar-legend .dot.deep {
  background: #a78bfa;
}
.bar-legend .dot.light {
  background: rgba(255, 255, 255, 0.5);
}
.bar-legend .dot.rem {
  background: rgba(255, 255, 255, 0.25);
}

/* ===== 推荐卡 ===== */
.rec-row {
  display: flex;
  gap: 12px;
  margin-bottom: 8px;
}
.rec {
  flex: 1;
  border-radius: 18px;
  overflow: hidden;
  background: var(--card);
  border: 1px solid rgba(255, 255, 255, 0.05);
  cursor: pointer;
  transition: transform 0.15s ease;
  -webkit-tap-highlight-color: transparent;
}
.rec:active {
  transform: scale(0.97);
}
.rec .thumb {
  height: 74px;
  position: relative;
}
.rec .thumb svg {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
}
.rec .meta {
  padding: 9px 10px 12px;
}
.rec .rt {
  font-size: 12px;
  color: #fff;
  font-weight: 600;
  line-height: 1.3;
}
.rec .rd {
  font-size: 10px;
  color: var(--sub);
  margin-top: 5px;
}

.spacer {
  flex: 1;
  min-height: 8px;
}
</style>
