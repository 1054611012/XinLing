<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import AppIcon from './AppIcon.vue'

const route = useRoute()
const router = useRouter()

const tabs = [
  { name: '/', label: '首页', icon: 'wap-home-o' },
  { name: '/explore', label: '探索', icon: 'compass-o' },
  { name: '/community', label: '社区', icon: 'chat-o' },
  { name: '/profile', label: '我的', icon: 'contact-o' },
]

// 深色沉浸页（首页视频背景、睡眠模块等）使用暗色玻璃菜单栏，与 SleepTabBar 统一视觉语言
const darkRoutes = ['/', '/sleep']
const isDark = computed(() => darkRoutes.some((r) => route.path === r || route.path.startsWith(r + '/')))

const active = computed(() => route.path)

function go(name: string) {
  if (route.path === name) return
  router.push(name)
}

function iconColor(t: (typeof tabs)[number]) {
  const on = active.value === t.name
  if (isDark.value) return on ? '#ECEAFF' : 'rgba(255,255,255,0.5)'
  return on ? '#4338CA' : '#9aa0ab'
}
</script>

<template>
  <nav class="tide-tabbar" :class="{ dark: isDark }">
    <div
      v-for="t in tabs"
      :key="t.name"
      class="tab-item"
      :class="{ active: active === t.name }"
      @click="go(t.name)"
    >
      <AppIcon :name="t.icon" :size="23" :color="iconColor(t)" />
      <span class="tab-label">{{ t.label }}</span>
    </div>
  </nav>
</template>

<style scoped>
.tide-tabbar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  z-index: 100;
  display: flex;
  height: 56px;
  padding-bottom: env(safe-area-inset-bottom, 0px);
  box-sizing: content-box;
}

/* ===== 浅色态（探索 / 社区 / 我的） ===== */
.tide-tabbar:not(.dark) {
  background: rgba(255, 255, 255, 0.94);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-top: 1px solid rgba(0, 0, 0, 0.05);
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.05);
}

.tide-tabbar:not(.dark) .tab-label {
  color: #9aa0ab;
}

.tide-tabbar:not(.dark) .tab-item.active .tab-label {
  color: #4338ca;
  font-weight: 500;
}

/* ===== 深色态（首页 / 睡眠，对齐 home_design 的 .tabbar） ===== */
.tide-tabbar.dark {
  background: rgba(15, 23, 42, 0.55);
  backdrop-filter: blur(18px);
  -webkit-backdrop-filter: blur(18px);
  border-top: 0.5px solid rgba(255, 255, 255, 0.10);
}
/* 底部梦境紫辉光（设计稿 ::before 渐变） */
.tide-tabbar.dark::before {
  content: '';
  position: absolute;
  inset: 0;
  background: linear-gradient(to top, rgba(124, 58, 237, 0.16), transparent 80%);
  pointer-events: none;
}

.tide-tabbar.dark .tab-label {
  color: rgba(255, 255, 255, 0.5);
}

.tide-tabbar.dark .tab-item.active .tab-label {
  color: #eceaff;
  font-weight: 500;
}

.tide-tabbar.dark .tab-item.active .app-icon {
  filter: drop-shadow(0 0 8px rgba(167, 139, 250, 0.85));
}
.tide-tabbar.dark .tab-item.active .app-icon :deep(svg) {
  stroke: #c4b5fd;
}

.tab-item {
  flex: 1;
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 3px;
  cursor: pointer;
  transition: transform 0.18s ease, opacity 0.18s ease;
  user-select: none;
}

.tab-item:active {
  transform: scale(0.9);
  opacity: 0.8;
}

/* 激活态顶部光条（对齐 home_design 设计稿 .tab-item.active::before） */
.tide-tabbar.dark .tab-item.active::before {
  content: '';
  position: absolute;
  top: -4px;
  left: 50%;
  transform: translateX(-50%);
  width: 20px;
  height: 3px;
  border-radius: 2px;
  background: linear-gradient(90deg, #A78BFA, #C4B5FD);
  box-shadow: 0 0 8px rgba(167,139,250,.6);
}

.tab-label {
  font-size: 11px;
  line-height: 1;
  letter-spacing: 0.5px;
  transition: color 0.2s ease;
}
</style>
