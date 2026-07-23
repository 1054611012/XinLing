<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showConfirmDialog, showLoadingToast, closeToast } from 'vant'
import { getMallGoods, exchangeGoods } from '@/api/growth'
import type { MallGoods } from '@/api/growth'

const router = useRouter()
const goods = ref<MallGoods[]>([])
const loading = ref(false)

async function loadData() {
  loading.value = true
  const toast = showLoadingToast({ message: '加载中...', forbidClick: true, duration: 0 })
  try {
    const res = await getMallGoods()
    goods.value = res.data.records
  } catch {
    showToast('加载失败')
  } finally {
    closeToast()
    loading.value = false
  }
}

async function handleExchange(goodsId: number, name: string, price: number) {
  try {
    await showConfirmDialog({
      title: '确认兑换',
      message: `确定要花费 ${price} 积分兑换「${name}」吗？`,
      confirmButtonColor: '#7c5cff'
    })
    const toast = showLoadingToast({ message: '兑换中...', forbidClick: true, duration: 0 })
    await exchangeGoods(goodsId)
    closeToast()
    showToast('兑换成功')
    loadData()
  } catch {
    closeToast()
  }
}

onMounted(() => { loadData() })
</script>

<template>
  <div class="page mall-page">
    <van-nav-bar title="积分商城" left-arrow @click-left="router.back()" />

    <div class="goods-list">
      <div
        v-for="item in goods"
        :key="item.id"
        class="goods-card glass-card"
      >
        <div class="goods-cover">
          <van-image width="80" height="80" radius="8" :src="item.cover || ''">
            <template v-slot:error>{{ item.type === 'vip' ? '👑' : '🎁' }}</template>
          </van-image>
        </div>
        <div class="goods-info">
          <div class="goods-name">{{ item.name }}</div>
          <div class="goods-desc">{{ item.description }}</div>
          <div class="goods-bottom">
            <span class="goods-price">{{ item.price }} 积分</span>
            <van-button
              size="small"
              round
              type="primary"
              :disabled="item.stock <= 0"
              @click="handleExchange(item.id, item.name, item.price)"
            >
              {{ item.stock > 0 ? '兑换' : '已售罄' }}
            </van-button>
          </div>
        </div>
      </div>

      <van-empty v-if="!loading && goods.length === 0" description="暂无商品" />
    </div>
  </div>
</template>

<style scoped>
.mall-page {
  min-height: 100vh;
  min-height: 100dvh;
}

.goods-list {
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.goods-card {
  display: flex;
  gap: 14px;
  padding: 14px;
}

.goods-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.goods-name {
  font-size: 15px;
  font-weight: 600;
  margin-bottom: 4px;
}

.goods-desc {
  font-size: 12px;
  color: var(--app-text-secondary);
  margin-bottom: 8px;
  flex: 1;
}

.goods-bottom {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.goods-price {
  font-size: 14px;
  font-weight: 600;
  color: #f5af19;
}
</style>
