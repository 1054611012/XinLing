<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showLoadingToast, closeToast } from 'vant'
import { getAchievementList } from '@/api/growth'
import type { Achievement } from '@/api/growth'

const router = useRouter()
const achievements = ref<Achievement[]>([])
const loading = ref(false)

async function loadData() {
  loading.value = true
  const toast = showLoadingToast({ message: '加载中...', forbidClick: true, duration: 0 })
  try {
    const res = await getAchievementList()
    achievements.value = res.data.records
  } catch {
    showToast('加载失败')
  } finally {
    closeToast()
    loading.value = false
  }
}

onMounted(() => { loadData() })
</script>

<template>
  <div class="page achievement-page">
    <van-nav-bar title="成就列表" left-arrow @click-left="router.back()" />

    <div class="achievement-grid">
      <div
        v-for="item in achievements"
        :key="item.id"
        class="achievement-card glass-card"
        :class="{ unlocked: item.isUnlocked }"
      >
        <div class="achievement-icon" :class="{ locked: !item.isUnlocked }">
          {{ item.isUnlocked ? item.icon : '🔒' }}
        </div>
        <div class="achievement-name">{{ item.name }}</div>
        <div class="achievement-desc">{{ item.description }}</div>
      </div>

      <van-empty v-if="!loading && achievements.length === 0" description="暂无成就" />
    </div>
  </div>
</template>

<style scoped>
.achievement-page {
  min-height: 100vh;
  min-height: 100dvh;
}

.achievement-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
  padding: 16px;
}

.achievement-card {
  padding: 20px 16px;
  text-align: center;
}

.achievement-card.unlocked {
  border-color: rgba(124, 92, 255, 0.3);
}

.achievement-icon {
  font-size: 36px;
  margin-bottom: 10px;
}

.achievement-icon.locked {
  filter: grayscale(1);
  opacity: 0.4;
}

.achievement-name {
  font-size: 14px;
  font-weight: 600;
  margin-bottom: 4px;
}

.achievement-desc {
  font-size: 11px;
  color: var(--app-text-secondary);
  line-height: 1.4;
}
</style>
