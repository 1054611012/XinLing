import request from './request'
import type { ApiResponse } from '@/types/api'

export interface ActivityItem {
  id: number
  title: string
  description: string
  cover: string
  type: string
  startTime: string
  endTime: string
  joinedCount: number
  status: number
}

export interface CouponItem {
  id: number
  name: string
  type: string
  value: number
  conditionAmount: number
  endTime: string
  status: number
}

export function getActivityList(): Promise<ApiResponse<ActivityItem[]>> {
  return request.get('/activity/list')
}

export function getActivityDetail(id: number): Promise<ApiResponse<ActivityItem>> {
  return request.get(`/activity/detail/${id}`)
}

export function joinActivity(id: number): Promise<ApiResponse<null>> {
  return request.post(`/activity/join/${id}`)
}

export function getCouponList(): Promise<ApiResponse<CouponItem[]>> {
  return request.get('/pay/coupon/list')
}

export function receiveCoupon(id: number): Promise<ApiResponse<null>> {
  return request.post(`/pay/coupon/receive/${id}`)
}
