<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showLoadingToast, closeToast } from 'vant'
import { getVipPackages, getVipInfo } from '@/api/vip'
import { createOrder, getPayUrl, queryOrderStatus } from '@/api/order'
import { useAuthStore } from '@/stores/auth'
import type { VipPackage, VipInfo } from '@/types/api'
import vipIcon from '@/assets/vip.png'

const router = useRouter()
const authStore = useAuthStore()
const packages = ref<VipPackage[]>([])
const vipInfo = ref<VipInfo | null>(null)
const loading = ref(false)
const purchasingId = ref<number | null>(null)

async function loadData() {
  loading.value = true
  try {
    const [pkgRes, infoRes] = await Promise.all([
      getVipPackages(),
      getVipInfo()
    ])
    packages.value = pkgRes.data
    vipInfo.value = infoRes.data
  } catch {
    showToast('加载失败')
  } finally {
    loading.value = false
  }
}

async function handleBuy(id: number) {
  if (purchasingId.value !== null) return
  purchasingId.value = id

  showLoadingToast({
    message: '创建订单...',
    forbidClick: true,
    duration: 0
  })

  try {
    // Step 1: 创建订单
    const orderRes = await createOrder({ productId: id, payType: 'wxpay' })
    const orderNo = orderRes.data.orderNo

    closeToast()
    showLoadingToast({
      message: '正在跳转支付...',
      forbidClick: true,
      duration: 0
    })

    // Step 2: 获取支付 URL
    const payRes = await getPayUrl(orderNo, 'wxpay')
    const payUrl = payRes.data.payUrl

    closeToast()
    if (payUrl) {
      window.location.href = payUrl
    }

    // Step 3: 轮询支付状态
    startPolling(orderNo)
  } catch {
    closeToast()
    showToast('创建失败')
    purchasingId.value = null
  }
}

function startPolling(orderNo: string) {
  const maxAttempts = 60        // 最多轮询 60 次
  const intervalMs = 3000       // 每 3 秒轮询一次
  let attempts = 0

  const timer = setInterval(async () => {
    attempts++

    try {
      const res = await queryOrderStatus(orderNo)
      const status = res.data.orderStatus  // 0-待支付 1-已支付 2-已取消 3-已退款

      if (status === 1) {
        clearInterval(timer)
        // 支付成功：刷新用户信息和 VIP 数据
        await Promise.all([
          authStore.fetchUserInfo(),
          loadData()
        ])
        showToast({
          message: 'VIP 开通成功！',
          type: 'success',
          duration: 2000
        })
        purchasingId.value = null
      } else if (status === 2 || status === 3) {
        // 已取消或已退款
        clearInterval(timer)
        showToast('订单已取消')
        purchasingId.value = null
      }
    } catch {
      // ignore single poll error
    }

    if (attempts >= maxAttempts) {
      clearInterval(timer)
      showToast('支付超时，请前往订单查看')
      purchasingId.value = null
    }
  }, intervalMs)
}

onMounted(() => {
  loadData()
})
</script>

<template>
  <div class="page vip-page">
    <!-- VIP Status -->
    <div class="vip-header glass-card">
      <div class="vip-header-icon">
        <img :src="vipIcon" alt="VIP" class="vip-header-img" />
      </div>
      <div class="vip-header-text">
        <div class="vip-header-title" v-if="vipInfo && vipInfo.vipStatus > 0">
          尊贵的VIP会员
        </div>
        <div class="vip-header-title" v-else>
          成为VIP会员
        </div>
        <div class="vip-header-desc" v-if="vipInfo && vipInfo.remainingDays > 0">
          会员剩余 {{ vipInfo.remainingDays }} 天
        </div>
        <div class="vip-header-desc" v-else>
          解锁全部高级功能
        </div>
      </div>
    </div>

    <!-- Packages -->
    <div class="package-list">
      <div
        v-for="pkg in packages"
        :key="pkg.id"
        class="package-card glass-card"
        :class="{ 'is-popular': pkg.type === 'year' }"
      >
        <div class="package-left">
          <div class="package-icon">
            <template v-if="pkg.type === 'month'">
              <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="#7c5cff" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><polyline points="20 12 20 22 4 22 4 12"/><rect x="2" y="7" width="20" height="5"/><line x1="12" y1="22" x2="12" y2="7"/><path d="M12 7H7.5a2.5 2.5 0 0 1 0-5C11 2 12 7 12 7z"/><path d="M12 7h4.5a2.5 2.5 0 0 0 0-5C13 2 12 7 12 7z"/></svg>
            </template>
            <template v-else-if="pkg.type === 'quarter'">
              <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="#7c5cff" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="8" r="6"/><path d="M15.477 12.89L17 22l-5-3-5 3 1.523-9.11"/></svg>
            </template>
            <template v-else-if="pkg.type === 'year'">
              <img :src="vipIcon" alt="VIP" class="vip-pkg-img" />
            </template>
            <template v-else>
              <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="#7c5cff" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M12 2c-5 0-9 4-9 9v10l3-3 3 3 3-3 3 3 3-3 3 3V11c0-5-4-9-9-9z"/></svg>
            </template>
          </div>
          <div class="package-info">
            <div class="package-name">
              {{ pkg.name }}
              <van-tag v-if="pkg.type === 'year'" color="#f5af19" style="margin-left: 6px;">热门</van-tag>
            </div>
            <div class="package-desc">{{ pkg.description }}</div>
          </div>
        </div>
        <div class="package-price">
          <div class="price-current">&yen;{{ pkg.price }}</div>
          <div class="price-original" v-if="pkg.originalPrice > pkg.price">&yen;{{ pkg.originalPrice }}</div>
        </div>
        <van-button
          size="small"
          round
          type="primary"
          class="buy-btn"
          :disabled="purchasingId !== null"
          @click.stop="handleBuy(pkg.id)"
        >
          {{ purchasingId === pkg.id ? '处理中' : '立即开通' }}
        </van-button>
      </div>
    </div>

    <!-- Features -->
    <div class="features-section">
      <div class="features-title">会员权益</div>
      <div class="feature-list">
        <div class="feature-item">
          <AppIcon name="success" color="#00c896" size="18" />
          <span>无限次专注模式</span>
        </div>
        <div class="feature-item">
          <AppIcon name="success" color="#00c896" size="18" />
          <span>高级睡眠分析报告</span>
        </div>
        <div class="feature-item">
          <AppIcon name="success" color="#00c896" size="18" />
          <span>全部助眠音频解锁</span>
        </div>
        <div class="feature-item">
          <AppIcon name="success" color="#00c896" size="18" />
          <span>AI 助手深度对话</span>
        </div>
        <div class="feature-item">
          <AppIcon name="success" color="#00c896" size="18" />
          <span>去广告纯净体验</span>
        </div>
        <div class="feature-item">
          <AppIcon name="success" color="#00c896" size="18" />
          <span>云数据同步备份</span>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.vip-page {
  padding: 16px;
}

.vip-header {
  text-align: center;
  padding: 32px 20px;
  margin-bottom: 20px;
}

.vip-header-icon {
  font-size: 48px;
  margin-bottom: 12px;
  display: flex;
  justify-content: center;
}

.vip-header-img {
  width: 48px;
  height: 48px;
  object-fit: contain;
}

.vip-header-title {
  font-size: 22px;
  font-weight: 700;
  margin-bottom: 8px;
}

.vip-header-desc {
  font-size: 14px;
  color: var(--app-text-secondary);
}

.package-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 24px;
}

.package-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px;
  gap: 12px;
  cursor: pointer;
  transition: all 0.3s;
  border: 1px solid transparent;
}

.package-card.is-popular {
  border-color: #f5af19;
  background: rgba(245, 175, 25, 0.08);
}

.package-left {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
}

.package-icon {
  font-size: 28px;
  display: flex;
  align-items: center;
}

.vip-pkg-img {
  width: 28px;
  height: 28px;
  object-fit: contain;
}

.package-name {
  font-size: 16px;
  font-weight: 600;
}

.package-desc {
  font-size: 12px;
  color: var(--app-text-secondary);
  margin-top: 2px;
}

.package-price {
  text-align: right;
}

.price-current {
  font-size: 20px;
  font-weight: 700;
  color: var(--app-accent-light);
}

.price-original {
  font-size: 12px;
  color: var(--app-text-secondary);
  text-decoration: line-through;
  margin-top: 2px;
}

.buy-btn {
  flex-shrink: 0;
}

.features-section {
  padding: 0 4px;
}

.features-title {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 16px;
}

.feature-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 14px;
}
</style>
