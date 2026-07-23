import request from './request'
import type { ApiResponse, NotificationItem } from '@/types/api'

export function getNotificationList(page: number = 1, size: number = 20): Promise<ApiResponse<NotificationItem[]>> {
  return request.get('/notification/list', { params: { page, size } })
}

export function markNotificationRead(id: number): Promise<ApiResponse<null>> {
  return request.post(`/notification/markRead/${id}`)
}

export function markAllNotificationsRead(): Promise<ApiResponse<null>> {
  return request.post('/notification/markAllRead')
}

export function deleteNotification(id: number): Promise<ApiResponse<null>> {
  return request.post(`/notification/delete/${id}`)
}
