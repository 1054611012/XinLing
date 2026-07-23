import request from './request'
import type { ApiResponse, MomentItem, MomentComment, PublishMomentParams } from '@/types/api'

export function getMoments(tab: string = 'recommend', page: number = 1, size: number = 20, extraConfig?: any): Promise<ApiResponse<MomentItem[]>> {
  return request.get('/moment/list', {
    params: { type: tab, page, size },
    ...extraConfig,
    // 确保 headers 正确合并
    headers: {
      ...(extraConfig?.headers || {})
    }
  })
}

export function publishMoment(data: PublishMomentParams): Promise<ApiResponse<MomentItem>> {
  // 后端使用 @RequestParam 接收表单参数，需构造 FormData
  const formData = new FormData()
  formData.append('content', data.content)
  if (data.images && data.images.length > 0) {
    formData.append('images', JSON.stringify(data.images))
  }
  if (data.isAnonymous !== undefined) {
    formData.append('isAnonymous', String(data.isAnonymous))
  }
  if (data.visibility !== undefined) {
    formData.append('visibility', String(data.visibility))
  }
  return request.post('/moment/publish', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

export function likeMoment(momentId: number): Promise<ApiResponse<null>> {
  return request.post(`/moment/like/${momentId}`)
}

export function unlikeMoment(momentId: number): Promise<ApiResponse<null>> {
  return request.post(`/moment/unlike/${momentId}`)
}

export function commentMoment(momentId: number, content: string): Promise<ApiResponse<MomentComment>> {
  return request.post(`/moment/comment/${momentId}`, null, { params: { content } })
}

export function getMomentDetail(momentId: number, extraConfig?: any): Promise<ApiResponse<MomentItem>> {
  return request.get(`/moment/detail/${momentId}`, {
    ...extraConfig,
    headers: { ...(extraConfig?.headers || {}) }
  })
}

export function deleteMoment(id: number): Promise<ApiResponse<null>> {
  return request.post(`/moment/delete/${id}`)
}

export function deleteComment(id: number): Promise<ApiResponse<null>> {
  return request.post(`/moment/deleteComment/${id}`)
}
