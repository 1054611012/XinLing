<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showLoadingToast, closeToast } from 'vant'
import { getOrderList } from '@/api/order'
import type { PayOrder } from '@/types/api'

const router = useRouter()
const tabs = [
  { key: '', label: '全部' },
  { key: '0', label: '待支付' },
  { key: '1', label: '已支付' },
  { key: '2', label: '已取消' },
  { key: '3', label: '已退款' }
]
const activeTab = ref('')
const orders = ref<PayOrder[]>([])
const loading = ref(false)
const finished = ref(false)
const page = ref(1)

async function loadOrders(append = false) {
  loading.value = true
  const toast = showLoadingToast({ message: '加载中...', forbidClick: true, duration: 0 })
  try {
    const status = activeTab.value || undefined
    const res = await getOrderList(page.value, 20)
    const result = res.data as any
    const records = Array.isArray(result) ? result : (result.list || [])
    // client-side filter by status
    let filtered = records
    if (status !== undefined) {
      filtered = records.filter((o: PayOrder) => String(o.orderStatus) === status)
    }
    if (append) {
      orders.value = [...orders.value, ...filtered]
    } else {
      orders.value = filtered
    }
    finished.value = orders.value.length >= (result.total || orders.value.length)
  } catch {
    showToast('加载失败')
  } finally {
    closeToast()
    loading.value = false
  }
}

function onTabChange() {
  page.value = 1
  loadOrders()
}

function goDetail(orderNo: string) {
  router.push(`/order/${orderNo}`)
}

function getStatusLabel(status: number): string {
  const map: Record<number, string> = {
    0: '待支付',
    1: '已支付',
    2: '已取消',
    3: '已退款'
  }
  return map[status] || '未知'
}

function getStatusColor(status: number): string {
  const map: Record<number, string> = {
    0: '#f5af19',
    1: '#00c896',
    2: '#8888aa',
    3: '#ee0a24'
  }
  return map[status] || '#8888aa'
}

onMounted(() => { loadOrders() })
</script>

<template>
  <div class="page order-list-page">
    <van-nav-bar title="我的订单" left-arrow @click-left="router.back()" />

    <van-tabs v-model:active="activeTab" @change="onTabChange">
      <van-tab v-for="tab in tabs" :key="tab.key" :title="tab.label" :name="tab.key" />
    </van-tabs>

    <div class="order-list">
      <div
        v-for="order in orders"
        :key="order.orderNo"
        class="order-card glass-card"
        @click="goDetail(order.orderNo)"
      >
        <div class="order-header">
          <span class="order-no">订单号: {{ order.orderNo }}</span>
          <span class="order-status" :style="{ color: getStatusColor(order.orderStatus) }">
            {{ getStatusLabel(order.orderStatus) }}
          </span>
        </div>
        <div class="order-body">
          <div class="order-icon">👑</div>
          <div class="order-info">
            <div class="order-name">{{ order.packageName }}</div>
            <div class="order-time">{{ order.createTime }}</div>
          </div>
          <div class="order-amount">¥{{ order.payAmount || order.amount }}</div>
        </div>
      </div>

      <van-empty v-if="!loading && orders.length === 0" description="暂无订单" />

      <div class="load-more" v-if="!finished && orders.length > 0">
        <van-loading v-if="loading" size="20">加载中...</van-loading>
        <van-button v-else plain size="small" @click="page++; loadOrders(true)">加载更多</van-button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.order-list-page {
  min-height: 100vh;
  min-height: 100dvh;
}

.order-list {
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.order-card {
  padding: 16px;
  cursor: pointer;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  padding-bottom: 12px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
}

.order-no {
  font-size: 12px;
  color: var(--app-text-secondary);
}

.order-status {
  font-size: 13px;
  font-weight: 500;
}

.order-body {
  display: flex;
  align-items: center;
  gap: 12px;
}

.order-icon {
  font-size: 28px;
}

.order-info {
  flex: 1;
  min-width: 0;
}

.order-name {
  font-size: 15px;
  font-weight: 500;
  margin-bottom: 2px;
}

.order-time {
  font-size: 12px;
  color: var(--app-text-secondary);
}

.order-amount {
  font-size: 18px;
  font-weight: 700;
  color: var(--app-accent-light);
}

.load-more {
  text-align: center;
  padding: 12px;
}
</style>
