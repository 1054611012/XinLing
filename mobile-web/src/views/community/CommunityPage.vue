<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import { getMoments, likeMoment, unlikeMoment } from '@/api/community'
import { DEFAULT_AVATAR } from '@/utils/constants'
import type { MomentItem } from '@/types/api'

const router = useRouter()
const activeTab = ref('recommend')
const moments = ref<MomentItem[]>([])
const loading = ref(false)
const refreshing = ref(false)
const finished = ref(false)
const page = ref(1)
const PAGE_SIZE = 10

/** 格式化相对时间 */
function formatRelativeTime(timeStr: string): string {
  if (!timeStr) return ''
  const now = Date.now()
  const time = new Date(timeStr).getTime()
  if (isNaN(time)) return timeStr
  const diff = now - time

  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`
  if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前`
  if (diff < 604800000) return `${Math.floor(diff / 86400000)}天前`
  // 超过7天显示日期
  const d = new Date(time)
  return `${d.getMonth() + 1}月${d.getDate()}日`
}

/** 格式化计数（万单位） */
function formatCount(count: number): string {
  if (count >= 10000) {
    return (count / 10000).toFixed(1) + '万'
  }
  return String(count)
}

async function loadMoments(append = false) {
  loading.value = true
  try {
    const res = await getMoments(activeTab.value, append ? page.value : 1, PAGE_SIZE, {
      headers: { _loading: false }
    })
    const list = Array.isArray(res.data) ? res.data : []
    if (append) {
      moments.value = [...moments.value, ...list]
    } else {
      moments.value = list
      page.value = 1
    }
    finished.value = list.length < PAGE_SIZE
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
  loadMoments()
}

function onLoadMore() {
  if (finished.value) return
  page.value++
  loadMoments(true)
}

async function handleLike(moment: MomentItem) {
  try {
    if (moment.isLiked) {
      await unlikeMoment(moment.id)
      moment.isLiked = false
      moment.likeCount = Math.max(0, moment.likeCount - 1)
    } else {
      await likeMoment(moment.id)
      moment.isLiked = true
      moment.likeCount = (moment.likeCount || 0) + 1
    }
  } catch {
    showToast('操作失败')
  }
}

function goDetail(id: number) {
  router.push(`/moment/${id}`)
}

function goPublish() {
  router.push('/moment/create')
}

onMounted(() => {
  loadMoments()
})
</script>

<template>
  <div class="page community-page">
    <!-- Tabs -->
    <van-tabs v-model:active="activeTab" sticky class="community-tabs">
      <van-tab title="推荐" name="recommend" />
      <van-tab title="关注" name="following" />
    </van-tabs>

    <!-- Moments List -->
    <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
      <van-list
        v-model:loading="loading"
        :finished="finished"
        finished-text="没有更多了"
        @load="onLoadMore"
      >
        <!-- Empty state by tab -->
        <van-empty
          v-if="!loading && moments.length === 0 && activeTab === 'recommend'"
          description="暂无动态，快来发布第一条吧"
        />
        <van-empty
          v-if="!loading && moments.length === 0 && activeTab === 'following'"
          description="关注更多用户，发现精彩内容"
        />

        <div class="moment-list">
          <div
            v-for="(moment, index) in moments"
            :key="moment.id"
            class="moment-card glass-card"
            :style="{ animationDelay: `${index * 0.05}s` }"
            @click="goDetail(moment.id)"
          >
            <!-- User info -->
            <div class="moment-header">
              <van-image
                round
                width="36"
                height="36"
                :src="moment.avatar || DEFAULT_AVATAR"
              >
                <template v-if="!moment.avatar" #error>
                  <div class="avatar-placeholder">
                    <van-icon name="contact" size="18" color="#6b6b8d" />
                  </div>
                </template>
              </van-image>
              <div class="moment-user-info">
                <div class="moment-nickname-row">
                  <span class="moment-nickname">{{ moment.nickname || '匿名用户' }}</span>
                  <van-tag v-if="moment.vipStatus > 0" round size="small" color="#f5af19" class="vip-tag">VIP</van-tag>
                </div>
                <div class="moment-time">{{ formatRelativeTime(moment.createTime) }}</div>
              </div>
            </div>

            <!-- Content -->
            <div class="moment-content">{{ moment.content }}</div>

            <!-- Images -->
            <div
              v-if="moment.images && moment.images.length > 0"
              class="moment-images-grid"
            >
              <template v-if="moment.images.length === 1">
                <van-image
                  :src="moment.images[0]"
                  class="image-single"
                  fit="cover"
                  radius="8"
                  @click.stop
                />
              </template>
              <template v-else-if="moment.images.length <= 3">
                <div
                  v-for="(img, idx) in moment.images.slice(0, 3)"
                  :key="idx"
                  class="image-item image-row-3"
                >
                  <van-image :src="img" fit="cover" radius="6" @click.stop />
                </div>
              </template>
              <template v-else>
                <div
                  v-for="(img, idx) in moment.images.slice(0, 9)"
                  :key="idx"
                  class="image-item image-grid-multi"
                >
                  <van-image :src="img" fit="cover" radius="4" @click.stop />
                </div>
              </template>
            </div>

            <!-- Actions -->
            <div class="moment-actions">
              <div class="action-item like-btn" :class="{ liked: moment.isLiked }" @click.stop="handleLike(moment)">
                <van-icon
                  :name="moment.isLiked ? 'like' : 'like-o'"
                  :color="moment.isLiked ? '#ee0a24' : '#8888aa'"
                  size="18"
                />
                <span class="action-count">{{ formatCount(moment.likeCount) }}</span>
              </div>
              <div class="action-item" @click.stop="goDetail(moment.id)">
                <van-icon name="chat-o" color="#8888aa" size="18" />
                <span class="action-count">{{ formatCount(moment.commentCount) }}</span>
              </div>
            </div>
          </div>
        </div>
      </van-list>
    </van-pull-refresh>

    <!-- Publish Button -->
    <div class="publish-btn" @click="goPublish">
      <van-button
        round
        type="primary"
        icon="plus"
        size="large"
        class="publish-button"
      >
        发布
      </van-button>
    </div>
  </div>
</template>

<style scoped>
.community-page {
  min-height: 100vh;
  min-height: 100dvh;
  padding-bottom: 120px;
}

/* Tabs styling */
.community-tabs :deep(.van-tabs__line) {
  background: linear-gradient(135deg, #7c5cff, #00c896);
  height: 3px;
  border-radius: 2px;
  width: 24px !important;
}

.community-tabs :deep(.van-tab--active .van-tab__text) {
  font-weight: 600;
}

/* Moment list */
.moment-list {
  padding: 12px 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.moment-card {
  padding: 16px;
  animation: fade-up 0.35s ease both;
}

.moment-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 12px;
}

.avatar-placeholder {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: var(--app-accent-bg);
  display: flex;
  align-items: center;
  justify-content: center;
}

.moment-user-info {
  flex: 1;
}

.moment-nickname {
  font-size: 14px;
  font-weight: 500;
}

.moment-nickname-row {
  display: flex;
  align-items: center;
  gap: 6px;
}

.vip-tag {
  padding: 0 6px;
  font-size: 10px;
  line-height: 16px;
  border: none;
}

.moment-time {
  font-size: 12px;
  color: var(--app-text-secondary);
  margin-top: 2px;
}

.moment-content {
  font-size: 14px;
  line-height: 1.6;
  margin-bottom: 12px;
  word-break: break-word;
}

/* Image grid */
.moment-images-grid {
  display: grid;
  gap: 4px;
  margin-bottom: 12px;
}

.image-single {
  width: 200px;
  max-width: 100%;
  aspect-ratio: 1;
  border-radius: 8px;
  overflow: hidden;
}

.image-row-3 {
  aspect-ratio: 1;
  border-radius: 6px;
  overflow: hidden;
}

:where(.image-item) {
  overflow: hidden;
}

:where(.image-item) :deep(.van-image) {
  width: 100%;
  height: 100%;
}

:where(.image-item) :deep(img) {
  transition: transform 0.3s ease;
}

:where(.image-item:active) :deep(img) {
  transform: scale(1.05);
}

/* 3 images row layout */
.moment-images-grid:has(.image-row-3:nth-child(2)) {
  grid-template-columns: repeat(3, 1fr);
}

.moment-images-grid:has(.image-row-3:first-child:nth-last-child(3)) {
  grid-template-columns: repeat(3, 1fr);
}

.moment-images-grid:has(.image-grid-multi) {
  grid-template-columns: repeat(3, 1fr);
}

.moment-actions {
  display: flex;
  gap: 24px;
  padding-top: 12px;
  border-top: 1px solid var(--app-border);
}

.action-item {
  display: flex;
  align-items: center;
  gap: 4px;
  cursor: pointer;
  transition: transform 0.2s ease;
}

.action-item:active {
  transform: scale(0.9);
}

.like-btn.liked .action-count {
  color: #ee0a24;
}

.action-count {
  font-size: 12px;
  color: var(--app-text-secondary);
  transition: color 0.2s;
}

.publish-btn {
  position: fixed;
  bottom: 80px;
  right: 20px;
  z-index: 100;
  animation: fade-up 0.4s ease 0.2s both;
}

.publish-button {
  width: auto;
  height: 44px;
  padding: 0 20px;
  box-shadow: 0 4px 12px rgba(124, 92, 255, 0.4);
  transition: box-shadow 0.3s, transform 0.2s;
}

.publish-button:active {
  transform: scale(0.95);
  box-shadow: 0 2px 8px rgba(124, 92, 255, 0.3);
}

/* Keyframe animation for card entrance */
@keyframes fade-up {
  from {
    opacity: 0;
    transform: translateY(16px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
