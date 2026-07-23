<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showLoadingToast, closeToast } from 'vant'
import { getMessageList } from '@/api/social'
import type { PrivateMessage } from '@/api/social'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()
const messages = ref<PrivateMessage[]>([])
const loading = ref(false)

async function loadData() {
  loading.value = true
  const toast = showLoadingToast({ message: '加载中...', forbidClick: true, duration: 0 })
  try {
    const res = await getMessageList()
    messages.value = res.data.records || []
  } catch {
    showToast('加载失败')
  } finally {
    closeToast()
    loading.value = false
  }
}

function goChat(userId: number) {
  router.push(`/message/${userId}`)
}

onMounted(() => { loadData() })
</script>

<template>
  <div class="page message-list-page">
    <van-nav-bar title="私信" left-arrow @click-left="router.back()" />

    <div class="message-list">
      <div class="empty-section" v-if="!loading && messages.length === 0">
        <van-empty description="暂无私信">
          <template #image>
            <van-icon name="chat-o" size="64" color="#8888aa" />
          </template>
          <template #description>
            <p class="empty-text">暂无私信消息</p>
            <p class="empty-hint">开通VIP后可与好友私信聊天</p>
          </template>
        </van-empty>
        <van-tag v-if="!authStore.isVip" color="#f5af19" size="medium" style="margin-top: 12px;">
          VIP专属功能
        </van-tag>
      </div>

      <div
        v-for="msg in messages"
        :key="msg.id"
        class="message-item glass-card"
        @click="goChat(msg.fromUserId || msg.toUserId)"
      >
        <van-image round width="44" height="44" :src="''">
          <template v-slot:error>
            <div class="msg-avatar-placeholder">{{ (msg.fromUserId || '').toString().slice(-2) }}</div>
          </template>
        </van-image>
        <div class="msg-info">
          <div class="msg-name">用户{{ (msg.fromUserId || msg.toUserId) }}</div>
          <div class="msg-preview">{{ msg.content }}</div>
        </div>
        <div class="msg-time">{{ msg.createTime }}</div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.message-list-page {
  min-height: 100vh;
  min-height: 100dvh;
}

.message-list {
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.empty-section {
  text-align: center;
  padding: 40px 0;
}

.empty-text {
  font-size: 14px;
  color: var(--app-text-secondary);
}

.empty-hint {
  font-size: 12px;
  color: var(--app-text-secondary);
  margin-top: 4px;
  opacity: 0.7;
}

.message-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  cursor: pointer;
}

.msg-avatar-placeholder {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  background: rgba(124, 92, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  color: #7c5cff;
}

.msg-info {
  flex: 1;
  min-width: 0;
}

.msg-name {
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 4px;
}

.msg-preview {
  font-size: 13px;
  color: var(--app-text-secondary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.msg-time {
  font-size: 11px;
  color: var(--app-text-secondary);
  flex-shrink: 0;
}
</style>
