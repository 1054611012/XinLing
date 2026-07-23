<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showConfirmDialog, showToast } from 'vant'
import { useAuthStore } from '@/stores/auth'
import { DEFAULT_AVATAR } from '@/utils/constants'
import { getDevices, getInviter } from '@/api/user'
import vipIcon from '@/assets/vip.png'

const router = useRouter()
const authStore = useAuthStore()

const deviceCount = ref(0)
const inviterName = ref<string | null>(null)

async function loadProfileData() {
  try {
    const devRes = await getDevices()
    deviceCount.value = (devRes.data || []).length
  } catch {
    // ignore
  }
  try {
    const invRes = await getInviter()
    inviterName.value = invRes.data.inviterNickname
  } catch {
    // ignore
  }
}

function goTo(path: string) {
  router.push(path)
}

function getVipLabel(status: number): string {
  if (status === 0) return '普通用户'
  if (status === 1) return 'VIP会员'
  if (status === 2) return '终身VIP'
  return '未知'
}

async function handleLogout() {
  try {
    await showConfirmDialog({
      title: '退出登录',
      message: '确定要退出登录吗？'
    })
    authStore.logout()
    showToast('已退出')
  } catch {
    // cancelled
  }
}

onMounted(() => {
  if (authStore.isLoggedIn) {
    authStore.fetchUserInfo()
  }
  loadProfileData()
})
</script>

<template>
  <div class="tide-profile-page">
    <!-- 用户信息卡片 -->
    <div class="user-card">
      <van-image
        round
        width="80"
        height="80"
        :src="authStore.userInfo?.avatar || DEFAULT_AVATAR"
      />
      <div class="user-info">
        <div class="user-nickname">
          {{ authStore.userInfo?.nickname || '用户' }}
        </div>
        <div class="user-phone">{{ authStore.userInfo?.phone || '--' }}</div>
        <div class="user-vip" v-if="authStore.isVip">
          <img :src="vipIcon" alt="VIP" style="width: 12px; height: 12px;" />
          <span>{{ getVipLabel(authStore.userInfo?.vipStatus ?? 0) }}</span>
        </div>
      </div>
      <van-icon name="edit" size="20" color="#999" @click="goTo('/profile/edit')" />
    </div>

    <!-- VIP卡片 -->
    <div class="vip-card" @click="goTo('/vip')">
      <div class="vip-left">
        <img :src="vipIcon" alt="VIP" style="width: 24px; height: 24px;" />
        <div class="vip-text">
          <div class="vip-title">{{ getVipLabel(authStore.userInfo?.vipStatus ?? 0) }}</div>
          <div class="vip-desc">{{ authStore.isVip ? '畅享所有功能' : '开通会员解锁更多功能' }}</div>
        </div>
      </div>
      <div class="vip-action" v-if="!authStore.isVip">
        <span>立即开通</span>
        <van-icon name="arrow" size="16" />
      </div>
    </div>

    <!-- 菜单列表 -->
    <div class="menu-section">
      <div class="menu-group">
        <div class="menu-item" @click="goTo('/profile/edit')">
          <van-icon name="user-circle-o" size="20" color="#666" />
          <span class="menu-label">编辑资料</span>
          <van-icon name="arrow" size="16" color="#ccc" />
        </div>
        <div class="menu-item" @click="goTo('/profile/settings')">
          <van-icon name="setting-o" size="20" color="#666" />
          <span class="menu-label">偏好设置</span>
          <van-icon name="arrow" size="16" color="#ccc" />
        </div>
        <div class="menu-item" @click="goTo('/profile/devices')">
          <van-icon name="desktop-o" size="20" color="#666" />
          <span class="menu-label">设备管理</span>
          <span class="menu-badge">{{ deviceCount }}台</span>
          <van-icon name="arrow" size="16" color="#ccc" />
        </div>
      </div>

      <div class="menu-group">
        <div class="menu-item" @click="goTo('/growth')">
          <van-icon name="bar-chart-o" size="20" color="#666" />
          <span class="menu-label">成长中心</span>
          <van-icon name="arrow" size="16" color="#ccc" />
        </div>
        <div class="menu-item" @click="goTo('/rank')">
          <van-icon name="ascending" size="20" color="#666" />
          <span class="menu-label">排行榜</span>
          <van-icon name="arrow" size="16" color="#ccc" />
        </div>
        <div class="menu-item" @click="goTo('/challenge')">
          <van-icon name="medal-o" size="20" color="#666" />
          <span class="menu-label">挑战活动</span>
          <van-icon name="arrow" size="16" color="#ccc" />
        </div>
      </div>

      <div class="menu-group">
        <div class="menu-item" @click="goTo('/vip')">
          <van-icon name="vip-card-o" size="20" color="#666" />
          <span class="menu-label">会员中心</span>
          <van-icon name="arrow" size="16" color="#ccc" />
        </div>
        <div class="menu-item" @click="goTo('/order')">
          <van-icon name="orders-o" size="20" color="#666" />
          <span class="menu-label">我的订单</span>
          <van-icon name="arrow" size="16" color="#ccc" />
        </div>
        <div class="menu-item" @click="goTo('/notification')">
          <van-icon name="bell-o" size="20" color="#666" />
          <span class="menu-label">消息通知</span>
          <van-icon name="arrow" size="16" color="#ccc" />
        </div>
      </div>
    </div>

    <!-- 退出登录 -->
    <div class="logout-section">
      <van-button block plain round @click="handleLogout" class="logout-btn">
        退出登录
      </van-button>
    </div>
  </div>
</template>

<style scoped>
.tide-profile-page {
  background: #f8f8f8;
  min-height: 100vh;
  padding: 20px 16px 80px;
}

/* 用户信息卡片 */
.user-card {
  background: #fff;
  border-radius: 16px;
  padding: 24px 20px;
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.user-info {
  flex: 1;
}

.user-nickname {
  font-size: 20px;
  font-weight: 600;
  color: #333;
  margin-bottom: 4px;
}

.user-phone {
  font-size: 14px;
  color: #999;
  margin-bottom: 4px;
}

.user-vip {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #ffa940;
}

/* VIP卡片 */
.vip-card {
  background: linear-gradient(135deg, #ffd3b6, #ffaaa5);
  border-radius: 16px;
  padding: 20px;
  margin-bottom: 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 4px 12px rgba(255, 170, 165, 0.3);
}

.vip-left {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
}

.vip-text {
  flex: 1;
}

.vip-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 4px;
}

.vip-desc {
  font-size: 14px;
  color: #666;
}

.vip-action {
  display: flex;
  align-items: center;
  gap: 4px;
  background: #fff;
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 500;
  color: #ff6b6b;
}

/* 菜单列表 */
.menu-section {
  margin-bottom: 24px;
}

.menu-group {
  background: #fff;
  border-radius: 12px;
  margin-bottom: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.menu-item {
  display: flex;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #f5f5f5;
  cursor: pointer;
  transition: background 0.2s;
}

.menu-item:last-child {
  border-bottom: none;
}

.menu-item:active {
  background: #f8f8f8;
}

.menu-label {
  flex: 1;
  margin-left: 12px;
  font-size: 16px;
  color: #333;
}

.menu-badge {
  font-size: 12px;
  color: #999;
  margin-right: 8px;
}

/* 退出登录 */
.logout-section {
  margin-top: 32px;
}

.logout-btn {
  height: 48px;
  font-size: 16px;
  color: #ff6b6b;
  border-color: #ff6b6b;
}
</style>