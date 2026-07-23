import { ref, onUnmounted } from 'vue'

export function useCountdown(initialSeconds = 60) {
  const count = ref(0)
  const isRunning = ref(false)
  let timer: ReturnType<typeof setInterval> | null = null

  function start(seconds?: number) {
    stop()
    count.value = seconds ?? initialSeconds
    isRunning.value = true
    timer = setInterval(() => {
      count.value--
      if (count.value <= 0) {
        stop()
      }
    }, 1000)
  }

  function stop() {
    if (timer) {
      clearInterval(timer)
      timer = null
    }
    isRunning.value = false
  }

  function reset() {
    stop()
    count.value = 0
  }

  onUnmounted(() => {
    stop()
  })

  return { count, isRunning, start, stop, reset }
}
