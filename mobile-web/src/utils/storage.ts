/**
 * localStorage 封装
 */

const PREFIX = 'xinling_'

export function getItem<T = string>(key: string): T | null {
  try {
    const raw = localStorage.getItem(PREFIX + key)
    if (raw === null) return null
    return JSON.parse(raw) as T
  } catch {
    return localStorage.getItem(PREFIX + key) as unknown as T
  }
}

export function setItem(key: string, value: any): void {
  try {
    const str = typeof value === 'string' ? value : JSON.stringify(value)
    localStorage.setItem(PREFIX + key, str)
  } catch (e) {
    console.error('存储失败:', e)
  }
}

export function removeItem(key: string): void {
  localStorage.removeItem(PREFIX + key)
}

export function clearAll(): void {
  const keys = Object.keys(localStorage).filter(k => k.startsWith(PREFIX))
  keys.forEach(k => localStorage.removeItem(k))
}

export const TokenKey = 'token'

export function getToken(): string | null {
  return getItem<string>(TokenKey)
}

export function setToken(token: string): void {
  setItem(TokenKey, token)
}

export function removeToken(): void {
  removeItem(TokenKey)
}
