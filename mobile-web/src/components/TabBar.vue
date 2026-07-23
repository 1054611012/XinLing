<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

const active = computed(() => route.path)

function onTabChange(path: string) {
  router.push(path)
}
</script>

<template>
  <van-tabbar
    :value="active"
    @change="onTabChange"
    active-color="#666"
    inactive-color="#999"
    :border="false"
    safe-area-inset-bottom
    class="tide-tabbar"
  >
    <van-tabbar-item name="/" class="tab-item">
      <template #icon>
        <van-icon name="wap-home-o" :size="24" />
      </template>
      <span class="tab-label">首页</span>
    </van-tabbar-item>
    <van-tabbar-item name="/explore" class="tab-item">
      <template #icon>
        <van-icon name="compass-o" :size="24" />
      </template>
      <span class="tab-label">探索</span>
    </van-tabbar-item>
    <van-tabbar-item name="/profile" class="tab-item">
      <template #icon>
        <van-icon name="contact-o" :size="24" />
      </template>
      <span class="tab-label">我的</span>
    </van-tabbar-item>
  </van-tabbar>
</template>

<style scoped>
.tide-tabbar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  z-index: 100;
  background: rgba(255, 255, 255, 0.95) !important;
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-top: 1px solid #f0f0f0;
  box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.04);
}

:deep(.van-tabbar-item) {
  padding: 6px 0;
}

:deep(.van-tabbar-item__icon) {
  margin-bottom: 2px;
  font-size: 24px;
}

:deep(.van-tabbar-item--active) {
  color: #666;
}

.tab-label {
  font-size: 10px;
  font-weight: 400;
}
</style>