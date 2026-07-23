<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { showToast, showConfirmDialog, showLoadingToast, closeToast } from 'vant'
import { getOrderDetail, cancelOrder } from '@/api/order'
import type { PayOrder } from '@/types/api'

const router = useRouter()
const route = useRoute()
const orderNo = route.params.orderNo as string
const order = ref<PayOrder | null>(null)
const loading = ref(false)

async function loadData() {
  loading.value = true
  const toast = showLoadingToast({ message: '加载中...', forbidClick: true, duration: 0 })
  try {
    const res = await getOrderDetail(orderNo)
    order.value = res.data
  } catch {
    showToast('加载失败')
  } finally {
    closeToast()
    loading.value = false
  }
}

async function handleCancel() {
  try {
    await showConfirmDialog({
      title: '取消订单',
      message: '确定要取消此订单吗？',
      confirmButtonColor: '#ee0a24'
    })
    const toast = showLoadingToast({ message: '处理中...', forbidClick: true, duration: 0 })
    await cancelOrder(orderNo)
    closeToast()
    showToast('订单已取消')
    loadData()
  } catch {
    closeToast()
  }
}

function getStatusLabel(status: number): string {
  const map: Record<number, string> = {
    0: '待支付',
    1: '支付成功',
    2: '已取消',
    3: '已退款',
    4: '已过期'
  }
  return map[status] || '未知'
}

function getStatusColor(status: number): string {
  const map: Record<number, string> = {
    0: '#f5af19',
    1: '#00c896',
    2: '#8888aa',
    3: '#ee0a24',
    4: '#8888aa'
  }
  return map[status] || '#8888aa'
}

function getStatusIcon(status: number): string {
  const map: Record<number, string> = {
    0: '⏳',
    1: '✅',
    2: '❌',
    3: '↩️',
    4: '⏰'
  }
  return map[status] || '❓'
}

onMounted(() => { loadData() })
</script>

<template>
  <div class="page order-detail-page">
    <van-nav-bar title="订单详情" left-arrow @click-left="router.back()" />

    <div v-if="order" class="detail-content">
      <!-- Status Banner -->
      <div class="status-banner glass-card" :style="{ borderColor: getStatusColor(order.orderStatus) }">
        <div class="status-icon">{{ getStatusIcon(order.orderStatus) }}</div>
        <div class="status-text">
          <div class="status-title" :style="{ color: getStatusColor(order.orderStatus) }">
            {{ getStatusLabel(order.orderStatus) }}
          </div>
          <div class="status-desc">{{ order.packageName }}</div>
        </div>
      </div>

      <!-- Order Info -->
      <div class="info-section glass-card">
        <div class="info-row">
          <span class="info-label">订单编号</span>
          <span class="info-value">{{ order.orderNo }}</span>
        </div>
        <div class="info-row">
          <span class="info-label">商品名称</span>
          <span class="info-value">{{ order.packageName }}</span>
        </div>
        <div class="info-row">
          <span class="info-label">订单金额</span>
          <span class="info-value">¥{{ order.amount }}</span>
        </div>
        <div class="info-row" v-if="order.payAmount && order.payAmount !== order.amount">
          <span class="info-label">实付金额</span>
          <span class="info-value accent">¥{{ order.payAmount }}</span>
        </div>
        <div class="info-row">
          <span class="info-label">支付方式</span>
          <span class="info-value">{{ order.payType || '--' }}</span>
        </div>
        <div class="info-row">
          <span class="info-label">创建时间</span>
          <span class="info-value">{{ order.createTime }}</span>
        </div>
        <div class="info-row" v-if="order.payTime">
          <span class="info-label">支付时间</span>
          <span class="info-value">{{ order.payTime }}</span>
        </div>
      </div>

      <!-- Actions -->
      <div class="action-section" v-if="order.orderStatus === 0">
        <van-button
          round
          block
          type="primary"
          class="action-btn pay-btn"
        >
          立即支付 ¥{{ order.payAmount || order.amount }}
        </van-button>
        <van-button
          round
          block
          plain
          class="action-btn"
          @click="handleCancel"
        >
          取消订单
        </van-button>
      </div>
    </div>

    <van-loading v-else-if="loading" class="loading-state" vertical>加载中...</van-loading>
    <van-empty v-else description="订单不存在" />
  </div>
</template>

<style scoped>
.order-detail-page {
  min-height: 100vh;
  min-height: 100dvh;
}

.detail-content {
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.status-banner {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 24px 20px;
  border-left: 3px solid;
}

.status-icon {
  font-size: 40px;
}

.status-title {
  font-size: 20px;
  font-weight: 700;
  margin-bottom: 4px;
}

.status-desc {
  font-size: 14px;
  color: var(--app-text-secondary);
}

.info-section {
  padding: 20px;
}

.info-row {
  display: flex;
  justify-content: space-between;
  padding: 10px 0;
  border-bottom: 1px solid rgba(255, 255, 255, 0.04);
}

.info-row:last-child {
  border-bottom: none;
}

.info-label {
  font-size: 14px;
  color: var(--app-text-secondary);
}

.info-value {
  font-size: 14px;
  font-weight: 500;
  text-align: right;
}

.info-value.accent {
  color: var(--app-accent-light);
  font-weight: 700;
}

.action-section {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-top: 8px;
}

.action-btn {
  height: 48px;
  font-size: 16px;
}

.pay-btn {
  border: none;
  background: linear-gradient(135deg, #7c5cff, #00c896);
}

.loading-state {
  display: flex;
  justify-content: center;
  padding: 80px 0;
}
</style>
