<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

const tabs = [
  {
    key: 'home',
    path: '/sleep',
    label: '首页',
    icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M3 11l9-8 9 8"/><path d="M5 10v10h14V10"/></svg>'
  },
  {
    key: 'breathe',
    path: '/sleep/breathe',
    label: '呼吸',
    icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="9"/><circle cx="12" cy="12" r="3.5" fill="currentColor" stroke="none"/></svg>'
  },
  {
    key: 'mix',
    path: '/sleep/mix',
    label: '混音',
    icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M4 18V8m5 10V5m5 13v-7m5 7V9"/></svg>'
  },
  {
    key: 'report',
    path: '/sleep/report',
    label: '报告',
    icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="4" y="4" width="16" height="16" rx="2"/><path d="M8 14l3-3 2 2 3-4"/></svg>'
  }
]

const active = computed(() => {
  const p = route.path
  if (p === '/sleep' || p === '/sleep/') return 'home'
  if (p.startsWith('/sleep/breathe')) return 'breathe'
  if (p.startsWith('/sleep/mix')) return 'mix'
  if (p.startsWith('/sleep/report')) return 'report'
  return ''
})

function go(t: (typeof tabs)[number]) {
  if (active.value !== t.key) router.push(t.path)
}
</script>

<template>
  <nav class="sleep-tabbar">
    <button
      v-for="t in tabs"
      :key="t.key"
      class="tab"
      :class="{ active: active === t.key }"
      @click="go(t)"
    >
      <span class="ic" v-html="t.icon" />
      <span class="tl">{{ t.label }}</span>
    </button>
  </nav>
</template>

<style scoped>
.sleep-tabbar {
  --purple: #7c3aed;
  --indigo2: #6366f1;
  --glass: rgba(255, 255, 255, 0.08);
  --glass-stroke: rgba(255, 255, 255, 0.12);
  --sub: #94a3b8;

  position: fixed;
  left: 18px;
  right: 18px;
  bottom: calc(env(safe-area-inset-bottom, 0px) + 14px);
  z-index: 30;
  display: flex;
  justify-content: space-around;
  align-items: center;
  padding: 11px 8px;
  border-radius: 30px;
  background: var(--glass);
  border: 1px solid var(--glass-stroke);
  backdrop-filter: blur(22px);
  -webkit-backdrop-filter: blur(22px);
  box-shadow: 0 14px 36px rgba(0, 0, 0, 0.4);
}

.tab {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  min-width: 48px;
  background: none;
  border: none;
  color: var(--sub);
  font-size: 10px;
  letter-spacing: 0.5px;
  cursor: pointer;
  -webkit-tap-highlight-color: transparent;
}

.tab .ic {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 22px;
  height: 22px;
  color: var(--sub);
  transition: transform 0.22s ease;
}
.tab .ic :deep(svg) {
  width: 21px;
  height: 21px;
}

.tab.active {
  color: #fff;
}
.tab.active .ic {
  width: 36px;
  height: 36px;
  margin-top: -22px;
  padding: 7px;
  border-radius: 14px;
  color: #fff;
  background: linear-gradient(135deg, var(--purple), var(--indigo2));
  box-shadow: 0 8px 20px rgba(124, 58, 237, 0.5);
}
.tab.active .ic :deep(svg) {
  width: 22px;
  height: 22px;
}
</style>
