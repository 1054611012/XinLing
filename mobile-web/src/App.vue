<script setup lang="ts">
import { computed, ref, watch, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import TabBar from '@/components/TabBar.vue'
import { initDarkMode } from '@/hooks/useDarkMode'

onMounted(() => {
  initDarkMode()
})

const route = useRoute()
const router = useRouter()

const showTabBar = computed(() => {
  const tabPages = ['/', '/explore', '/community', '/profile']
  return tabPages.includes(route.path)
})

const transitionName = ref('slide-left')
const prevDepth = ref(0)

watch(() => route.path, (_to, _from) => {
  const toDepth = (route.meta?.depth as number) || 0
  transitionName.value = toDepth >= prevDepth.value ? 'slide-left' : 'slide-right'
  prevDepth.value = toDepth
})
</script>

<template>
  <div class="app-container">
    <router-view v-slot="{ Component }">
      <transition :name="transitionName" mode="out-in">
        <component :is="Component" />
      </transition>
    </router-view>
    <TabBar v-if="showTabBar" />
  </div>
</template>

<style scoped>
.app-container {
  min-height: 100vh;
  min-height: 100dvh;
  background-color: var(--app-bg-primary);
  color: var(--app-text-primary);
  padding-bottom: calc(50px + env(safe-area-inset-bottom, 0px));
}

/* Slide transitions — tide-inspired smoother curves */
.slide-left-enter-active,
.slide-left-leave-active,
.slide-right-enter-active,
.slide-right-leave-active {
  transition: transform 0.35s cubic-bezier(0.4, 0, 0.2, 1),
              opacity 0.35s cubic-bezier(0.4, 0, 0.2, 1);
}

.slide-left-enter-from {
  transform: translateX(24px) scale(0.98);
  opacity: 0;
}

.slide-left-leave-to {
  transform: translateX(-24px) scale(0.98);
  opacity: 0;
}

.slide-right-enter-from {
  transform: translateX(-24px) scale(0.98);
  opacity: 0;
}

.slide-right-leave-to {
  transform: translateX(24px) scale(0.98);
  opacity: 0;
}
</style>
