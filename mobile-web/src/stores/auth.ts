import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as loginApi, getUserInfo, updateUser } from '@/api/user'
import type { AppUserInfoVO } from '@/types/api'
import { getToken, setToken, removeToken } from '@/utils/storage'
import router from '@/router'

export const useAuthStore = defineStore('auth', () => {
  const token = ref<string | null>(getToken())
  const userInfo = ref<AppUserInfoVO | null>(null)

  const isLoggedIn = computed(() => !!token.value)
  const isVip = computed(() => (userInfo.value?.vipStatus ?? 0) > 0)

  async function login(phone: string, code: string, inviterId?: number) {
    const res = await loginApi({
      phone,
      code,
      inviterId
    })
    const data = res.data
    setToken(data.token)
    token.value = data.token
    userInfo.value = data.userInfo
  }

  function logout() {
    token.value = null
    userInfo.value = null
    removeToken()
    router.push('/login')
  }

  async function fetchUserInfo() {
    const res = await getUserInfo()
    userInfo.value = res.data
  }

  async function updateProfile(data: { nickname?: string; gender?: number; birthday?: string }) {
    await updateUser(data)
    await fetchUserInfo()
  }

  return { token, userInfo, isLoggedIn, isVip, login, logout, fetchUserInfo, updateProfile }
})
