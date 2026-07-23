<script setup lang="ts">
import { ref } from 'vue'
import { ontologyReason, getOntologyKnowledge } from '@/api/ontology'

interface Message {
  id: number
  role: 'user' | 'assistant'
  content: string
  createTime: string
}

const messages = ref<Message[]>([
  {
    id: 0,
    role: 'assistant',
    content: '我是本体智能体，可以帮助你理解概念之间的关系和进行知识推理。请问有什么想了解的？',
    createTime: new Date().toISOString()
  }
])

const inputText = ref('')
const loading = ref(false)

async function sendMessage() {
  if (!inputText.value.trim() || loading.value) return

  const userMsg: Message = {
    id: Date.now(),
    role: 'user',
    content: inputText.value.trim(),
    createTime: new Date().toISOString()
  }
  messages.value.push(userMsg)
  inputText.value = ''
  loading.value = true

  try {
    const res = await ontologyReason(userMsg.content)
    const assistantMsg: Message = {
      id: Date.now() + 1,
      role: 'assistant',
      content: res.data || '抱歉，我无法回答这个问题。',
      createTime: new Date().toISOString()
    }
    messages.value.push(assistantMsg)
  } catch (error) {
    const errorMsg: Message = {
      id: Date.now() + 1,
      role: 'assistant',
      content: '抱歉，服务暂时不可用，请稍后再试。',
      createTime: new Date().toISOString()
    }
    messages.value.push(errorMsg)
  } finally {
    loading.value = false
  }
}

async function loadKnowledge() {
  try {
    const res = await getOntologyKnowledge()
    const knowledgeMsg: Message = {
      id: Date.now(),
      role: 'assistant',
      content: '当前本体知识库内容：\n\n' + (res.data || '知识库为空'),
      createTime: new Date().toISOString()
    }
    messages.value.push(knowledgeMsg)
  } catch (error) {
    console.error('加载知识库失败', error)
  }
}
</script>

<template>
  <div class="ontology-chat-page">
    <div class="header">
      <div class="header-content">
        <h1 class="title">本体智能体</h1>
        <p class="subtitle">基于本体知识库进行知识推理和问答</p>
      </div>
    </div>

    <div class="messages-container">
      <div v-for="msg in messages" :key="msg.id" :class="['message', msg.role]">
        <div class="message-content">
          <p>{{ msg.content }}</p>
        </div>
      </div>
      
      <div v-if="loading" class="loading-indicator">
        <div class="dot"></div>
        <div class="dot"></div>
        <div class="dot"></div>
      </div>
    </div>

    <div class="input-area">
      <textarea 
        v-model="inputText" 
        placeholder="输入你的问题，例如：RAG和知识库有什么关系？"
        @keydown.enter.exact.prevent="sendMessage"
      ></textarea>
      <div class="buttons">
        <button class="btn-secondary" @click="loadKnowledge">查看知识库</button>
        <button class="btn-primary" :disabled="!inputText.trim() || loading" @click="sendMessage">
          {{ loading ? '思考中...' : '发送' }}
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.ontology-chat-page {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background: linear-gradient(180deg, #1a1a2e 0%, #16213e 100%);
}

.header {
  padding: 60px 24px 20px;
  background: rgba(255, 255, 255, 0.05);
}

.header-content {
  text-align: center;
}

.title {
  font-size: 24px;
  font-weight: 600;
  color: rgba(255, 255, 255, 0.95);
  margin: 0 0 8px;
}

.subtitle {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.5);
  margin: 0;
}

.messages-container {
  flex: 1;
  overflow-y: auto;
  padding: 20px 24px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.message {
  max-width: 85%;
}

.message.user {
  align-self: flex-end;
}

.message.assistant {
  align-self: flex-start;
}

.message-content {
  background: rgba(255, 255, 255, 0.08);
  border-radius: 16px;
  padding: 14px 18px;
  backdrop-filter: blur(10px);
}

.message.user .message-content {
  background: rgba(99, 102, 241, 0.3);
}

.message-content p {
  margin: 0;
  font-size: 15px;
  line-height: 1.6;
  color: rgba(255, 255, 255, 0.9);
  white-space: pre-wrap;
}

.loading-indicator {
  display: flex;
  gap: 8px;
  padding: 14px 18px;
  background: rgba(255, 255, 255, 0.08);
  border-radius: 16px;
  align-self: flex-start;
}

.dot {
  width: 8px;
  height: 8px;
  background: rgba(255, 255, 255, 0.6);
  border-radius: 50%;
  animation: bounce 1.4s infinite ease-in-out both;
}

.dot:nth-child(1) { animation-delay: -0.32s; }
.dot:nth-child(2) { animation-delay: -0.16s; }

@keyframes bounce {
  0%, 80%, 100% { transform: scale(0); }
  40% { transform: scale(1); }
}

.input-area {
  padding: 16px 24px;
  padding-bottom: calc(16px + env(safe-area-inset-bottom));
  background: rgba(0, 0, 0, 0.2);
}

textarea {
  width: 100%;
  padding: 14px 18px;
  background: rgba(255, 255, 255, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  color: rgba(255, 255, 255, 0.9);
  font-size: 15px;
  line-height: 1.5;
  resize: none;
  box-sizing: border-box;
  min-height: 56px;
}

textarea::placeholder {
  color: rgba(255, 255, 255, 0.4);
}

textarea:focus {
  outline: none;
  border-color: rgba(99, 102, 241, 0.5);
}

.buttons {
  display: flex;
  gap: 12px;
  margin-top: 12px;
  justify-content: flex-end;
}

.btn-secondary, .btn-primary {
  padding: 10px 20px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-secondary {
  background: transparent;
  border: 1px solid rgba(255, 255, 255, 0.2);
  color: rgba(255, 255, 255, 0.7);
}

.btn-secondary:active {
  background: rgba(255, 255, 255, 0.1);
}

.btn-primary {
  background: linear-gradient(135deg, #6366f1, #8b5cf6);
  border: none;
  color: white;
}

.btn-primary:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.btn-primary:active:not(:disabled) {
  transform: scale(0.96);
}
</style>