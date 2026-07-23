import request from './request'
import type { ApiResponse } from '@/types/api'

export interface DistributorInfoVO {
  id: number
  userId: number
  level: number
  levelName: string
  realName: string
  phone: string
  alipayAccount: string
  wechatAccount: string
  totalCommission: number
  availableCommission: number
  frozenCommission: number
  totalWithdraw: number
  totalFans: number
  totalOrders: number
  status: number
  promotionCode: string
}

export interface CommissionRecord {
  id: number
  distributorId: number
  orderNo: string
  userId: number
  source: string
  type: string
  amount: number
  orderAmount: number
  rate: number
  status: string
  createTime: string
  nickname?: string
}

export interface TeamMember {
  userId: number
  nickname: string
  avatar: string
  joinTime: string
}

export function getDistributorInfo(): Promise<ApiResponse<DistributorInfoVO>> {
  return request.get('/distribution/info')
}

export function getCommissionRecords(page: number = 1, size: number = 20): Promise<ApiResponse<{ list: CommissionRecord[]; page: number; size: number }>> {
  return request.get('/distribution/commissionDetail', { params: { page, size } })
}

export function getTeamList(page: number = 1, size: number = 20): Promise<ApiResponse<TeamMember[]>> {
  return request.get('/distribution/teamDirect', { params: { page, size } })
}

export function applyWithdraw(amount: number, payType: string = 'alipay'): Promise<ApiResponse<null>> {
  return request.post('/distribution/applyWithdraw', null, { params: { amount, payType } })
}

export function applyDistributor(realName: string, alipayAccount?: string, wechatAccount?: string): Promise<ApiResponse<null>> {
  return request.post('/distribution/apply', null, { params: { realName, alipayAccount, wechatAccount } })
}

export function getPromotionCode(): Promise<ApiResponse<{ promotionCode: string }>> {
  return request.get('/distribution/promotionCode')
}

export function getCommissionOverview(): Promise<ApiResponse<any>> {
  return request.get('/distribution/commissionOverview')
}

export function getWithdrawList(): Promise<ApiResponse<any[]>> {
  return request.get('/distribution/withdrawList')
}
