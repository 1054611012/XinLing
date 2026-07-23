import request from './request'
import type { ApiResponse, HomeInfoVO } from '@/types/api'

export function getHomeInfo(): Promise<ApiResponse<HomeInfoVO>> {
  return request.get('/home/info')
}
