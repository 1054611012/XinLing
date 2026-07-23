<script setup lang="ts">
/**
 * ChatPage - AI 数字人聊天页面（v3 — Canvas 实时渲染）
 *
 * 核心升级：
 * 1. FrameBasedAvatar Canvas 60fps 实时渲染小狐灵
 * 2. 4种情绪：idle/listen/think/answer 各自独立驱动动画
 * 3. TTS 语音同步 + 口型实时开合
 *
 * @author SuXia
 * @date 2026/06/01
 */
import { ref, nextTick, onMounted, onUnmounted } from 'vue'
import { sendMessage, getChatHistory } from '@/api/chat'
import type { ChatMessage } from '@/types/api'
import FrameBasedAvatar from '@/components/FrameBasedAvatar.vue'

// 消息列表
const messages = ref<ChatMessage[]>([
  {
    id: 0,
    role: 'assistant',
    content: '嘿嘿～ 本狐灵驾到！🦊 叫我小灵就好～ 有什么好玩的事想聊聊吗？',
    createTime: new Date().toISOString()
  }
])

const inputText = ref('')
const isThinking = ref(false)
const loading = ref(false)
const showThinking = ref(false)

// 数字人状态
const mood = ref<'idle' | 'listen' | 'think' | 'answer'>('idle')
const speaking = ref(false)
const currentAudioUrl = ref<string | undefined>(undefined)
const lastAnswerText = ref('')

// 语音设置（可后续做成用户偏好）
const voiceSetting = ref('XIAOXIAO')

// ---- 情绪检测 ----
function detectMood(text: string): 'idle' | 'answer' {
  const happyWords = ['开心', '好', '棒', '🎉', '😊', '不错', '厉害', '恭喜', '🥰', '🌸', '谢谢', '哈哈']
  return happyWords.some(w => text.includes(w)) ? 'answer' : 'idle'
}

// ---- 加载历史 ----
async function loadHistory() {
  try {
    loading.value = true
    const res = await getChatHistory()
    if (res.data.messages?.length > 0) {
      messages.value = res.data.messages
      const last = [...res.data.messages].reverse().find((m: ChatMessage) => m.role === 'assistant')
      if (last) mood.value = detectMood(last.content)
    }
  } catch {
    // ignore
  } finally {
    loading.value = false
  }
}

// ---- 发送消息 ----
async function handleSend() {
  const content = inputText.value.trim()
  if (!content || isThinking.value) return

  // 添加用户消息
  messages.value.push({
    id: Date.now(),
    role: 'user',
    content,
    createTime: new Date().toISOString()
  })
  inputText.value = ''
  scrollDown()

  // 状态：听题
  mood.value = 'listen'

  setTimeout(() => {
    mood.value = 'think'
  }, 800)

  isThinking.value = true
  showThinking.value = true

  try {
    const res = await sendMessage({
      content,
      voice: voiceSetting.value
    })
    if (res.data?.reply) {
      const replyMsg = res.data.reply
      messages.value.push(replyMsg)

      // 如果有语音 URL，播放
      if (replyMsg.audioUrl) {
        currentAudioUrl.value = replyMsg.audioUrl
        speaking.value = true
        mood.value = 'answer'
      } else {
        // 无语音时自动检测情绪
        mood.value = detectMood(replyMsg.content)
      }
    } else {
      mood.value = detectMood(res.data?.reply?.content || '')
    }
  } catch {
    messages.value.push({
      id: Date.now() + 1,
      role: 'assistant',
      content: '呜哇～走神了！🥺 再说一次好不好～',
      createTime: new Date().toISOString()
    })
    mood.value = 'idle'
  } finally {
    isThinking.value = false
    showThinking.value = false
    scrollDown()
  }
}

// ---- 语音播放结束回调 ----
function onAudioEnd() {
  speaking.value = false
  currentAudioUrl.value = undefined
  mood.value = 'idle'
}

// ---- 滚到底部 ----
function scrollDown() {
  nextTick(() => {
    const chatList = document.querySelector('.cx')
    if (chatList) {
      chatList.scrollTo({ top: 9999, behavior: 'smooth' })
    }
  })
}

// ---- 生命周期 ----
onMounted(() => {
  loadHistory()
  setTimeout(() => {
    mood.value = 'idle'
  }, 600)
})

onUnmounted(() => {
  speaking.value = false
})
</script>

<template>
  <div class="page cp">
    <!-- 氛围背景 -->
    <div class="bg-hz" />
    <div class="bg-hz2" />

    <!-- 数字人角色（Canvas 60fps 实时渲染） -->
    <FrameBasedAvatar
      :mood="mood"
      :speaking="speaking"
      :audio-url="currentAudioUrl"
      @audio-end="onAudioEnd"
    />

    <!-- 消息列表 -->
    <div class="cx">
      <div
        v-for="(msg, i) in messages"
        :key="msg.id"
        class="r"
        :class="msg.role"
        :style="{ animationDelay: i * 0.04 + 's' }"
      >
        <div v-if="msg.role === 'assistant'" class="av">
          <div class="avf" />
        </div>
        <div class="b">
          {{ msg.content }}
        </div>
      </div>

      <!-- 思考指示器 -->
      <div v-if="showThinking" class="r assistant">
        <div class="av t-av"><div class="avf" /></div>
        <div class="b tb">
          <span class="d">.</span>
          <span class="d">.</span>
          <span class="d">.</span>
        </div>
      </div>
    </div>

    <!-- 输入框 -->
    <div class="ib">
      <van-field
        v-model="inputText"
        placeholder="跟小灵聊聊天吧～"
        :border="false"
        :disabled="isThinking"
        @keypress.enter="handleSend"
        autosize
        type="text"
        rows="1"
      />
      <van-button
        :disabled="!inputText.trim() || isThinking"
        type="primary"
        size="small"
        round
        class="sb"
        @click="handleSend"
      >
        发送
      </van-button>
    </div>
  </div>
</template>

<style scoped>
/* ===== 全局布局 ===== */
.cp {
  display: flex;
  flex-direction: column;
  height: 100vh;
  height: 100dvh;
  background: #f8f4ef;
  position: relative;
  overflow: hidden;
}

/* ===== 氛围光照（暖色调，极低透明度） ===== */
.bg-hz {
  position: fixed;
  top: -80px;
  left: 50%;
  transform: translateX(-50%);
  width: 300px;
  height: 300px;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(255, 160, 80, 0.07), transparent 70%);
  pointer-events: none;
}
.bg-hz2 {
  position: fixed;
  top: 25%;
  right: -50px;
  width: 200px;
  height: 200px;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(255, 120, 160, 0.05), transparent 70%);
  pointer-events: none;
}

/* ===== 消息列表 ===== */
.cx {
  flex: 1;
  overflow-y: auto;
  padding: 6px 16px 14px;
  display: flex;
  flex-direction: column;
  gap: 10px;
  z-index: 1;
}

.r {
  display: flex;
  gap: 7px;
  align-items: flex-end;
  max-width: 90%;
  animation: mi 0.3s ease both;
}

@keyframes mi {
  from {
    opacity: 0;
    transform: translateY(8px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.user {
  align-self: flex-end;
  flex-direction: row-reverse;
}

.assistant {
  align-self: flex-start;
}

.b {
  padding: 10px 14px;
  border-radius: 16px;
  font-size: 14px;
  line-height: 1.65;
  word-break: break-word;
}

.user .b {
  background: linear-gradient(135deg, #fce4d6, #f5d5c0);
  color: #3d2e20;
  border: none;
  border-bottom-right-radius: 4px;
}

.assistant .b {
  background: #ffffff;
  color: #3d2e20;
  border: none;
  border-bottom-left-radius: 4px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.04);
}

/* ===== 迷你头像 ===== */
.av {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  flex-shrink: 0;
  overflow: hidden;
  background: linear-gradient(135deg, #fce4d6, #f5d5c0);
}

.avf {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  background: linear-gradient(135deg, #fce4d6, #f5d5c0);
}

.t-av {
  animation: tb 0.5s ease-in-out infinite alternate;
}

@keyframes tb {
  from {
    transform: translateY(0);
  }
  to {
    transform: translateY(-3px);
  }
}

/* ===== 思考动画 ===== */
.tb {
  display: flex;
  gap: 3px;
  align-items: center;
  padding: 12px 16px;
}

.d {
  font-size: 20px;
  line-height: 0;
  animation: db 1.4s infinite;
  color: #b0a090;
}

.d:nth-child(2) {
  animation-delay: 0.2s;
}
.d:nth-child(3) {
  animation-delay: 0.4s;
}

@keyframes db {
  0%, 80%, 100% {
    opacity: 0;
  }
  40% {
    opacity: 1;
  }
}

/* ===== 输入框 ===== */
.ib {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 12px;
  padding-bottom: calc(10px + env(safe-area-inset-bottom));
  background: rgba(255, 255, 255, 0.8);
  border-top: none;
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  z-index: 1;
  flex-shrink: 0;
}

.ib :deep(.van-field) {
  background: #ede8e0;
  border-radius: 20px;
  padding: 6px 14px;
  flex: 1;
}

.ib :deep(.van-field__control) {
  color: #3d2e20;
}

.ib :deep(.van-field__placeholder) {
  color: #b0a090;
}

.sb {
  flex-shrink: 0;
  height: 36px;
  padding: 0 16px;
  background: linear-gradient(135deg, #f5a080, #f080a0);
  border: none;
}

.sb:deep(.van-button__text) {
  color: #fff;
}

.sb:disabled {
  opacity: 0.4;
}
</style>
