// ===== 通用响应 =====
export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
  timestamp: number
}

// ===== 用户 =====
export interface AppUserInfoVO {
  id: number
  nickname: string
  avatar: string
  phone: string
  email: string
  gender: number      // 0-未知 1-男 2-女
  birthday: string
  vipStatus: number   // 0-普通 1-VIP 2-终身VIP
  inviterId: number | null
}

export interface AppLoginBody {
  phone: string
  code: string
  deviceId?: string
  deviceName?: string
  deviceType?: string
  inviterId?: number
}

export interface LoginResponseVO {
  token: string
  userInfo: AppUserInfoVO
}

export interface SendCodeBody {
  phone: string
  scene?: string  // login/register/bind
}

export interface UpdateUserBody {
  nickname?: string
  gender?: number
  birthday?: string
}

export interface UserDevice {
  id: number
  userId: number
  deviceId: string
  deviceName: string
  deviceType: string
  loginTime: string
  lastActiveTime: string
  ipAddress: string
}

export interface UserSettings {
  defaultFocusTime: number
  defaultBreakTime: number
  defaultAudioId: number | null
  darkMode: number
  notification: number
  volume: number
  aiVoiceId: string
}

// ===== 专注 =====
export interface FocusRecord {
  id: number
  userId: number
  startTime: string
  endTime: string
  duration: number
  status: number     // 0-进行中 1-已完成 2-已中断
  mode: string       // tomato/deep/free
  tag: string
  interruptCount: number
  note: string
  audioMixId: number
}

export interface StartFocusParams {
  mode: string
  duration: number
}

export interface FocusSettings {
  strictMode: number
  appBlock: number
  allowedApps: string
  notificationBlock: number
  aiEncouragement: number
  encouragementInterval: number
}

// ===== 睡眠 =====
export interface SleepRecord {
  id: number
  userId: number
  startTime: string
  endTime: string
  duration: number
  sleepScore: number
  deepSleepMinutes: number
  lightSleepMinutes: number
  remSleepMinutes: number
  interruptCount: number
  snoringCount: number
}

export interface SleepReport {
  score: number
  duration: number
  deepSleepPercent: number
  lightSleepPercent: number
  awakeCount: number
  suggestion: string
}

// ===== 音频 =====
export interface AudioItem {
  id: number
  title: string
  audioUrl: string
  duration: number
  fileType: string        // audio / video / image
  fileExt: string | null  // mp3 / wav / mp4 ...
  sourceType: string | null // upload / system
  narrator: string | null
  playCount: number
  isFavorite: boolean
  tags: string[]
}

export interface AudioMix {
  id: number
  name: string
  audioIds: number[]
  audios: AudioItem[]
  isDefault: boolean
}

// ===== AI 聊天 =====
export interface ChatMessage {
  id: number
  role: string
  content: string
  createTime: string
  audioUrl?: string   // TTS 语音合成 URL（可选）
}

export interface SendMessageParams {
  content: string
  sessionId?: string
  voice?: string      // TTS 语音角色：XIAOXIAO / YUNXI / XIAOXUAN 等
}

/** TTS 语音角色 */
export interface TtsVoiceItem {
  name: string
  displayName: string
  style: string
}

// ===== 社区 =====
export interface MomentItem {
  id: number
  userId: number
  nickname: string
  avatar: string
  content: string
  images: string[]
  likeCount: number
  commentCount: number
  isLiked: boolean
  createTime: string
  vipStatus: number  // 0-普通 1-VIP 2-终身VIP
}

export interface MomentComment {
  id: number
  momentId: number
  userId: number
  nickname: string
  avatar: string
  content: string
  createTime: string
  vipStatus: number  // 0-普通 1-VIP 2-终身VIP
}

export interface PublishMomentParams {
  content: string
  images: string[]
  tags?: string[]
  isAnonymous?: number
  visibility?: number
}

export interface VipInfo {
  vipStatus: number
  expireTime: string
  remainingDays: number
}

// ===== 订单 =====
export interface VipPackage {
  id: number
  name: string
  description: string
  price: number
  originalPrice: number
  days: number
  type: string     // month/quarter/year/lifetime
  status: number
  sortOrder: number
}

export interface PayOrder {
  id: number
  orderNo: string
  packageId: number
  packageName: string
  amount: number
  payAmount: number
  orderStatus: number  // 0-待支付 1-已支付 2-已取消 3-已退款
  payType: string
  payTime?: string
  createTime: string
}

export interface CreateOrderParams {
  productId: number
  payType: string
  couponId?: number
}

// ===== 首页 =====
export interface HomeInfoVO {
  greeting: string
  todayFocusMinutes: number
  todaySleepMinutes: number
  continuousDays: number
  recommendedAudio: HomeRecommendedAudio[]
  aiTopic: HomeAiTopic | null
  hotMoments: HomeHotMoment[]
}

export interface HomeRecommendedAudio {
  id: number
  title: string
  cover: string
  author: string
  duration: number
  playCount: number
}

export interface HomeAiTopic {
  title: string
  content: string
}

export interface HomeHotMoment {
  id: number
  userId: number
  nickname: string
  avatar: string
  content: string
  likeCount: number
  commentCount: number
  createTime: string
  vipStatus: number  // 0-普通 1-VIP 2-终身VIP
}

// ===== 推广 =====
export interface DistributorInfoVO {
  distributorId: number
  level: number
  levelName: string
  status: number
  totalCommission: number
  availableCommission: number
  totalFans: number
  totalOrders: number
  promotionCode: string
}

export interface CommissionRecord {
  id: number
  amount: number
  type: string
  source: string
  status: string
  createTime: string
}

export interface TeamMember {
  userId: number
  nickname: string
  avatar: string
  joinTime: string
  contribution: number
}

// ===== 通知 =====
export interface NotificationItem {
  id: number
  type: string
  title: string
  content: string
  isRead: boolean
  createTime: string
  relateId: number
}
