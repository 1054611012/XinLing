import request from './request'
import type { ApiResponse, VipPackage, VipInfo } from '@/types/api'

export function getVipPackages(): Promise<ApiResponse<VipPackage[]>> {
  return request.get('/vip/packages')
}

export function getVipInfo(): Promise<ApiResponse<VipInfo>> {
  return request.get('/vip/info')
}

export function cancelAutoRenew(): Promise<ApiResponse<null>> {
  return request.post('/vip/cancelAutoRenew')
}
