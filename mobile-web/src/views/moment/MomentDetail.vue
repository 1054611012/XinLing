<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { showToast } from 'vant'
import { getMomentDetail, likeMoment, unlikeMoment, commentMoment } from '@/api/community'
import type { MomentItem, MomentComment } from '@/types/api'
import { DEFAULT_AVATAR } from '@/utils/constants'

const route = useRoute()
const router = useRouter()

const moment = ref<MomentItem | null>(null)
const comments = ref<MomentComment[]>([])
const loading = ref(false)
const commentText = ref('')

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
  const d = new Date(time)
  return `${d.getMonth() + 1}月${d.getDate()}日`
}

async function loadDetail() {
  const id = Number(route.params.id)
  if (!id) {
    showToast('参数错误')
    router.back()
    return
  }
  loading.value = true
  try {
    const res = await getMomentDetail(id, { headers: { _loading: false } })
    const data = res.data as any
    moment.value = data.moment || data
    comments.value = data.comments || []
  } catch {
    showToast('加载失败')
  } finally {
    loading.value = false
  }
}

async function handleLike() {
  if (!moment.value) return
  try {
    if (moment.value.isLiked) {
      await unlikeMoment(moment.value.id)
      moment.value.isLiked = false
      moment.value.likeCount = Math.max(0, moment.value.likeCount - 1)
    } else {
      await likeMoment(moment.value.id)
      moment.value.isLiked = true
      moment.value.likeCount = (moment.value.likeCount || 0) + 1
    }
  } catch {
    showToast('操作失败')
  }
}

async function handleComment() {
  const content = commentText.value.trim()
  if (!content || !moment.value) return
  try {
    await commentMoment(moment.value.id, content)
    comments.value.push({
      id: Date.now(),
      momentId: moment.value.id,
      userId: 0,
      nickname: '',
      avatar: '',
      content,
      createTime: new Date().toISOString()
    } as MomentComment)
    moment.value.commentCount++
    commentText.value = ''
    showToast('评论成功')
  } catch {
    showToast('评论失败')
  }
}

onMounted(() => {
  loadDetail()
})
</script>

<template>
  <div class="page moment-detail-page">
    <!-- Nav -->
    <van-nav-bar
      title="动态详情"
      left-arrow
      @click-left="router.back()"
    />

    <!-- Loading -->
    <div v-if="loading" class="loading-center">
      <van-loading size="24" color="#7c5cff">加载中...</van-loading>
    </div>

    <template v-if="moment && !loading">
      <!-- Moment Content -->
      <div class="moment-content-section fade-in">
        <div class="moment-user">
          <van-image
            round
            width="40"
            height="40"
            :src="moment.avatar || DEFAULT_AVATAR"
          >
            <template v-if="!moment.avatar" #error>
              <div class="avatar-placeholder">
                <van-icon name="contact" size="20" :color="'var(--app-text-secondary)'" />
              </div>
            </template>
          </van-image>
          <div class="moment-user-info">
            <div class="moment-nickname-row">
              <span class="moment-nickname">{{ moment.nickname || '匿名用户' }}</span>
              <van-tag v-if="moment.vipStatus > 0" round size="mini" color="#f5af19" class="vip-tag">VIP</van-tag>
            </div>
            <div class="moment-time">{{ formatRelativeTime(moment.createTime) }}</div>
          </div>
        </div>

        <div class="moment-text">{{ moment.content }}</div>

        <!-- Images -->
        <div
          v-if="moment.images && moment.images.length > 0"
          class="detail-images"
        >
          <div
            v-for="(img, idx) in moment.images.slice(0, 9)"
            :key="idx"
            class="detail-image-item"
            :class="{
              'image-wide': moment.images.length === 1,
              'image-narrow': moment.images.length > 1
            }"
          >
            <van-image :src="img" fit="cover" radius="8" @click.stop />
          </div>
        </div>

        <div class="moment-stats">
          <div class="stat-item" :class="{ liked: moment.isLiked }" @click="handleLike">
            <van-icon
              :name="moment.isLiked ? 'like' : 'like-o'"
              :color="moment.isLiked ? '#ee0a24' : '#8888aa'"
              size="20"
            />
            <span>{{ moment.likeCount }}</span>
          </div>
          <div class="stat-item">
            <van-icon name="chat-o" :color="'var(--app-text-secondary)'" size="20" />
            <span>{{ moment.commentCount }}</span>
          </div>
        </div>
      </div>

      <!-- Comments -->
      <div class="comments-section fade-in" style="animation-delay: 0.15s">
        <div class="comments-title">评论 ({{ comments.length }})</div>
        <div v-if="comments.length === 0" class="no-comments">
          <van-icon name="chat-o" size="40" :color="'var(--app-border)'" />
          <span>暂无评论，来说点什么吧</span>
        </div>
        <div
          v-for="comment in comments"
          :key="comment.id"
          class="comment-item fade-in"
          :style="{ animationDelay: '0.2s' }"
        >
          <van-image
            round
            width="28"
            height="28"
            :src="comment.avatar || DEFAULT_AVATAR"
          >
            <template v-if="!comment.avatar" #error>
              <div class="comment-avatar-placeholder">
                <van-icon name="contact" size="14" :color="'var(--app-text-secondary)'" />
              </div>
            </template>
          </van-image>
          <div class="comment-body">
            <div class="comment-nickname-row">
              <span class="comment-nickname">{{ comment.nickname || '匿名用户' }}</span>
              <van-tag v-if="comment.vipStatus > 0" round size="mini" color="#f5af19" class="vip-tag">VIP</van-tag>
            </div>
            <div class="comment-text">{{ comment.content }}</div>
            <div class="comment-time">{{ formatRelativeTime(comment.createTime) }}</div>
          </div>
        </div>
      </div>
    </template>

    <!-- Bottom Input -->
    <div class="bottom-input">
      <van-field
        v-model="commentText"
        placeholder="写下你的评论..."
        :border="false"
        type="text"
        rows="1"
        autosize
      />
      <van-button
        :disabled="!commentText.trim()"
        type="primary"
        size="small"
        round
        @click="handleComment"
      >
        发送
      </van-button>
    </div>
  </div>
</template>

<style scoped>
.moment-detail-page {
  min-height: 100vh;
  min-height: 100dvh;
  padding-bottom: 60px;
}

.loading-center {
  display: flex;
  justify-content: center;
  padding: 60px 0;
}

/* Fade in animation */
.fade-in {
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

/* Moment content */
.moment-content-section {
  padding: 16px;
}

.moment-user {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.avatar-placeholder {
  width: 40px;
  height: 40px;
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
  font-size: 15px;
  font-weight: 500;
  color: var(--app-text-primary);
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

.moment-text {
  font-size: 15px;
  line-height: 1.7;
  margin-bottom: 16px;
  word-break: break-word;
  color: var(--app-text-primary);
}

/* Detail images */
.detail-images {
  display: grid;
  gap: 4px;
  margin-bottom: 16px;
}

.image-wide {
  max-width: 100%;
  aspect-ratio: 16/9;
  border-radius: 8px;
  overflow: hidden;
}

.image-narrow {
  aspect-ratio: 1;
  border-radius: 8px;
  overflow: hidden;
}

/* Grid layout for multi images */
.detail-images:has(.image-narrow:nth-child(2)) {
  grid-template-columns: repeat(2, 1fr);
}

.detail-images:has(.image-narrow:first-child:nth-last-child(3),
                  .image-narrow:first-child:nth-last-child(3) ~ .image-narrow) {
  grid-template-columns: repeat(3, 1fr);
}

.detail-images:has(.image-narrow:nth-child(4),
                  .image-narrow:nth-child(5),
                  .image-narrow:nth-child(6)) {
  grid-template-columns: repeat(3, 1fr);
}

.detail-images:has(.image-narrow:nth-child(7),
                  .image-narrow:nth-child(8),
                  .image-narrow:nth-child(9)) {
  grid-template-columns: repeat(3, 1fr);
}

.detail-image-item {
  overflow: hidden;
}

.detail-image-item :deep(.van-image) {
  width: 100%;
  height: 100%;
}

.detail-image-item :deep(img) {
  transition: transform 0.3s ease;
}

.detail-image-item:active :deep(img) {
  transform: scale(1.05);
}

.moment-stats {
  display: flex;
  gap: 24px;
  padding: 16px 0;
  border-top: 1px solid var(--app-border);
  margin-top: 16px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: var(--app-text-secondary);
  cursor: pointer;
  transition: transform 0.2s ease;
}

.stat-item:active {
  transform: scale(0.9);
}

.stat-item.liked span {
  color: #ee0a24;
}

/* Comments */
.comments-section {
  padding: 0 16px 80px;
}

.comments-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid rgba(46, 36, 51, 0.04);
  color: var(--app-text-primary);
}

.no-comments {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 40px 0;
  color: var(--app-text-secondary);
  font-size: 14px;
}

.no-comments :deep(.van-icon) {
  color: rgba(46, 36, 51, 0.04) !important;
}

.comment-item {
  display: flex;
  gap: 10px;
  margin-bottom: 16px;
}

.comment-avatar-placeholder {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: var(--app-accent-bg);
  display: flex;
  align-items: center;
  justify-content: center;
}

.comment-body {
  flex: 1;
  min-width: 0;
}

.comment-nickname {
  font-size: 13px;
  font-weight: 500;
  margin-bottom: 4px;
  color: var(--app-accent-light);
}

.comment-nickname-row {
  display: flex;
  align-items: center;
  gap: 6px;
}

.comment-text {
  font-size: 14px;
  line-height: 1.5;
  margin-bottom: 4px;
  word-break: break-word;
}

.comment-time {
  font-size: 11px;
  color: var(--app-text-secondary);
}

/* Bottom input */
.bottom-input {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  padding-bottom: calc(8px + env(safe-area-inset-bottom));
  background: var(--app-glass-bg);
  backdrop-filter: blur(24px);
  -webkit-backdrop-filter: blur(24px);
  border-top: 1px solid var(--app-glass-border);
}

.bottom-input :deep(.van-field) {
  background: var(--app-input-bg);
  border-radius: 20px;
  padding: 6px 12px;
  flex: 1;
}
</style>
