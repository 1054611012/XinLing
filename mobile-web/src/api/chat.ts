import request from './request'
import type { ApiResponse, ChatMessage, SendMessageParams } from '@/types/api'

export function sendMessage(data: SendMessageParams): Promise<ApiResponse<{ reply: ChatMessage }>> {
  return request.post('/chat/send', data, {
    timeout: 120000,
    headers: { _loading: false }
  })
}

export function getChatHistory(sessionId?: string, page: number = 1, size: number = 50): Promise<ApiResponse<{ messages: ChatMessage[]; total: number }>> {
  return request.get('/chat/history', { params: { sessionId, page, size } })
}
