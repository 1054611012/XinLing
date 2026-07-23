import { createRouter, createWebHashHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import { getToken } from '@/utils/storage'

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/LoginPage.vue'),
    meta: { title: '登录', noAuth: true, depth: 0 }
  },
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/home/HomePage.vue'),
    meta: { title: '首页', depth: 0 }
  },
  {
    path: '/explore',
    name: 'Explore',
    component: () => import('@/views/explore/ExplorePage.vue'),
    meta: { title: '探索', depth: 0 }
  },
  {
    path: '/sleep',
    name: 'Sleep',
    component: () => import('@/views/sleep/SleepPage.vue'),
    meta: { title: '睡眠', depth: 1 }
  },
  {
    path: '/audio',
    name: 'Audio',
    component: () => import('@/views/audio/AudioPage.vue'),
    meta: { title: '音频', depth: 1 }
  },
  {
    path: '/chat',
    name: 'Chat',
    component: () => import('@/views/chat/ChatPage.vue'),
    meta: { title: 'AI 助手', depth: 1 }
  },
  {
    path: '/ontology',
    name: 'Ontology',
    component: () => import('@/views/chat/OntologyChatPage.vue'),
    meta: { title: '本体智能体', depth: 1 }
  },
  {
    path: '/community',
    name: 'Community',
    component: () => import('@/views/community/CommunityPage.vue'),
    meta: { title: '社区', depth: 0 }
  },
  {
    path: '/moment/create',
    name: 'MomentCreate',
    component: () => import('@/views/moment/MomentCreatePage.vue'),
    meta: { title: '发布动态', depth: 1 }
  },
  {
    path: '/moment/:id',
    name: 'MomentDetail',
    component: () => import('@/views/moment/MomentDetail.vue'),
    meta: { title: '动态详情', depth: 1 }
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('@/views/profile/ProfilePage.vue'),
    meta: { title: '我的', depth: 0 }
  },
  {
    path: '/vip',
    name: 'Vip',
    component: () => import('@/views/vip/VipPage.vue'),
    meta: { title: '会员中心', depth: 1 }
  },
  {
    path: '/distribution',
    name: 'Distribution',
    component: () => import('@/views/distribution/DistributionPage.vue'),
    meta: { title: '推广中心', depth: 1 }
  },
  {
    path: '/notification',
    name: 'Notification',
    component: () => import('@/views/notification/NotificationPage.vue'),
    meta: { title: '消息通知', depth: 1 }
  },

  // ===== Order Module =====
  {
    path: '/order',
    name: 'OrderList',
    component: () => import('@/views/order/OrderListPage.vue'),
    meta: { title: '我的订单', depth: 1 }
  },
  {
    path: '/order/:orderNo',
    name: 'OrderDetail',
    component: () => import('@/views/order/OrderDetailPage.vue'),
    meta: { title: '订单详情', depth: 2 }
  },

  // ===== Profile Sub-pages =====
  {
    path: '/profile/edit',
    name: 'ProfileEdit',
    component: () => import('@/views/settings/ProfileEditPage.vue'),
    meta: { title: '编辑资料', depth: 1 }
  },
  {
    path: '/profile/settings',
    name: 'Settings',
    component: () => import('@/views/settings/SettingsPage.vue'),
    meta: { title: '偏好设置', depth: 1 }
  },
  {
    path: '/profile/devices',
    name: 'DeviceManage',
    component: () => import('@/views/device/DeviceManagePage.vue'),
    meta: { title: '设备管理', depth: 1 }
  },

  // ===== Growth Module =====
  {
    path: '/growth',
    name: 'Growth',
    component: () => import('@/views/growth/GrowthPage.vue'),
    meta: { title: '成长中心', depth: 1 }
  },
  {
    path: '/achievement',
    name: 'Achievement',
    component: () => import('@/views/growth/AchievementPage.vue'),
    meta: { title: '成就列表', depth: 2 }
  },
  {
    path: '/task',
    name: 'Task',
    component: () => import('@/views/growth/TaskPage.vue'),
    meta: { title: '每日任务', depth: 2 }
  },
  {
    path: '/mall',
    name: 'Mall',
    component: () => import('@/views/growth/MallPage.vue'),
    meta: { title: '积分商城', depth: 2 }
  },

  // ===== Ranking =====
  {
    path: '/rank',
    name: 'Rank',
    component: () => import('@/views/rank/RankPage.vue'),
    meta: { title: '排行榜', depth: 1 }
  },

  // ===== Challenge Module =====
  {
    path: '/challenge',
    name: 'ChallengeList',
    component: () => import('@/views/challenge/ChallengeListPage.vue'),
    meta: { title: '挑战活动', depth: 1 }
  },
  {
    path: '/challenge/:id',
    name: 'ChallengeDetail',
    component: () => import('@/views/challenge/ChallengeDetailPage.vue'),
    meta: { title: '挑战详情', depth: 2 }
  },

  // ===== Activity Module =====
  {
    path: '/activity',
    name: 'ActivityList',
    component: () => import('@/views/activity/ActivityListPage.vue'),
    meta: { title: '活动中心', depth: 1 }
  },

  // ===== Message Module =====
  {
    path: '/message',
    name: 'MessageList',
    component: () => import('@/views/message/MessageListPage.vue'),
    meta: { title: '私信', depth: 1 }
  },
  {
    path: '/message/:userId',
    name: 'MessageChat',
    component: () => import('@/views/message/MessageChatPage.vue'),
    meta: { title: '聊天', depth: 2 }
  },

  // ===== Social Module =====
  {
    path: '/followers',
    name: 'Followers',
    component: () => import('@/views/community/FollowersPage.vue'),
    meta: { title: '粉丝', depth: 1 }
  },
  {
    path: '/following',
    name: 'Following',
    component: () => import('@/views/community/FollowingPage.vue'),
    meta: { title: '关注', depth: 1 }
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

router.beforeEach((to, _from, next) => {
  document.title = to.meta.title as string || '心聆'
  const token = getToken()
  if (!to.meta.noAuth && !token) {
    next('/login')
  } else {
    next()
  }
})

export default router
