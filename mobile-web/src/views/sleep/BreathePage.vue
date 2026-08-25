<script setup lang="ts">
import { ref, computed, watch, onUnmounted } from 'vue'
import SleepShell from './SleepShell.vue'

// 4·7·8 呼吸法：吸气 4s → 屏息 7s → 呼气 8s
const phases = [
  { key: 'inhale', name: '吸气', sub: '4 秒', dur: 4, scale: 1.13 },
  { key: 'hold', name: '屏息', sub: '7 秒', dur: 7, scale: 1.13 },
  { key: 'exhale', name: '呼气', sub: '8 秒', dur: 8, scale: 1.0 }
]

const phaseIndex = ref(0)
const remain = ref(phases[0].dur)
const playing = ref(false)
const ballScale = ref(1.0)
const ballTransition = ref('4s')
const sessionElapsed = ref(0) // 秒
const TOTAL = 600 // 10:00 单次练习

let timer: ReturnType<typeof setInterval> | null = null

const currentPhase = computed(() => phases[phaseIndex.value])

const elapsedLabel = computed(() => {
  const m = Math.floor(sessionElapsed.value / 60)
  const s = sessionElapsed.value % 60
  return `${String(m).padStart(2, '0')}:${String(s).padStart(2, '0')}`
})
const progressPct = computed(() => Math.min((sessionElapsed.value / TOTAL) * 100, 100))

function applyPhaseVisual() {
  const p = currentPhase.value
  ballScale.value = p.scale
  ballTransition.value = `${p.dur}s`
}

function tick() {
  if (remain.value > 1) {
    remain.value--
  } else {
    phaseIndex.value = (phaseIndex.value + 1) % phases.length
    remain.value = currentPhase.value.dur
  }
  if (sessionElapsed.value < TOTAL) sessionElapsed.value++
}

function start() {
  playing.value = true
  applyPhaseVisual()
  timer = setInterval(tick, 1000)
}

function pause() {
  playing.value = false
  if (timer) {
    clearInterval(timer)
    timer = null
  }
}

function toggle() {
  playing.value ? pause() : start()
}

function gotoPhase(delta: number) {
  phaseIndex.value = (phaseIndex.value + delta + phases.length) % phases.length
  remain.value = currentPhase.value.dur
  applyPhaseVisual()
}

watch(phaseIndex, applyPhaseVisual)
onUnmounted(pause)

function fmtTime(t: number) {
  const m = Math.floor(t / 60)
  const s = t % 60
  return `${String(m).padStart(2, '0')}:${String(s).padStart(2, '0')}`
}
</script>

<template>
  <SleepShell variant="breathe">
    <div class="breathe-page">
      <div class="br-head">
        <div class="t">呼吸引导</div>
        <div class="s">4 · 7 · 8 安神呼吸法</div>
      </div>

      <div class="breathe-wrap">
        <svg class="breathe-svg" viewBox="0 0 320 320">
          <defs>
            <radialGradient id="glow" cx="50%" cy="50%" r="50%">
              <stop offset="0%" stop-color="#A78BFA" stop-opacity=".55" />
              <stop offset="45%" stop-color="#7C3AED" stop-opacity=".22" />
              <stop offset="100%" stop-color="#7C3AED" stop-opacity="0" />
            </radialGradient>
            <radialGradient id="ball" cx="38%" cy="30%" r="75%">
              <stop offset="0%" stop-color="#C4B5FD" />
              <stop offset="42%" stop-color="#A78BFA" />
              <stop offset="100%" stop-color="#5B21B6" />
            </radialGradient>
            <filter id="soft" x="-60%" y="-60%" width="220%" height="220%">
              <feGaussianBlur stdDeviation="6" />
            </filter>
          </defs>
          <circle class="glow-c" cx="160" cy="160" r="150" fill="url(#glow)" />
          <circle cx="160" cy="160" r="122" fill="none" stroke="#6366F1" stroke-opacity=".26" stroke-width="1.5" />
          <circle cx="160" cy="160" r="100" fill="none" stroke="#6366F1" stroke-opacity=".18" stroke-width="1.5" />
          <g
            class="ball-group"
            :style="{
              transform: `scale(${ballScale})`,
              transformOrigin: 'center',
              transformBox: 'fill-box',
              transition: `transform ${ballTransition} ease-in-out`
            }"
          >
            <circle cx="160" cy="160" r="80" fill="url(#ball)" filter="url(#soft)" opacity=".9" />
            <circle cx="160" cy="160" r="74" fill="url(#ball)" />
            <ellipse cx="138" cy="134" rx="26" ry="15" fill="#fff" opacity=".35" filter="url(#soft)" />
          </g>
          <text class="ball-text" x="160" y="152">{{ currentPhase.name }}</text>
          <text class="ball-sub" x="160" y="184">{{ remain }} 秒</text>
        </svg>

        <div class="br-guide">
          <div class="m">深长地吸气，让身体慢慢沉入柔软</div>
          <div class="c">吸气 4 秒 · 屏息 7 秒 · 呼气 8 秒</div>
        </div>
      </div>

      <div class="progress">
        <div class="prog-bar">
          <div class="prog-fill" :style="{ width: progressPct + '%' }" />
        </div>
        <div class="prog-time">
          <span>{{ elapsedLabel }}</span>
          <span>{{ fmtTime(TOTAL) }}</span>
        </div>
      </div>

      <div class="controls">
        <button class="ctrl" title="上一段" @click="gotoPhase(-1)">
          <svg viewBox="0 0 24 24" fill="currentColor"><path d="M6 5h2v14H6zM20 5 9 12l11 7z" /></svg>
        </button>
        <button class="ctrl play" :title="playing ? '暂停' : '播放'" @click="toggle">
          <svg v-if="!playing" viewBox="0 0 24 24" fill="currentColor"><path d="M8 5v14l11-7z" /></svg>
          <svg v-else viewBox="0 0 24 24" fill="currentColor">
            <rect x="6" y="5" width="4" height="14" rx="1.2" />
            <rect x="14" y="5" width="4" height="14" rx="1.2" />
          </svg>
        </button>
        <button class="ctrl" title="下一段" @click="gotoPhase(1)">
          <svg viewBox="0 0 24 24" fill="currentColor"><path d="M16 5h2v14h-2zM4 5l11 7L4 19z" /></svg>
        </button>
      </div>
    </div>
  </SleepShell>
</template>

<style scoped>
.breathe-page {
  display: flex;
  flex-direction: column;
  min-height: calc(100dvh - 64px - env(safe-area-inset-top, 0px) - 100px);
}
.br-head {
  text-align: center;
  margin-bottom: 6px;
}
.br-head .t {
  font-family: var(--font-title);
  font-size: 20px;
  font-weight: 600;
  color: #fff;
}
.br-head .s {
  color: var(--lilac);
  font-size: 11.5px;
  letter-spacing: 2px;
  margin-top: 6px;
  font-weight: 600;
}
.breathe-wrap {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}
.breathe-svg {
  width: min(78vw, 320px);
  height: min(78vw, 320px);
}
.glow-c {
  transform-origin: 160px 160px;
  animation: glowpulse 8s ease-in-out infinite;
}
@keyframes glowpulse {
  0%,
  100% {
    opacity: 0.7;
  }
  50% {
    opacity: 1;
  }
}
.ball-text {
  font-family: var(--font-title);
  font-size: 30px;
  font-weight: 600;
  fill: #fff;
  text-anchor: middle;
}
.ball-sub {
  font-size: 13px;
  fill: rgba(255, 255, 255, 0.75);
  text-anchor: middle;
  font-weight: 500;
}
.br-guide {
  text-align: center;
  margin-top: 10px;
}
.br-guide .m {
  font-size: 15px;
  color: #fff;
  font-weight: 500;
}
.br-guide .c {
  font-size: 12px;
  color: var(--sub);
  margin-top: 8px;
  letter-spacing: 0.5px;
}
.progress {
  margin: 18px 6px 6px;
}
.prog-bar {
  height: 4px;
  border-radius: 4px;
  background: rgba(255, 255, 255, 0.12);
  overflow: hidden;
}
.prog-fill {
  height: 100%;
  border-radius: 4px;
  background: linear-gradient(90deg, var(--indigo2), var(--lilac));
  transition: width 0.4s linear;
}
.prog-time {
  display: flex;
  justify-content: space-between;
  font-size: 11px;
  color: var(--sub);
  margin-top: 8px;
  font-family: var(--font-body);
}
.controls {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 26px;
  margin-top: 20px;
}
.ctrl {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.08);
  border: 1px solid var(--glass-stroke);
  color: #fff;
  cursor: pointer;
  -webkit-tap-highlight-color: transparent;
}
.ctrl svg {
  width: 20px;
  height: 20px;
}
.ctrl:active {
  transform: scale(0.94);
}
.ctrl.play {
  width: 66px;
  height: 66px;
  background: linear-gradient(135deg, var(--purple), var(--indigo2));
  border: none;
  box-shadow: 0 12px 30px rgba(124, 58, 237, 0.5);
}
.ctrl.play svg {
  width: 26px;
  height: 26px;
}
</style>
