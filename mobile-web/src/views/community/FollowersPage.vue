<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showLoadingToast, closeToast } from 'vant'
import { getFollowers, followUser } from '@/api/social'
import type { FollowUser } from '@/api/social'

const router = useRouter()
const users = ref<FollowUser[]>([])
const loading = ref(false)

async function loadData() {
  loading.value = true
  const toast = showLoadingToast({ message: '加载中...', forbidClick: true, duration: 0 })
  try {
    const res = await getFollowers()
    users.value = res.data.records || []
  } catch {
    showToast('加载失败')
  } finally {
    closeToast()
    loading.value = false
  }
}

async function handleFollow(userId: number) {
  try {
    await followUser(userId)
    showToast('关注成功')
    loadData()
  } catch {
    showToast('操作失败')
  }
}

onMounted(() => { loadData() })
</script>

<template>
  <div class="page followers-page">
    <van-nav-bar title="粉丝" left-arrow @click-left="router.back()" />

    <div class="user-list">
      <div
        v-for="(user, index) in users"
        :key="user.userId"
        class="user-item glass-card"
        :style="{ animationDelay: `${index * 0.06}s` }"
      >
        <van-image
          round
          width="44"
          height="44"
          :src="user.avatar || ''"
        >
          <template v-if="!user.avatar" #error>
            <div class="user-avatar-placeholder">
              <van-icon name="contact" size="22" color="#6b6b8d" />
            </div>
          </template>
        </van-image>
        <div class="user-info">
          <div class="user-name">{{ user.nickname }}</div>
          <div class="user-create-time">关注于 {{ user.createTime }}</div>
        </div>
        <van-button
          round
          size="small"
          :type="user.isFollowed ? 'default' : 'primary'"
          :plain="user.isFollowed"
          class="follow-btn"
          @click="handleFollow(user.userId)"
        >
          {{ user.isFollowed ? '已关注' : '关注' }}
        </van-button>
      </div>

      <van-empty v-if="!loading && users.length === 0" description="暂无粉丝" />
    </div>
  </div>
</template>

<style scoped>
.followers-page {
  min-height: 100vh;
  min-height: 100dvh;
}

.user-list {
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.user-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  animation: fade-up 0.35s ease both;
}

@keyframes fade-up {
  from {
    opacity: 0;
    transform: translateY(12px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.user-avatar-placeholder {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  background: var(--app-accent-bg);
  display: flex;
  align-items: center;
  justify-content: center;
}

.user-info {
  flex: 1;
  min-width: 0;
}

.user-name {
  font-size: 15px;
  font-weight: 500;
  margin-bottom: 2px;
}

.user-create-time {
  font-size: 12px;
  color: var(--app-text-secondary);
}

.follow-btn {
  transition: transform 0.2s ease;
  flex-shrink: 0;
}

.follow-btn:active {
  transform: scale(0.92);
}
</style>
