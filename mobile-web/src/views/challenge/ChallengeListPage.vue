<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showLoadingToast, closeToast } from 'vant'
import { getChallengeList, getMyChallenges, joinChallenge } from '@/api/social'

const router = useRouter()
const activeTab = ref('open')
const challenges = ref<any[]>([])
const loading = ref(false)

async function loadData() {
  loading.value = true
  const toast = showLoadingToast({ message: '加载中...', forbidClick: true, duration: 0 })
  try {
    const res = activeTab.value === 'open'
      ? await getChallengeList()
      : await getMyChallenges()
    challenges.value = res.data.records || []
  } catch {
    showToast('加载失败')
  } finally {
    closeToast()
    loading.value = false
  }
}

async function handleJoin(id: number) {
  try {
    const toast = showLoadingToast({ message: '加入中...', forbidClick: true, duration: 0 })
    await joinChallenge(id)
    closeToast()
    showToast('🎉 加入成功')
    loadData()
  } catch (e: any) {
    closeToast()
    showToast(e?.response?.data?.message || e?.message || '加入失败')
  }
}

watch(activeTab, () => loadData())
onMounted(() => loadData())
</script>

<template>
  <div class="page challenge-page">
    <van-nav-bar title="挑战活动" left-arrow @click-left="router.back()" />

    <van-tabs v-model:active="activeTab" class="app-tabs" :swipeable="false">
      <van-tab title="进行中" name="open" />
      <van-tab title="我的挑战" name="my" />
    </van-tabs>

    <div class="list-wrap">
      <div
        v-for="(item, idx) in challenges"
        :key="item.id"
        class="glass-card challenge-card"
        :style="{ animationDelay: `${idx * 0.08}s` }"
        @click="activeTab === 'my' && router.push(`/challenge/${item.challengeId || item.id}`)"
      >
        <div class="card-row">
          <div class="card-icon" :class="item.type === 'focus' ? 'icon-focus' : 'icon-sleep'">
            <span>{{ item.type === 'focus' ? '🎯' : '🌙' }}</span>
          </div>
          <div class="card-info">
            <div class="card-title">{{ item.title }}</div>
            <div class="card-desc">{{ item.description }}</div>
            <div class="card-tags">
              <van-tag round plain color="var(--app-accent)" style="margin-right:6px">{{ item.duration }}天</van-tag>
              <van-tag v-if="item.pointsReward" round plain color="#e6a23c" style="margin-right:6px">⭐{{ item.pointsReward }}积分</van-tag>
              <van-tag v-if="item.vipDaysReward" round plain color="#7c5cff">👑VIP{{ item.vipDaysReward }}天</van-tag>
            </div>
          </div>
        </div>

        <div class="card-footer">
          <template v-if="activeTab === 'open'">
            <span class="join-count" v-if="item.joinedCount">{{ item.joinedCount }}人参与</span>
            <van-button
              round
              type="primary"
              size="small"
              @click.stop="handleJoin(item.id)"
            >立即参与</van-button>
          </template>
          <template v-else>
            <div class="progress-row">
              <div class="progress-bar">
                <div class="progress-fill" :style="{ width: Math.min(100, (item.completedDays || 0) / (item.duration || 1) * 100) + '%' }" />
              </div>
              <span class="progress-text">
                <template v-if="item.status === 1">✅ 已完成</template>
                <template v-else>{{ item.currentDay || 0 }}/{{ item.duration }}天</template>
              </span>
            </div>
          </template>
        </div>
      </div>

      <van-empty v-if="!loading && challenges.length === 0" :description="activeTab === 'open' ? '暂无挑战活动' : '你还没有参与任何挑战'" />
    </div>
  </div>
</template>

<style scoped>
.challenge-page {
  min-height: 100vh;
  min-height: 100dvh;
}
.app-tabs :deep(.van-tabs__line) {
  background: linear-gradient(135deg, var(--app-accent), var(--app-success));
  height: 3px;
  border-radius: 2px;
  width: 24px !important;
}
.list-wrap {
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.challenge-card {
  padding: 16px;
  animation: fade-up 0.4s ease both;
}
.card-row {
  display: flex;
  gap: 14px;
}
.card-icon {
  width: 48px;
  height: 48px;
  border-radius: var(--app-radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
  flex-shrink: 0;
}
.icon-focus { background: rgba(124, 92, 255, 0.08); }
.icon-sleep { background: rgba(74, 66, 212, 0.08); }
.card-info { flex: 1; min-width: 0; }
.card-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 4px;
}
.card-desc {
  font-size: 12px;
  color: var(--app-text-secondary);
  margin-bottom: 10px;
  line-height: 1.5;
}
.card-tags { display: flex; flex-wrap: wrap; gap: 4px; }
.card-footer {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid var(--app-glass-border);
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.join-count {
  font-size: 12px;
  color: var(--app-text-secondary);
}
.progress-row {
  display: flex;
  align-items: center;
  gap: 10px;
  width: 100%;
}
.progress-bar {
  flex: 1;
  height: 6px;
  background: var(--app-input-bg);
  border-radius: 3px;
  overflow: hidden;
}
.progress-fill {
  height: 100%;
  border-radius: 3px;
  background: linear-gradient(90deg, var(--app-accent), var(--app-success));
  transition: width 0.5s ease;
}
.progress-text {
  font-size: 12px;
  color: var(--app-text-secondary);
  white-space: nowrap;
}

@keyframes fade-up {
  from { opacity: 0; transform: translateY(14px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>
