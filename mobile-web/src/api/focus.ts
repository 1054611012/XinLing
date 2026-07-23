import request from './request'
import type { ApiResponse, FocusRecord, FocusSettings, StartFocusParams } from '@/types/api'

export function startFocus(data: StartFocusParams): Promise<ApiResponse<FocusRecord>> {
  return request.post('/focus/start', data)
}

export function pauseFocus(): Promise<ApiResponse<null>> {
  return request.post('/focus/pause')
}

export function resumeFocus(): Promise<ApiResponse<null>> {
  return request.post('/focus/resume')
}

export function endFocus(): Promise<ApiResponse<FocusRecord>> {
  return request.post('/focus/end')
}

export function interruptFocus(reason: string): Promise<ApiResponse<FocusRecord>> {
  return request.post('/focus/interrupt', { reason })
}

export function getFocusRecords(page: number = 1, size: number = 20): Promise<ApiResponse<{ records: FocusRecord[]; total: number }>> {
  return request.get('/focus/records', { params: { page, size } })
}

export function getFocusSettings(): Promise<ApiResponse<FocusSettings>> {
  return request.get('/focus/settings')
}

export function updateFocusSettings(data: FocusSettings): Promise<ApiResponse<null>> {
  return request.post('/focus/settings/update', data)
}
