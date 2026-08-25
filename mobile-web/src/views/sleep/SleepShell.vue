<script setup lang="ts">
import { useRouter } from 'vue-router'
import SleepTabBar from './SleepTabBar.vue'

withDefaults(defineProps<{ variant?: 'home' | 'breathe' | 'faint' }>(), {
  variant: 'home'
})

const router = useRouter()

function back() {
  router.back()
}
</script>

<template>
  <div class="sleep-shell" :class="'v-' + variant">
    <!-- 背景：home/breathe 用真实极光生图；faint 用微弱径向渐变 -->
    <div class="bg-aurora" v-if="variant !== 'faint'" />
    <div class="bg-faint" v-else />

    <!-- 压暗遮罩，保证文字可读 -->
    <div class="scrim" :class="'scrim-' + variant" />

    <!-- 顶栏：返回 -->
    <div class="topbar">
      <button class="back" @click="back" aria-label="返回">
        <svg viewBox="0 0 24 24" fill="none" stroke="#fff" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <path d="M15 18l-6-6 6-6" />
        </svg>
      </button>
    </div>

    <div class="content">
      <slot />
    </div>

    <SleepTabBar />
  </div>
</template>

<style scoped>
.sleep-shell {
  position: relative;
  min-height: 100vh;
  min-height: 100dvh;
  overflow: hidden;
  --indigo: #4338ca;
  --indigo2: #6366f1;
  --purple: #7c3aed;
  --lilac: #a78bfa;
  --card: #192134;
  --sub: #94a3b8;
  --glass: rgba(255, 255, 255, 0.08);
  --glass-stroke: rgba(255, 255, 255, 0.12);
  --font-title: 'Lora', 'Songti SC', 'Noto Serif SC', serif;
  --font-body: 'Raleway', -apple-system, 'PingFang SC', 'Microsoft YaHei', sans-serif;
}

/* 极光生图背景（首页 / 呼吸页） */
.bg-aurora {
  position: absolute;
  inset: 0;
  z-index: 0;
  background: url('/aurora.png') center / cover no-repeat;
}
/* 混音 / 报告 微弱极光 */
.bg-faint {
  position: absolute;
  inset: 0;
  z-index: 0;
  background-color: #0f172a;
  background-image:
    radial-gradient(440px 440px at 82% -8%, rgba(124, 58, 237, 0.2), transparent 60%),
    radial-gradient(380px 380px at -12% 28%, rgba(99, 102, 241, 0.16), transparent 60%),
    radial-gradient(520px 520px at 50% 122%, rgba(67, 56, 202, 0.18), transparent 60%);
}

/* 遮罩 */
.scrim {
  position: absolute;
  inset: 0;
  z-index: 1;
}
.scrim-home {
  background: linear-gradient(
    180deg,
    rgba(15, 23, 42, 0.18) 0%,
    rgba(15, 23, 42, 0.35) 42%,
    rgba(15, 23, 42, 0.92) 78%,
    rgba(15, 23, 42, 0.99) 100%
  );
}
.scrim-breathe {
  background: radial-gradient(
    120% 80% at 50% 38%,
    rgba(15, 23, 42, 0.3),
    rgba(15, 23, 42, 0.78) 70%,
    rgba(15, 23, 42, 0.96) 100%
  );
}
.scrim-faint {
  background: linear-gradient(180deg, rgba(15, 23, 42, 0) 62%, rgba(15, 23, 42, 0.45) 100%);
}

.topbar {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  z-index: 20;
  display: flex;
  align-items: center;
  gap: 8px;
  padding: calc(env(safe-area-inset-top, 0px) + 14px) 18px 6px;
}
.back {
  width: 38px;
  height: 38px;
  border-radius: 50%;
  background: var(--glass);
  border: 1px solid var(--glass-stroke);
  backdrop-filter: blur(18px);
  -webkit-backdrop-filter: blur(18px);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  flex: 0 0 auto;
  -webkit-tap-highlight-color: transparent;
}
.back:active {
  transform: scale(0.94);
}
.back svg {
  width: 22px;
  height: 22px;
}

.content {
  position: relative;
  z-index: 10;
  padding: calc(env(safe-area-inset-top, 0px) + 64px) 22px 100px;
  box-sizing: border-box;
}
</style>
