<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showLoadingToast, closeToast } from 'vant'
import { getDailyTasks, claimTaskReward } from '@/api/growth'
import type { DailyTask } from '@/api/growth'

const router = useRouter()
const tasks = ref<DailyTask[]>([])
const loading = ref(false)

async function loadData() {
  loading.value = true
  const toast = showLoadingToast({ message: '加载中...', forbidClick: true, duration: 0 })
  try {
    const res = await getDailyTasks()
    tasks.value = res.data.records
  } catch {
    showToast('加载失败')
  } finally {
    closeToast()
    loading.value = false
  }
}

async function handleClaim(taskId: number) {
  try {
    const toast = showLoadingToast({ message: '领取中...', forbidClick: true, duration: 0 })
    await claimTaskReward(taskId)
    closeToast()
    showToast('奖励已领取')
    loadData()
  } catch {
    closeToast()
    showToast('领取失败')
  }
}

onMounted(() => { loadData() })
</script>

<template>
  <div class="page task-page">
    <van-nav-bar title="每日任务" left-arrow @click-left="router.back()" />

    <div class="task-list">
      <div
        v-for="task in tasks"
        :key="task.id"
        class="task-card glass-card"
      >
        <div class="task-left">
          <div class="task-icon">{{ task.icon || '📌' }}</div>
          <div class="task-info">
            <div class="task-name">{{ task.name }}</div>
            <div class="task-desc">{{ task.description }}</div>
            <div class="task-progress" v-if="task.maxProgress > 1">
              <div class="progress-bar">
                <div class="progress-fill" :style="{ width: (task.progress / task.maxProgress * 100) + '%' }" />
              </div>
              <span class="progress-text">{{ task.progress }}/{{ task.maxProgress }}</span>
            </div>
          </div>
        </div>
        <div class="task-right">
          <div class="task-reward">+{{ task.pointsReward }}分</div>
          <van-button
            v-if="task.status === 'completed'"
            size="small"
            round
            type="primary"
            @click="handleClaim(task.id)"
          >领取</van-button>
          <van-tag v-else-if="task.status === 'claimed'" color="#00c896">已领取</van-tag>
          <van-tag v-else plain color="#8888aa">进行中</van-tag>
        </div>
      </div>

      <van-empty v-if="!loading && tasks.length === 0" description="暂无任务" />
    </div>
  </div>
</template>

<style scoped>
.task-page {
  min-height: 100vh;
  min-height: 100dvh;
}

.task-list {
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.task-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px;
  gap: 12px;
}

.task-left {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  flex: 1;
  min-width: 0;
}

.task-icon {
  font-size: 28px;
  flex-shrink: 0;
}

.task-info {
  flex: 1;
  min-width: 0;
}

.task-name {
  font-size: 15px;
  font-weight: 600;
  margin-bottom: 2px;
}

.task-desc {
  font-size: 12px;
  color: var(--app-text-secondary);
}

.task-progress {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 8px;
}

.progress-bar {
  flex: 1;
  height: 4px;
  background: rgba(255,255,255,0.06);
  border-radius: 2px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #7c5cff, #00c896);
  border-radius: 2px;
  transition: width 0.3s;
}

.progress-text {
  font-size: 11px;
  color: var(--app-text-secondary);
  white-space: nowrap;
}

.task-right {
  text-align: right;
  flex-shrink: 0;
}

.task-reward {
  font-size: 12px;
  color: #f5af19;
  margin-bottom: 6px;
}
</style>
