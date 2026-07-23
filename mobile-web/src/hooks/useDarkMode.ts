import { ref, watch } from 'vue'

const DARK_MODE_KEY = 'xinling-dark-mode'

/** 当前是否为深色模式 */
export const isDark = ref(false)

/** 初始化深色模式：检查 localStorage → 系统偏好 → 默认关 */
export function initDarkMode() {
  const saved = localStorage.getItem(DARK_MODE_KEY)

  if (saved !== null) {
    isDark.value = saved === 'true'
  } else {
    // 跟随系统
    isDark.value = window.matchMedia('(prefers-color-scheme: dark)').matches
  }

  applyTheme(isDark.value)

  // 监听系统变化
  window.matchMedia('(prefers-color-scheme: dark)').addEventListener('change', (e) => {
    const saved = localStorage.getItem(DARK_MODE_KEY)
    if (saved === null) {
      // 未手动设置过，才跟随系统
      isDark.value = e.matches
      applyTheme(isDark.value)
    }
  })
}

/** 切换深色模式 */
export function toggleDarkMode(value: boolean) {
  isDark.value = value
  localStorage.setItem(DARK_MODE_KEY, String(value))
  applyTheme(value)
}

/** 应用主题 */
function applyTheme(dark: boolean) {
  if (dark) {
    document.documentElement.classList.add('dark-mode')
  } else {
    document.documentElement.classList.remove('dark-mode')
  }
}
