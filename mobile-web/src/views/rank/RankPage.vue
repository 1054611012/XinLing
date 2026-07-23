<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showLoadingToast, closeToast } from 'vant'
import { getRankList } from '@/api/social'
import type { RankItem } from '@/api/social'

const router = useRouter()
const activeTab = ref('focus')
const periods = ['today', 'week', 'month']
const activePeriod = ref('week')
const ranks = ref<RankItem[]>([])
const loading = ref(false)

async function loadData() {
  loading.value = true
  const toast = showLoadingToast({ message: '加载中...', forbidClick: true, duration: 0 })
  try {
    const res = await getRankList(activeTab.value, activePeriod.value)
    ranks.value = res.data.records
  } catch {
    showToast('加载失败')
  } finally {
    closeToast()
    loading.value = false
  }
}

function getMedal(index: number): string {
  if (index === 0) return '🥇'
  if (index === 1) return '🥈'
  if (index === 2) return '🥉'
  return ''
}

onMounted(() => { loadData() })
</script>

<template>
  <div class="page rank-page">
    <van-nav-bar title="排行榜" left-arrow @click-left="router.back()" />

    <van-tabs v-model:active="activeTab" @change="loadData">
      <van-tab title="专注排行" name="focus" />
      <van-tab title="睡眠排行" name="sleep" />
      <van-tab title="成就排行" name="achievement" />
    </van-tabs>

    <div class="period-tabs">
      <span
        v-for="p in periods"
        :key="p"
        class="period-tab"
        :class="{ active: activePeriod === p }"
        @click="activePeriod = p; loadData()"
      >
        {{ p === 'today' ? '今日' : p === 'week' ? '本周' : '本月' }}
      </span>
    </div>

    <div class="rank-list">
      <div
        v-for="(item, index) in ranks"
        :key="item.userId"
        class="rank-item glass-card"
      >
        <div class="rank-number">{{ getMedal(index) || `#${index + 1}` }}</div>
        <van-image round width="40" height="40" :src="item.avatar || ''" />
        <div class="rank-info">
          <div class="rank-name">{{ item.nickname }}</div>
          <div class="rank-value">{{ item.value }}</div>
        </div>
      </div>

      <van-empty v-if="!loading && ranks.length === 0" description="暂无排行数据" />
    </div>
  </div>
</template>

<style scoped>
.rank-page {
  min-height: 100vh;
  min-height: 100dvh;
}

.period-tabs {
  display: flex;
  justify-content: center;
  gap: 8px;
  padding: 12px 16px;
}

.period-tab {
  padding: 6px 20px;
  border-radius: 16px;
  font-size: 13px;
  color: var(--app-text-secondary);
  background: rgba(255,255,255,0.04);
  cursor: pointer;
  transition: all 0.3s;
}

.period-tab.active {
  background: rgba(124, 92, 255, 0.2);
  color: #7c5cff;
}

.rank-list {
  padding: 0 16px 16px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.rank-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
}

.rank-number {
  width: 32px;
  text-align: center;
  font-size: 14px;
  font-weight: 600;
}

.rank-info {
  flex: 1;
}

.rank-name {
  font-size: 14px;
  font-weight: 500;
}

.rank-value {
  font-size: 12px;
  color: var(--app-text-secondary);
  margin-top: 2px;
}
</style>
