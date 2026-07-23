<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showLoadingToast, closeToast } from 'vant'
import { publishMoment } from '@/api/community'

const router = useRouter()

const content = ref('')
const isAnonymous = ref(false)
const visibility = ref(0) // 0-公开 1-私密
const submitting = ref(false)

function goBack() {
  router.back()
}

async function handleSubmit() {
  const text = content.value.trim()
  if (!text) {
    showToast('请输入动态内容')
    return
  }

  submitting.value = true
  const toast = showLoadingToast({
    message: '发布中...',
    forbidClick: true,
    duration: 0
  })

  try {
    // 先上传图片（如果有）
    const imageUrls: string[] = []

    await publishMoment({
      content: text,
      images: imageUrls,
      tags: [],
      isAnonymous: isAnonymous.value ? 1 : 0,
      visibility: visibility.value
    })

    closeToast()
    showToast('发布成功')
    // 返回上一页并刷新（通过 replace 触发）
    router.replace('/community')
  } catch (e: any) {
    closeToast()
    showToast(e?.message || '发布失败，请稍后重试')
  } finally {
    submitting.value = false
  }
}
</script>

<template>
  <div class="page moment-create-page">
    <!-- Nav -->
    <van-nav-bar
      title="发布动态"
      left-arrow
      @click-left="goBack"
    >
      <template #right>
        <van-button
          :disabled="!content.trim() || submitting"
          type="primary"
          size="small"
          round
          @click="handleSubmit"
        >
          发布
        </van-button>
      </template>
    </van-nav-bar>

    <!-- Content -->
    <div class="create-content">
      <van-field
        v-model="content"
        type="textarea"
        placeholder="此刻在想什么？分享你的感受..."
        :rows="6"
        maxlength="2000"
        show-word-limit
        :border="false"
        class="content-field"
        autosize
      />
    </div>

    <!-- Options -->
    <div class="create-options">
      <div class="option-item">
        <span class="option-label">
          <van-icon name="eye-o" class="option-icon" />
          匿名发布
        </span>
        <van-switch
          v-model="isAnonymous"
          size="22"
          active-color="#7c5cff"
          inactive-color="rgba(46,36,51,0.1)"
        />
      </div>

      <div class="option-item">
        <span class="option-label">
          <van-icon name="lock-o" class="option-icon" />
          可见范围
        </span>
        <van-radio-group v-model="visibility" direction="horizontal" class="visibility-group">
          <van-radio :name="0" icon-size="14" checked-color="#7c5cff">公开</van-radio>
          <van-radio :name="1" icon-size="14" checked-color="#7c5cff">私密</van-radio>
        </van-radio-group>
      </div>
    </div>
  </div>
</template>

<style scoped>
.moment-create-page {
  min-height: 100vh;
  min-height: 100dvh;
  background: var(--app-bg-primary);
}

.create-content {
  padding: 16px;
}

.content-field {
  background: var(--app-glass-bg);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid var(--app-glass-border);
  border-radius: var(--app-radius-md);
  padding: 12px 16px;
  font-size: 15px;
  line-height: 1.6;
  box-shadow: var(--app-glass-shadow);
}

.content-field :deep(.van-field__control::placeholder) {
  color: var(--app-text-tertiary);
}

.content-field :deep(.van-field__control) {
  color: var(--app-text-primary);
}

.content-field :deep(.van-field__word-limit) {
  color: var(--app-text-secondary);
  font-size: 12px;
  margin-top: 8px;
}

.create-options {
  margin: 16px;
  background: var(--app-glass-bg);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-radius: var(--app-radius-md);
  border: 1px solid var(--app-glass-border);
  box-shadow: var(--app-glass-shadow);
  overflow: hidden;
}

.option-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 16px;
}

.option-item + .option-item {
  border-top: 1px solid var(--app-border);
}

.option-label {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: var(--app-text-primary);
}

.option-icon {
  font-size: 16px;
  color: var(--app-accent);
}

.visibility-group {
  display: flex;
  gap: 12px;
}

.visibility-group :deep(.van-radio__label) {
  color: var(--app-text-primary);
  font-size: 13px;
}

.visibility-group :deep(.van-radio--checked .van-radio__label) {
  color: var(--app-accent);
}
</style>
