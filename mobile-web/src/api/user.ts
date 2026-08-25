import request from './request'
import type {
  ApiResponse,
  AppLoginBody,
  LoginResponseVO,
  SendCodeBody,
  AppUserInfoVO,
  UpdateUserBody,
  UserDevice,
  UserSettings,
  ThirdLoginBody
} from '@/types/api'

export function login(data: AppLoginBody): Promise<ApiResponse<LoginResponseVO>> {
  return request.post('/user/login', data, { headers: { _loading: false } })
}

/** 微信快捷登录：携带微信授权 code 换取 token */
export function wechatLogin(data: ThirdLoginBody): Promise<ApiResponse<LoginResponseVO>> {
  return request.post('/user/thirdLogin', data, { headers: { _loading: false } })
}

export function sendCode(data: SendCodeBody): Promise<ApiResponse<null>> {
  return request.post('/user/sendCode', data, { headers: { _loading: false } })
}

export function getUserInfo(): Promise<ApiResponse<AppUserInfoVO>> {
  return request.get('/user/info')
}

export function updateUser(data: UpdateUserBody): Promise<ApiResponse<null>> {
  return request.post('/user/update', data)
}

export function updateAvatar(avatarUrl: string): Promise<ApiResponse<{ avatarUrl: string }>> {
  return request.post('/user/updateAvatar', null, { params: { avatarUrl } })
}

export function getDevices(): Promise<ApiResponse<UserDevice[]>> {
  return request.get('/user/devices')
}

export function logoutDevice(deviceId: number | string): Promise<ApiResponse<null>> {
  return request.post(`/user/logoutDevice/${deviceId}`)
}

export function deleteAccount(): Promise<ApiResponse<null>> {
  return request.post('/user/deleteAccount')
}

export function getSettings(): Promise<ApiResponse<UserSettings>> {
  return request.get('/user/settings')
}

export function updateSettings(data: Partial<UserSettings>): Promise<ApiResponse<null>> {
  return request.post('/user/settings/update', data)
}

export function getInviter(): Promise<ApiResponse<{ inviterNickname: string; inviterAvatar: string }>> {
  return request.get('/user/inviter')
}

export function exportData(): Promise<ApiResponse<{ downloadUrl: string }>> {
  return request.get('/user/exportData')
}
