/**
 * usePersistedState — 响应式 ref 自动同步 localStorage
 *
 * 用法:
 *   const count = usePersistedState('focus_counter', 0)
 *   count.value++  // 自动存到 localStorage
 *
 * 支持对象、数组、基本类型。
 * key 会自动拼接 xinling_ 前缀。
 */

import { ref, watch, type Ref } from 'vue'
import { getItem, setItem } from '@/utils/storage'

export function usePersistedState<T>(
  key: string,
  defaultValue: T,
): Ref<T> {
  const stored = getItem<T>(key)
  const state = ref<T>(stored !== null ? stored : defaultValue) as Ref<T>

  // 深度 watch，自动同步
  watch(
    state,
    (val) => {
      setItem(key, val)
    },
    { deep: true },
  )

  return state
}
