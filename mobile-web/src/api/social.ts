import request from './request'
import type { ApiResponse } from '@/types/api'

export interface RankItem {
  userId: number
  nickname: string
  avatar: string
  value: number | string
  rank: number
}

export interface ChallengeItem {
  id: number
  title: string
  description: string
  cover: string
  type: string
  duration: number
  conditionValue: number
  pointsReward: number
  vipDaysReward: number
  joinedCount: number
  currentDay: number
  completedDays: number
  totalDays: number
  progressPercent: number
  status: number
  userStatus: number
  startTime: string
  endTime: string
}

export interface FollowUser {
  userId: number
  nickname: string
  avatar: string
  isFollowed: boolean
  createTime: string
}

export interface PrivateMessage {
  id: number
  fromUserId: number
  toUserId: number
  content: string
  isRead: boolean
  createTime: string
}

// ===== Rank =====
export function getRankList(type: string, period: string = 'week'): Promise<ApiResponse<{ records: RankItem[] }>> {
  return request.get('/rank/' + type, { params: { period } })
}

// ===== Challenge =====
export function getChallengeList(): Promise<ApiResponse<{ records: ChallengeItem[] }>> {
  return request.get('/challenge/list')
}

export function getMyChallenges(): Promise<ApiResponse<{ records: ChallengeItem[] }>> {
  return request.get('/challenge/my')
}

export function joinChallenge(id: number): Promise<ApiResponse<null>> {
  return request.post(`/challenge/join/${id}`)
}

export function getChallengeProgress(id: number): Promise<ApiResponse<ChallengeItem>> {
  return request.get(`/challenge/progress/${id}`)
}

export function dailyCheckin(id: number): Promise<ApiResponse<null>> {
  return request.post(`/challenge/dailyCheckin/${id}`)
}

// ===== Follow =====
export function getFollowers(userId?: number): Promise<ApiResponse<{ records: FollowUser[] }>> {
  return request.get('/user/followers/' + (userId || ''))
}

export function getFollowing(userId?: number): Promise<ApiResponse<{ records: FollowUser[] }>> {
  return request.get('/user/following/' + (userId || ''))
}

export function followUser(userId: number): Promise<ApiResponse<null>> {
  return request.post(`/user/follow/${userId}`)
}

export function unfollowUser(userId: number): Promise<ApiResponse<null>> {
  return request.post(`/user/unfollow/${userId}`)
}

// ===== Message =====
export function getMessageList(page: number = 1, size: number = 20): Promise<ApiResponse<{ records: PrivateMessage[] }>> {
  return request.get('/message/list', { params: { page, size } })
}

export function sendMessage(toUserId: number, content: string): Promise<ApiResponse<PrivateMessage>> {
  return request.post('/message/send', { toUserId, content })
}
