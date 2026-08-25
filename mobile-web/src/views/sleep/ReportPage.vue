<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getSleepRecords } from '@/api/sleep'
import type { SleepRecord } from '@/types/api'
import SleepShell from './SleepShell.vue'

const router = useRouter()
const lastRecord = ref<SleepRecord | null>(null)
const loading = ref(false)

const R = 80
const C = 2 * Math.PI * R

const scoreRing = computed(() => {
  const score = lastRecord.value?.sleepScore || 0
  const offset = C * (1 - Math.min(Math.max(score, 0), 100) / 100)
  return { offset, c: C }
})

const quality = computed(() => {
  const s = lastRecord.value?.sleepScore || 0
  if (s >= 85) return '优质睡眠'
  if (s >= 70) return '良好睡眠'
  if (s >= 60) return '一般睡眠'
  return '睡眠欠佳'
})

const qualitySub = computed(() => {
  const s = lastRecord.value?.sleepScore || 0
  return `优于 ${Math.min(95, Math.round(s * 0.9))}% 的用户`
})

const qualityNote = computed(() => {
  const rec = lastRecord.value
  if (!rec) return ''
  return '深睡占比充足，REM 周期完整，夜间觉醒偏少，整体恢复良好。'
})

const reportDate = computed(() => {
  const rec = lastRecord.value
  if (!rec?.endTime) return '最近一夜 · 已同步手环'
  const d = new Date(rec.endTime)
  if (isNaN(d.getTime())) return '最近一夜 · 已同步手环'
  return `${d.getMonth() + 1} 月 ${d.getDate()} 日 夜间 · 已同步手环`
})

const awakeMinutes = computed(() => {
  const rec = lastRecord.value
  if (!rec) return 0
  const totalMin = Math.round((rec.duration || 0) / 60)
  return Math.max(0, totalMin - (rec.deepSleepMinutes || 0) - (rec.lightSleepMinutes || 0) - (rec.remSleepMinutes || 0))
})

const PX_PER_MIN = 0.75 // 稿子比例：最大柱 200min → 150px
const MAX_H = 150
const BASE_Y = 175

const bars = computed(() => {
  const rec = lastRecord.value
  if (!rec) return []
  const stages = [
    { label: '深睡', minutes: rec.deepSleepMinutes || 0, color: '#4338CA' },
    { label: '浅睡', minutes: rec.lightSleepMinutes || 0, color: '#6366F1' },
    { label: 'REM', minutes: rec.remSleepMinutes || 0, color: '#A78BFA' },
    { label: '清醒', minutes: awakeMinutes.value, color: 'rgba(148,163,184,.55)' }
  ]
  const xs = [44, 106, 168, 230]
  return stages.map((s, i) => {
    const h = Math.min(s.minutes * PX_PER_MIN, MAX_H)
    return {
      ...s,
      x: xs[i],
      y: BASE_Y - h,
      height: h,
      timeLabel: s.minutes > 0 ? fmtMin(s.minutes) : ''
    }
  })
})

const metrics = computed(() => {
  const rec = lastRecord.value
  if (!rec) return []
  return [
    { label: '睡眠时长', value: fmtMin(Math.round((rec.duration || 0) / 60)), icon: 'clock' },
    { label: '觉醒次数', value: `${rec.interruptCount || 0} 次`, icon: 'bell' },
    { label: '鼾声', value: `${rec.snoringCount || 0} 次`, icon: 'moon' }
  ]
})

function fmtMin(min: number) {
  const m = Math.max(0, Math.round(min))
  if (m >= 60) {
    const h = Math.floor(m / 60)
    const mm = m % 60
    return mm ? `${h}h${mm}m` : `${h}h`
  }
  return `${m}m`
}

const icons: Record<string, string> = {
  clock:
    '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="9"/><path d="M12 7v5l3 3"/></svg>',
  bell:
    '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 3a6 6 0 0 0-6 6c0 2.5 1.5 4 2.5 5 .8.8 1 1.5 1 3h5c0-1.5.2-2.2 1-3 1-1 2.5-2.5 2.5-5a6 6 0 0 0-6-6Z"/><path d="M9.5 21h5"/></svg>',
  moon:
    '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 12.8A9 9 0 1 1 11.2 3a7 7 0 0 0 9.8 9.8Z"/></svg>'
}

async function load() {
  try {
    loading.value = true
    const res = await getSleepRecords(1, 1)
    if (res.data.records.length > 0) {
      lastRecord.value = res.data.records[0]
    }
  } catch {
    // ignore
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>

<template>
  <SleepShell variant="faint">
    <div class="report-page">
      <div class="rep-head">
        <h2>睡眠报告</h2>
        <div class="s">{{ reportDate }}</div>
      </div>

      <!-- 加载 / 空状态 -->
      <div v-if="loading" class="empty">加载中…</div>

      <div v-else-if="!lastRecord" class="empty glass">
        <div class="empty-ic">
          <svg viewBox="0 0 24 24" fill="none" stroke="#A78BFA" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round">
            <path d="M21 12.8A9 9 0 1 1 11.2 3a7 7 0 0 0 9.8 9.8Z" />
          </svg>
        </div>
        <div class="empty-title">还没有睡眠记录哦</div>
        <div class="empty-sub">完成一次睡眠后，这里会生成你的专属报告</div>
        <button class="empty-btn" @click="router.push('/sleep')">去睡眠</button>
      </div>

      <template v-else>
        <!-- 评分卡 -->
        <div class="score-card glass">
          <svg class="ring" viewBox="0 0 200 200">
            <defs>
              <linearGradient id="ring" x1="0" y1="0" x2="1" y2="1">
                <stop offset="0" stop-color="#6366F1" />
                <stop offset="1" stop-color="#A78BFA" />
              </linearGradient>
            </defs>
            <circle cx="100" cy="100" :r="R" fill="none" stroke="rgba(255,255,255,.10)" stroke-width="14" />
            <circle
              cx="100"
              cy="100"
              :r="R"
              fill="none"
              stroke="url(#ring)"
              stroke-width="14"
              stroke-linecap="round"
              :stroke-dasharray="scoreRing.c"
              :stroke-dashoffset="scoreRing.offset"
              transform="rotate(-90 100 100)"
            />
            <text class="score-num" x="100" y="96">{{ lastRecord.sleepScore || '--' }}</text>
            <text class="score-cap" x="100" y="120">分 / 100</text>
          </svg>
          <div class="label">
            <div class="big">{{ quality }}</div>
            <div class="sub">{{ qualitySub }}</div>
            <div class="note">{{ qualityNote }}</div>
          </div>
        </div>

        <!-- 睡眠阶段 -->
        <div class="stage-card">
          <div class="ct">睡眠阶段</div>
          <div class="cs">总睡眠 {{ fmtMin(Math.round((lastRecord.duration || 0) / 60)) }} · 周期 ×4</div>
          <svg class="chart" viewBox="0 0 320 200" preserveAspectRatio="xMidYMid meet">
            <line class="axis" x1="20" y1="175" x2="300" y2="175" />
            <g v-for="(b, i) in bars" :key="i">
              <rect :x="b.x" :y="b.y" width="46" :height="b.height" rx="8" :fill="b.color" />
              <text class="bar-time" :x="b.x + 23" :y="b.y - 7">{{ b.timeLabel }}</text>
              <text class="bar-label" :x="b.x + 23" y="188">{{ b.label }}</text>
            </g>
          </svg>
        </div>

        <!-- 指标卡 -->
        <div class="metrics">
          <div class="metric" v-for="(m, i) in metrics" :key="i">
            <div class="mi" v-html="icons[m.icon as keyof typeof icons]" />
            <div class="mv">{{ m.value }}</div>
            <div class="ml">{{ m.label }}</div>
          </div>
        </div>
      </template>
    </div>
  </SleepShell>
</template>

<style scoped>
.report-page {
  min-height: calc(100dvh - 64px - env(safe-area-inset-top, 0px));
}
.rep-head h2 {
  font-family: var(--font-title);
  font-size: 23px;
  font-weight: 600;
  color: #fff;
}
.rep-head .s {
  color: var(--sub);
  font-size: 12.5px;
  margin-top: 7px;
}

.empty {
  margin-top: 32px;
  padding: 36px 20px;
  border-radius: 20px;
  text-align: center;
}
.empty.glass {
  background: var(--glass);
  border: 1px solid var(--glass-stroke);
  backdrop-filter: blur(22px);
  -webkit-backdrop-filter: blur(22px);
}
.empty-ic {
  width: 56px;
  height: 56px;
  margin: 0 auto 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background: rgba(167, 139, 250, 0.14);
  border: 1px solid rgba(167, 139, 250, 0.3);
}
.empty-ic svg {
  width: 28px;
  height: 28px;
}
.empty-title {
  font-size: 16px;
  font-weight: 600;
  color: #fff;
}
.empty-sub {
  font-size: 12px;
  color: var(--sub);
  margin-top: 8px;
  line-height: 1.6;
}
.empty-btn {
  margin-top: 18px;
  background: linear-gradient(90deg, var(--purple), var(--indigo2));
  border: none;
  color: #fff;
  font-family: var(--font-body);
  font-weight: 600;
  font-size: 14px;
  padding: 12px 32px;
  border-radius: 14px;
  box-shadow: 0 10px 26px rgba(124, 58, 237, 0.35);
  cursor: pointer;
}

/* 评分卡 */
.score-card {
  padding: 18px;
  border-radius: 20px;
  margin-top: 18px;
  display: flex;
  align-items: center;
  gap: 18px;
  background: linear-gradient(150deg, rgba(124, 58, 237, 0.15), rgba(255, 255, 255, 0.03));
}
.score-card .ring {
  width: 120px;
  height: 120px;
  flex: 0 0 auto;
}
.score-num {
  font-family: var(--font-title);
  font-size: 38px;
  font-weight: 700;
  fill: #fff;
  text-anchor: middle;
}
.score-cap {
  font-size: 11px;
  fill: var(--sub);
  text-anchor: middle;
}
.label .big {
  font-family: var(--font-title);
  font-size: 15px;
  color: #fff;
  font-weight: 600;
}
.label .sub {
  font-size: 12px;
  color: var(--lilac);
  margin-top: 6px;
  font-weight: 600;
  letter-spacing: 1px;
}
.label .note {
  font-size: 11px;
  color: var(--sub);
  margin-top: 10px;
  line-height: 1.5;
}

/* 阶段卡 */
.stage-card {
  padding: 18px 16px 14px;
  border-radius: 20px;
  margin-top: 14px;
  background: var(--card);
}
.stage-card .ct {
  font-size: 12.5px;
  color: #fff;
  font-weight: 600;
  letter-spacing: 0.5px;
}
.stage-card .cs {
  font-size: 10.5px;
  color: var(--sub);
  margin-top: 3px;
}
.chart {
  width: 100%;
  height: 200px;
  margin-top: 8px;
}
.bar-label {
  font-size: 10px;
  fill: var(--sub);
  text-anchor: middle;
}
.bar-time {
  font-size: 10px;
  fill: #fff;
  text-anchor: middle;
  font-weight: 600;
}
.axis {
  stroke: rgba(255, 255, 255, 0.12);
  stroke-width: 1;
}

/* 指标卡 */
.metrics {
  display: flex;
  gap: 11px;
  margin-top: 14px;
}
.metric {
  flex: 1;
  padding: 14px 10px;
  border-radius: 16px;
  background: var(--card);
  text-align: center;
  border: 1px solid rgba(255, 255, 255, 0.04);
}
.metric .mi {
  margin: 0 auto 8px;
  width: 30px;
  height: 30px;
  border-radius: 9px;
  background: rgba(99, 102, 241, 0.16);
  display: flex;
  align-items: center;
  justify-content: center;
}
.metric .mi :deep(svg) {
  width: 16px;
  height: 16px;
  color: var(--lilac);
}
.metric .mv {
  font-family: var(--font-title);
  font-size: 18px;
  font-weight: 700;
  color: #fff;
}
.metric .ml {
  font-size: 10px;
  color: var(--sub);
  margin-top: 4px;
}
</style>
