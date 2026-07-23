<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showLoadingToast, closeToast } from 'vant'
import { getActivityList, joinActivity, getCouponList, receiveCoupon } from '@/api/activity'

const router = useRouter()
const activeTab = ref('activity')
const activities = ref<any[]>([])
const coupons = ref<any[]>([])
const loading = ref(false)

async function loadData() {
  loading.value = true
  const toast = showLoadingToast({ message: '加载中...', forbidClick: true, duration: 0 })
  try {
    if (activeTab.value === 'activity') {
      const res = await getActivityList()
      activities.value = Array.isArray(res.data) ? res.data : []
    } else {
      const res = await getCouponList()
      coupons.value = Array.isArray(res.data) ? res.data : []
    }
  } catch {
    showToast('加载失败')
  } finally {
    closeToast()
    loading.value = false
  }
}

async function handleJoin(id: number) {
  try {
    await joinActivity(id)
    showToast('参与成功')
    loadData()
  } catch {
    showToast('操作失败')
  }
}

async function handleReceive(id: number) {
  try {
    await receiveCoupon(id)
    showToast('领取成功')
    loadData()
  } catch {
    showToast('领取失败')
  }
}

function getTypeLabel(type: string): string {
  const map: Record<string, string> = {
    discount: '限时折扣',
    buy_one_get_one: '买一送一',
    new_user: '新用户专享',
    distribution: '分销活动'
  }
  return map[type] || type
}

onMounted(() => { loadData() })
</script>

<template>
  <div class="page activity-page">
    <van-nav-bar title="活动中心" left-arrow @click-left="router.back()" />

    <van-tabs v-model:active="activeTab" @change="loadData">
      <van-tab title="热门活动" name="activity" />
      <van-tab title="优惠券" name="coupon" />
    </van-tabs>

    <!-- Activities -->
    <div class="activity-list" v-if="activeTab === 'activity'">
      <div v-for="item in activities" :key="item.id" class="activity-card glass-card">
        <div class="activity-cover">
          <van-image width="100%" height="140" radius="12" :src="item.cover || ''">
            <template v-slot:error>
              <div class="activity-type-badge">{{ getTypeLabel(item.type) }}</div>
            </template>
          </van-image>
        </div>
        <div class="activity-body">
          <div class="activity-title">{{ item.title }}</div>
          <div class="activity-desc">{{ item.description }}</div>
          <div class="activity-footer">
            <span class="activity-time">{{ item.startTime }} ~ {{ item.endTime }}</span>
            <van-button
              round
              type="primary"
              size="small"
              @click="handleJoin(item.id)"
            >参与</van-button>
          </div>
        </div>
      </div>
      <van-empty v-if="!loading && activities.length === 0" description="暂无活动" />
    </div>

    <!-- Coupons -->
    <div class="coupon-list" v-else>
      <div v-for="item in coupons" :key="item.id" class="coupon-card glass-card">
        <div class="coupon-left">
          <div class="coupon-value">{{ item.type === 'discount' ? item.value + '折' : '¥' + item.value }}</div>
          <div class="coupon-condition" v-if="item.conditionAmount">满{{ item.conditionAmount }}可用</div>
        </div>
        <div class="coupon-right">
          <div class="coupon-name">{{ item.name }}</div>
          <div class="coupon-expire">{{ item.endTime }} 过期</div>
          <van-button
            round
            size="small"
            type="primary"
            plain
            @click="handleReceive(item.id)"
          >领取</van-button>
        </div>
      </div>
      <van-empty v-if="!loading && coupons.length === 0" description="暂无优惠券" />
    </div>
  </div>
</template>

<style scoped>
.activity-page {
  min-height: 100vh;
  min-height: 100dvh;
}

.activity-list,
.coupon-list {
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.activity-card {
  overflow: hidden;
}

.activity-cover {
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(124, 92, 255, 0.1);
}

.activity-type-badge {
  padding: 12px 24px;
  background: linear-gradient(135deg, #7c5cff, #00c896);
  border-radius: 8px;
  color: white;
  font-size: 14px;
}

.activity-body {
  padding: 16px;
}

.activity-title {
  font-size: 18px;
  font-weight: 700;
  margin-bottom: 6px;
}

.activity-desc {
  font-size: 13px;
  color: var(--app-text-secondary);
  margin-bottom: 10px;
  line-height: 1.5;
}

.activity-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.activity-time {
  font-size: 11px;
  color: var(--app-text-secondary);
}

/* Coupon */
.coupon-card {
  display: flex;
  overflow: hidden;
  padding: 0;
}

.coupon-left {
  width: 100px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 16px 12px;
  background: linear-gradient(135deg, rgba(245, 175, 25, 0.2), rgba(245, 175, 25, 0.05));
  position: relative;
}

.coupon-left::after {
  content: '';
  position: absolute;
  right: -8px;
  top: 0;
  bottom: 0;
  width: 16px;
  background: radial-gradient(circle at 0 50%, transparent 6px, var(--app-bg-primary) 6px);
  background-size: 16px 16px;
  background-repeat: repeat-y;
}

.coupon-value {
  font-size: 24px;
  font-weight: 800;
  color: #f5af19;
}

.coupon-condition {
  font-size: 10px;
  color: var(--app-text-secondary);
  margin-top: 2px;
}

.coupon-right {
  flex: 1;
  padding: 14px 16px;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.coupon-name {
  font-size: 14px;
  font-weight: 600;
}

.coupon-expire {
  font-size: 11px;
  color: var(--app-text-secondary);
  margin-bottom: 4px;
}
</style>
