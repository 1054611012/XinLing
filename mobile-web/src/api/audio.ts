import request from './request'
import type { ApiResponse, AudioItem, AudioMix } from '@/types/api'

export function getAudioList(category?: string, page: number = 1, size: number = 20): Promise<ApiResponse<{ records: AudioItem[]; total: number }>> {
  return request.get('/audio/list', { params: { category, page, size } })
}

export function getAudioDetail(id: number): Promise<ApiResponse<AudioItem>> {
  return request.get(`/audio/${id}`)
}

export function searchAudio(keyword: string, page: number = 1, size: number = 20): Promise<ApiResponse<{ records: AudioItem[]; total: number }>> {
  return request.get('/audio/search', { params: { keyword, page, size } })
}

export function getMixList(): Promise<ApiResponse<AudioMix[]>> {
  return request.get('/audio/mix/list')
}

export function saveMix(data: { name: string; audioIds: number[] }): Promise<ApiResponse<AudioMix>> {
  return request.post('/audio/mix', data)
}

export function getAudioHistory(page: number = 1, size: number = 20): Promise<ApiResponse<{ records: AudioItem[]; total: number }>> {
  return request.get('/audio/history', { params: { page, size } })
}
