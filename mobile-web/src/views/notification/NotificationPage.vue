<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { showToast } from 'vant'
import { getNotificationList, markNotificationRead, markAllNotificationsRead } from '@/api/notification'
import type { NotificationItem } from '@/types/api'

const notifications = ref<NotificationItem[]>([])
const loading = ref(false)
const refreshing = ref(false)
const finished = ref(false)
const page = ref(1)
const PAGE_SIZE = 20

const unreadCount = computed(() => {
  return notifications.value.filter(n => !n.isRead).length
})

async function loadNotifications(append = false) {
  loading.value = true
  try {
    const res = await getNotificationList(append ? page.value : 1, PAGE_SIZE)
    const list = Array.isArray(res.data) ? res.data : []
    if (append) {
      notifications.value = [...notifications.value, ...list]
    } else {
      notifications.value = list
    }
    finished.value = list.length < PAGE_SIZE
    if (!append) page.value = 1
  } catch {
    showToast('加载失败')
  } finally {
    loading.value = false
    refreshing.value = false
  }
}

function onRefresh() {
  refreshing.value = true
  page.value = 1
  loadNotifications()
}

function onLoadMore() {
  page.value++
  loadNotifications(true)
}

async function handleRead(id: number) {
  try {
    await markNotificationRead(id)
    const notif = notifications.value.find(n => n.id === id)
    if (notif) notif.isRead = true
  } catch {
    // ignore
  }
}

async function handleMarkAllRead() {
  try {
    await markAllNotificationsRead()
    notifications.value.forEach(n => { n.isRead = true })
    showToast('已全部标记为已读')
  } catch {
    showToast('操作失败')
  }
}

function getTypeIcon(type: string): string {
  switch (type) {
    case 'system': return 'info-o'
    case 'like': return 'like-o'
    case 'comment': return 'chat-o'
    case 'achievement': return 'award-o'
    default: return 'bell-o'
  }
}

onMounted(() => {
  loadNotifications()
})
</script>

<template>
  <div class="page notification-page">
    <!-- Header with mark all read -->
    <div class="notification-header">
      <div class="header-left">
        消息通知
        <span v-if="unreadCount > 0" class="unread-badge">{{ unreadCount }}</span>
      </div>
      <span class="mark-all-btn" v-if="unreadCount > 0" @click="handleMarkAllRead">
        全部已读
      </span>
    </div>

    <!-- List -->
    <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
      <van-list
        v-model:loading="loading"
        :finished="finished"
        finished-text="没有更多通知"
        @load="onLoadMore"
      >
        <div class="notification-list">
          <div
            v-for="notif in notifications"
            :key="notif.id"
            class="notification-item glass-card"
            :class="{ unread: !notif.isRead }"
            @click="handleRead(notif.id)"
          >
            <div class="notif-icon">
              <van-badge :dot="!notif.isRead">
                <AppIcon :name="getTypeIcon(notif.type)" size="24" color="#7c5cff" />
              </van-badge>
            </div>
            <div class="notif-content">
              <div class="notif-title">{{ notif.title }}</div>
              <div class="notif-body">{{ notif.content }}</div>
              <div class="notif-time">{{ notif.createTime }}</div>
            </div>
          </div>
        </div>
      </van-list>
    </van-pull-refresh>

    <van-empty v-if="!loading && notifications.length === 0" description="暂无通知" />
  </div>
</template>

<style scoped>
.notification-page {
  min-height: 100vh;
  min-height: 100dvh;
}

.notification-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  font-size: 18px;
  font-weight: 600;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 8px;
}

.unread-badge {
  background: #ee0a24;
  color: white;
  font-size: 11px;
  padding: 2px 8px;
  border-radius: 10px;
  font-weight: 500;
}

.mark-all-btn {
  font-size: 13px;
  color: var(--app-accent);
  font-weight: 400;
  cursor: pointer;
}

.notification-list {
  padding: 0 16px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.notification-item {
  display: flex;
  gap: 12px;
  padding: 14px 16px;
  cursor: pointer;
}

.notification-item.unread {
  border-left: 3px solid var(--app-accent);
}

.notif-icon {
  flex-shrink: 0;
  padding-top: 2px;
}

.notif-content {
  flex: 1;
  min-width: 0;
}

.notif-title {
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 4px;
}

.notif-body {
  font-size: 13px;
  color: var(--app-text-secondary);
  line-height: 1.5;
  margin-bottom: 4px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.notif-time {
  font-size: 11px;
  color: var(--app-text-secondary);
}
</style>
