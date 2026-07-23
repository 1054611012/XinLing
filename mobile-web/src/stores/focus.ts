import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { startFocus as startFocusApi, pauseFocus, resumeFocus, endFocus } from '@/api/focus'
import { usePersistedState } from '@/hooks/usePersistedState'

export const useFocusStore = defineStore('focus', () => {
  // ===== 持久化状态：跨页面/刷新保留 =====
  const isRunning = usePersistedState<boolean>('focus_isRunning', false)
  const isPaused = usePersistedState<boolean>('focus_isPaused', false)
  const mode = usePersistedState<string>('focus_mode', 'tomato')
  const selectedTag = usePersistedState<string>('focus_tag', '')
  const totalSeconds = usePersistedState<number>('focus_totalSec', 0)
  const remainingSeconds = usePersistedState<number>('focus_remainSec', 0)
  const customDurations = usePersistedState<Record<string, number>>(
    'focus_custom_durations',
    { tomato: 25, deep: 90, free: 60 },
  )

  const currentRecord = ref<any>(null)
  let timer: ReturnType<typeof setInterval> | null = null

  // ===== 恢复计时器（页面刷新后自动恢复倒计时） =====
  function ensureTimer() {
    if (isRunning.value && !timer && remainingSeconds.value > 0) {
      startTimer()
    }
  }

  // ===== 迁移旧版自定义时长（兼容 localStorage 旧 key） =====
  const LEGACY_DURATIONS_KEY = 'xinling_focus_custom_durations'
  function migrateOldDurations() {
    try {
      const raw = localStorage.getItem(LEGACY_DURATIONS_KEY)
      if (raw) {
        const parsed = JSON.parse(raw)
        let changed = false
        for (const k of Object.keys(parsed)) {
          if (!(k in customDurations.value)) {
            ;(customDurations.value as Record<string, number>)[k] = parsed[k]
            changed = true
          }
        }
        if (changed) customDurations.value = { ...customDurations.value }
        localStorage.removeItem(LEGACY_DURATIONS_KEY)
      }
    } catch { /* ignore */ }
  }

  migrateOldDurations()

  // ===== 页面可见性变化时恢复计时器 =====
  document.addEventListener('visibilitychange', () => {
    if (document.visibilityState === 'visible') {
      ensureTimer()
    }
  })

  // 初始化时尝试恢复计时器
  setTimeout(() => ensureTimer(), 100)

  function saveCustomDurations() {
    customDurations.value = { ...customDurations.value }
  }

  function setCustomDuration(m: string, minutes: number) {
    ;(customDurations.value as Record<string, number>)[m] = minutes
    customDurations.value = { ...customDurations.value }
  }

  const formattedTime = computed(() => {
    const mins = Math.floor(remainingSeconds.value / 60)
    const secs = remainingSeconds.value % 60
    return `${String(mins).padStart(2, '0')}:${String(secs).padStart(2, '0')}`
  })

  const progress = computed(() => {
    if (totalSeconds.value === 0) return 0
    return Math.round(((totalSeconds.value - remainingSeconds.value) / totalSeconds.value) * 100)
  })

  function getModeDuration(m: string): number {
    const d = customDurations.value as Record<string, number>
    const minutes = d[m] ?? 25
    return (minutes || 25) * 60
  }

  async function start(m: string, tag?: string) {
    const duration = getModeDuration(m)
    mode.value = m
    selectedTag.value = tag || ''
    totalSeconds.value = duration
    remainingSeconds.value = duration
    isRunning.value = true
    isPaused.value = false
    startTimer()
    saveCustomDurations()
    try {
      const res = await startFocusApi({ mode: m, duration })
      currentRecord.value = res.data
    } catch {
      // offline mode — local timer still runs
    }
  }

  function startTimer() {
    stopTimer()
    timer = setInterval(() => {
      tick()
    }, 1000)
  }

  function stopTimer() {
    if (timer) {
      clearInterval(timer)
      timer = null
    }
  }

  function tick() {
    if (!isPaused.value && remainingSeconds.value > 0) {
      remainingSeconds.value = remainingSeconds.value - 1
    }
    if (remainingSeconds.value <= 0) {
      stopTimer()
      isRunning.value = false
      isPaused.value = false
    }
  }

  async function pause() {
    isPaused.value = true
    stopTimer()
    try {
      await pauseFocus()
    } catch {
      // offline mode
    }
  }

  async function resume() {
    isPaused.value = false
    startTimer()
    try {
      await resumeFocus()
    } catch {
      // offline mode
    }
  }

  async function end(note?: string) {
    isRunning.value = false
    isPaused.value = false
    totalSeconds.value = 0
    remainingSeconds.value = 0
    mode.value = 'tomato'
    selectedTag.value = ''
    currentRecord.value = null
    stopTimer()
    try {
      await endFocus()
    } catch {
      // offline mode
    }
  }

  function reset() {
    stopTimer()
    isRunning.value = false
    isPaused.value = false
    totalSeconds.value = 0
    remainingSeconds.value = 0
    mode.value = 'tomato'
    selectedTag.value = ''
    currentRecord.value = null
  }

  return {
    isRunning,
    isPaused,
    mode,
    selectedTag,
    remainingSeconds,
    totalSeconds,
    currentRecord,
    customDurations,
    formattedTime,
    progress,
    start,
    tick,
    pause,
    resume,
    end,
    reset,
    setCustomDuration,
  }
})
