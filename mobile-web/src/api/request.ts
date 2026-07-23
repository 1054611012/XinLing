import axios from 'axios'
import type { AxiosInstance, InternalAxiosRequestConfig, AxiosResponse } from 'axios'
import { showToast, showLoadingToast, closeToast } from 'vant'
import { getToken, removeToken } from '@/utils/storage'
import router from '@/router'

const request: AxiosInstance = axios.create({
  baseURL: '/api/app',
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json'
  }
})

let loadingInstance: ReturnType<typeof showLoadingToast> | null = null
let requestCount = 0

// 请求拦截器
request.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const token = getToken()
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }

    // 自动 loading（可跳过）
    if (config.headers._loading !== false) {
      requestCount++
      if (requestCount === 1) {
        loadingInstance = showLoadingToast({
          message: '加载中...',
          forbidClick: true,
          duration: 0
        })
      }
    }

    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  (response: AxiosResponse) => {
    // 关闭 loading
    if (response.config.headers._loading !== false) {
      requestCount--
      if (requestCount <= 0 && loadingInstance) {
        closeToast()
        loadingInstance = null
        requestCount = 0
      }
    }

    const res = response.data

    if (res.code === 0 || res.code === 200) {
      return res
    }

    // token 过期
    if (res.code === 401) {
      removeToken()
      showToast('登录已过期，请重新登录')
      router.push('/login')
      return Promise.reject(new Error(res.message || '未授权'))
    }

    showToast(res.message || '请求失败')
    return Promise.reject(new Error(res.message || '请求失败'))
  },
  (error) => {
    // 关闭 loading
    requestCount = 0
    if (loadingInstance) {
      closeToast()
      loadingInstance = null
    }

    if (error.code === 'ECONNABORTED') {
      showToast('请求超时，请稍后重试')
    } else if (!error.response) {
      showToast('网络异常，请检查网络连接')
    } else {
      const status = error.response.status
      switch (status) {
        case 401:
          removeToken()
          showToast('登录已过期')
          router.push('/login')
          break
        case 403:
          showToast('没有权限访问')
          break
        case 404:
          showToast('请求的资源不存在')
          break
        case 500:
          showToast('服务器错误，请稍后重试')
          break
        default:
          showToast('请求失败')
      }
    }
    return Promise.reject(error)
  }
)

export default request
