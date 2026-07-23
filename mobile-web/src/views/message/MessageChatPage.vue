<script setup lang="ts">
import { ref, onMounted, nextTick } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { showToast, showLoadingToast, closeToast } from 'vant'
import { sendMessage } from '@/api/social'
import type { PrivateMessage } from '@/api/social'

const router = useRouter()
const route = useRoute()
const userId = Number(route.params.userId)
const messages = ref<PrivateMessage[]>([])
const inputText = ref('')
const loading = ref(false)

function scrollToBottom() {
  nextTick(() => {
    const el = document.querySelector('.chat-messages')
    if (el) el.scrollTop = el.scrollHeight
  })
}

async function handleSend() {
  const content = inputText.value.trim()
  if (!content) return

  messages.value.push({
    id: Date.now(),
    fromUserId: 0,
    toUserId: userId,
    content,
    isRead: true,
    createTime: new Date().toISOString()
  })
  inputText.value = ''
  scrollToBottom()

  try {
    await sendMessage(userId, content)
  } catch {
    showToast('发送失败')
  }
}

onMounted(() => {
  scrollToBottom()
})
</script>

<template>
  <div class="message-chat-page">
    <van-nav-bar title="聊天" left-arrow @click-left="router.back()" />

    <div class="chat-messages" ref="messagesRef">
      <div
        v-for="msg in messages"
        :key="msg.id"
        class="message-row"
        :class="{ 'is-self': msg.fromUserId === 0 }"
      >
        <div class="msg-bubble">{{ msg.content }}</div>
      </div>

      <div v-if="messages.length === 0" class="empty-chat">
        <van-empty description="暂无消息，发送一条消息开始聊天" />
      </div>
    </div>

    <div class="chat-input-bar">
      <van-field
        v-model="inputText"
        placeholder="输入消息..."
        :border="false"
        @keypress.enter="handleSend"
        autosize
        type="text"
        rows="1"
      />
      <van-button
        :disabled="!inputText.trim()"
        type="primary"
        size="small"
        round
        class="send-btn"
        @click="handleSend"
      >
        发送
      </van-button>
    </div>
  </div>
</template>

<style scoped>
.message-chat-page {
  display: flex;
  flex-direction: column;
  height: 100vh;
  height: 100dvh;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.message-row {
  display: flex;
  max-width: 80%;
}

.message-row.is-self {
  align-self: flex-end;
}

.message-row:not(.is-self) {
  align-self: flex-start;
}

.msg-bubble {
  padding: 10px 14px;
  border-radius: 14px;
  font-size: 14px;
  line-height: 1.5;
  word-break: break-word;
}

.is-self .msg-bubble {
  background: linear-gradient(135deg, #7c5cff, #4a42d4);
  color: white;
  border-bottom-right-radius: 4px;
}

:not(.is-self) .msg-bubble {
  background: var(--app-bg-card);
  color: var(--app-text-primary);
  border-bottom-left-radius: 4px;
}

.empty-chat {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}

.chat-input-bar {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  background: var(--app-bg-secondary);
  border-top: 1px solid rgba(255, 255, 255, 0.06);
}

.chat-input-bar :deep(.van-field) {
  background: rgba(255, 255, 255, 0.06);
  border-radius: 20px;
  padding: 6px 12px;
  flex: 1;
}

.send-btn {
  flex-shrink: 0;
  height: 36px;
  padding: 0 16px;
}
</style>
