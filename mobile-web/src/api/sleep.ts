import request from './request'
import type { ApiResponse, SleepRecord, SleepReport } from '@/types/api'

export function startSleep(): Promise<ApiResponse<SleepRecord>> {
  return request.post('/sleep/start')
}

export function endSleep(): Promise<ApiResponse<SleepRecord>> {
  return request.post('/sleep/end')
}

export function getSleepRecords(page: number = 1, size: number = 20): Promise<ApiResponse<{ records: SleepRecord[]; total: number }>> {
  return request.get('/sleep/records', { params: { page, size } })
}

export function getSleepReport(date: string): Promise<ApiResponse<SleepReport>> {
  return request.get(`/sleep/report/${date}`)
}
