import request from './request'
import type { ApiResponse } from '@/types/api'

export interface GrowthInfo {
  level: number
  levelName: string
  exp: number
  nextLevelExp: number
  points: number
  continuousFocusDays: number
  continuousSleepDays: number
}

export interface Achievement {
  id: number
  name: string
  description: string
  icon: string
  isUnlocked: boolean
  obtainTime: string | null
}

export interface DailyTask {
  id: number
  name: string
  description: string
  icon: string
  progress: number
  maxProgress: number
  pointsReward: number
  status: string    // 'ongoing' | 'completed' | 'claimed'
}

export interface MallGoods {
  id: number
  name: string
  description: string
  cover: string
  type: string      // vip/coupon/virtual
  price: number
  stock: number
}

export function getGrowthInfo(): Promise<ApiResponse<GrowthInfo>> {
  return request.get('/growth/info')
}

export function getAchievementList(): Promise<ApiResponse<{ records: Achievement[] }>> {
  return request.get('/achievement/list')
}

export function getDailyTasks(): Promise<ApiResponse<{ records: DailyTask[] }>> {
  return request.get('/task/daily')
}

export function claimTaskReward(taskId: number): Promise<ApiResponse<null>> {
  return request.post(`/task/claimReward/${taskId}`)
}

export function getMallGoods(): Promise<ApiResponse<{ records: MallGoods[] }>> {
  return request.get('/mall/goods')
}

export function exchangeGoods(goodsId: number): Promise<ApiResponse<null>> {
  return request.post(`/mall/exchange/${goodsId}`)
}
