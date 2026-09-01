<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()

// 背景场景：左右滑切换（上下滑是看内容）。按设计稿分 3 套色调（极光 / 深空 / 暮光）
const scenes = [
  {
    label: '极光',
    pos: 'center 18%',
    filter: 'blur(0px) hue-rotate(0deg) saturate(1.05)',
    glowA: 'rgba(67,56,202,.34)',
    glowB: 'rgba(124,58,237,.30)',
    overlay: 'linear-gradient(to bottom, rgba(67,56,202,.18) 0%, rgba(20,12,40,.06) 30%, rgba(0,0,0,.08) 60%, rgba(124,58,237,.24) 100%)',
  },
  {
    label: '深空',
    pos: 'center 38%',
    filter: 'blur(0px) hue-rotate(-30deg) saturate(1.32) brightness(.82)',
    glowA: 'rgba(59,130,246,.30)',
    glowB: 'rgba(99,102,241,.26)',
    overlay: 'linear-gradient(to bottom, rgba(30,58,138,.22) 0%, rgba(10,15,40,.10) 30%, rgba(0,0,0,.14) 60%, rgba(67,56,202,.26) 100%)',
  },
  {
    label: '暮光',
    pos: 'center 40%',
    filter: 'blur(0px) hue-rotate(24deg) saturate(1.14) brightness(1.06)',
    glowA: 'rgba(217,70,239,.26)',
    glowB: 'rgba(251,146,60,.20)',
    overlay: 'linear-gradient(to bottom, rgba(190,24,93,.16) 0%, rgba(40,12,40,.06) 30%, rgba(0,0,0,.08) 60%, rgba(251,146,60,.18) 100%)',
  },
]
const sceneIdx = ref(0)
const currentScene = computed(() => scenes[sceneIdx.value])
const glowStyle = computed(() => ({
  background: `radial-gradient(58% 48% at 18% 14%, ${currentScene.value.glowA}, transparent 70%), radial-gradient(52% 44% at 86% 82%, ${currentScene.value.glowB}, transparent 72%)`,
}))

// 切换场景时浮现的指示器 + 圆点（设计稿：平时隐藏，仅切换时短暂出现）
const sceneFlash = ref(false)
const dotsShow = ref(false)
const sceneFlashName = ref('')
const isPinned = ref(false)
let sceneFlashTimer: ReturnType<typeof setTimeout> | null = null
function flashScene() {
  sceneFlashName.value = scenes[sceneIdx.value]?.label || ''
  sceneFlash.value = true
  dotsShow.value = true
  if (sceneFlashTimer) clearTimeout(sceneFlashTimer)
  sceneFlashTimer = setTimeout(() => { sceneFlash.value = false; dotsShow.value = false }, 1600)
}
function setScene(i: number) {
  const ni = (i + scenes.length) % scenes.length
  if (ni === sceneIdx.value) return
  sceneIdx.value = ni
  flashScene()
}

const scrollContainer = ref<HTMLElement>()
const bgEl = ref<HTMLElement>()
const stickyHeader = ref<HTMLElement>()
const entriesSection = ref<HTMLElement>()
const heroSection = ref<HTMLElement>()
const homeEl = ref<HTMLElement>()

// 仅首屏空旷区可切换场景（滚到功能入口以下即锁定）
function canSwitch() {
  if (!scrollContainer.value || !heroSection.value) return true
  const bottom = heroSection.value.offsetTop + heroSection.value.offsetHeight
  return scrollContainer.value.scrollTop < bottom - 80
}

// 左右滑切场景：横向位移明显大于纵向才切换，避开分类横向滚动区
let touchX = 0
let touchY = 0
let touchTarget: EventTarget | null = null
let isSwitching = false
function onTouchStart(e: TouchEvent) {
  touchX = e.touches[0].clientX
  touchY = e.touches[0].clientY
  touchTarget = e.target
}
function onTouchEnd(e: TouchEvent) {
  if (isSwitching || !touchTarget || !canSwitch()) return
  // 在横向滚动子元素（分类）内不切场景
  if ((touchTarget as HTMLElement).closest?.('.category-items')) return

  const dx = e.changedTouches[0].clientX - touchX
  const dy = e.changedTouches[0].clientY - touchY
  if (Math.abs(dx) > Math.abs(dy) * 1.2 && Math.abs(dx) > 45) {
    isSwitching = true
    setScene(sceneIdx.value + (dx < 0 ? 1 : -1))
    setTimeout(() => { isSwitching = false }, 350)
  }
}

// 滚动：极光平滑淡出（不加 transform 视差，避免 fixed+JS transform 合成层抖动）+ 吸顶头部
// 用 requestAnimationFrame 批量写入 DOM，避免每个 scroll 事件都直接回流产生抖动
let scrollTicking = false
function onScroll() {
  if (scrollTicking) return
  scrollTicking = true
  requestAnimationFrame(runScroll)
}
function runScroll() {
  scrollTicking = false
  if (!scrollContainer.value) return
  const y = scrollContainer.value.scrollTop

  // 极光淡出：从顶部就开始线性衰减，0~520px 内完全淡出到 0，
  // 不再加 transform 平移，避免 fixed 元素被 JS 改 transform 造成的合成层抖动 / 撕裂
  if (bgEl.value) {
    const op = Math.max(0, Math.min(1, 1 - y / 520))
    bgEl.value.style.opacity = String(op)
  }

  // 吸顶头部：带迟滞阈值，避免在临界点反复切换导致头部抖动
  if (stickyHeader.value && entriesSection.value) {
    const headerH = stickyHeader.value.offsetHeight
    const pinThreshold = entriesSection.value.offsetTop - headerH - 60
    if (y >= pinThreshold) {
      if (!isPinned.value) isPinned.value = true
    } else if (y < pinThreshold - 40) {
      if (isPinned.value) isPinned.value = false
    }
  }
}

const clock = ref('')
function updateClock() {
  const d = new Date()
  clock.value = `${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`
}
let clockTimer: ReturnType<typeof setInterval> | null = null

function greet() {
  const h = new Date().getHours()
  if (h >= 5 && h < 9) return '早安'
  if (h >= 9 && h < 12) return '上午好'
  if (h >= 12 && h < 14) return '中午好'
  if (h >= 14 && h < 18) return '下午好'
  if (h >= 18 && h < 22) return '晚上好'
  return '夜深了'
}

const weekDays = ['日', '一', '二', '三', '四', '五', '六']
const todayIndex = new Date().getDay()

const monthAbbr = ['JAN', 'FEB', 'MAR', 'APR', 'MAY', 'JUN', 'JUL', 'AUG', 'SEP', 'OCT', 'NOV', 'DEC'][new Date().getMonth()]
const quote = {
  text: '到头来，有意义的并不是结果，而是我们度过的那些无可替代的时间。',
  author: '星野道夫',
  date: new Date().getDate(),
  month: monthAbbr,
}

// 入口图标采用设计稿内联 SVG 路径，4 个入口各自配色（focus 靛 / sleep 紫 / nap 琥珀 / breath 青）
const entries = [
  { label: '专注', route: '/focus', fn: 'focus', svg: '<circle cx="12" cy="12" r="8"/><circle cx="12" cy="12" r="3"/>' },
  { label: '睡眠', route: '/sleep', fn: 'sleep', svg: '<path d="M20 14.5A8 8 0 1 1 9.5 4 6.5 6.5 0 0 0 20 14.5z"/>' },
  { label: '小憩', route: '/explore', fn: 'nap', svg: '<path d="M12 3v2M12 19v2M5 12H3M21 12h-2M6 6l1.5 1.5M16.5 16.5 18 18M18 6l-1.5 1.5M7.5 16.5 6 18"/><circle cx="12" cy="12" r="3.5"/>' },
  { label: '呼吸', route: '/explore', fn: 'breath', svg: '<path d="M3 12c3-5 15-5 18 0-3 5-15 5-18 0z"/><circle cx="12" cy="12" r="2.5"/>' },
]

const tags = [
  { label: '每日冥想', emoji: '✨', route: '/explore' },
  { label: '5分钟轻冥想', emoji: '🍃', route: '/explore' },
  { label: '工作减压', emoji: '💼', route: '/explore' },
  { label: '松心呼吸法', emoji: '❄️', route: '/explore' },
  { label: '情绪SOS', emoji: '💊', route: '/explore' },
  { label: '冥想入门', emoji: '⛰️', route: '/explore' },
  { label: '心灵助手对话', emoji: '🤖', route: '/chat' },
]

const categories = [
  {
    title: '舒缓身心',
    subtitle: '平复情绪，增强自我调节的能力',
    items: [
      { title: '平复烦躁', desc: '5-15分钟 · 冥想练习', gradient: 'linear-gradient(135deg, #4C1D95 0%, #312E81 100%)' },
      { title: '舒缓悲伤', desc: '10分钟 · 冥想练习', gradient: 'linear-gradient(135deg, #5B21B6 0%, #4338CA 100%)' },
      { title: '接纳自己', desc: '10分钟 · 冥想练习', gradient: 'linear-gradient(135deg, #6D28D9 0%, #4C1D95 100%)' },
    ]
  },
  {
    title: '日间减压',
    subtitle: '通过冥想和放松缓解压力',
    items: [
      { title: '日间提神', desc: '5-10分钟 · 冥想练习', gradient: 'linear-gradient(135deg, #4338CA 0%, #3730A3 100%)' },
      { title: '身心清理', desc: '15分钟 · 冥想练习', gradient: 'linear-gradient(135deg, #3B82F6 0%, #4338CA 100%)' },
      { title: '午后小憩', desc: '10分钟 · 助眠', gradient: 'linear-gradient(135deg, #6366F1 0%, #4F46E5 100%)' },
    ]
  },
  {
    title: '唤醒活力一整天',
    subtitle: '消除疲劳，活力充沛一整天',
    items: [
      { title: '正念冥想·入门', desc: '8章 · 冥想练习', gradient: 'linear-gradient(135deg, #7C3AED 0%, #6D28D9 100%)' },
      { title: '雨天', desc: '5-15分钟 · 冥想练习', gradient: 'linear-gradient(135deg, #8B5CF6 0%, #7C3AED 100%)' },
      { title: '唤醒清晨', desc: '10分钟 · 冥想练习', gradient: 'linear-gradient(135deg, #A78BFA 0%, #7C3AED 100%)' },
    ]
  },
]

function go(r: string) { router.push(r) }

// 滚动渐入观察器
let revealObserver: IntersectionObserver | null = null

onMounted(() => {
  updateClock()
  clockTimer = setInterval(updateClock, 10000)
  if (authStore.isLoggedIn) authStore.fetchUserInfo()

  // 载入后演示一次场景指示器 + 圆点
  setTimeout(flashScene, 800)

  // 滚动渐入动画（设计稿 .reveal）
  if (homeEl.value) {
    const targets = homeEl.value.querySelectorAll('.reveal')
    if ('IntersectionObserver' in window) {
      revealObserver = new IntersectionObserver((ents) => {
        ents.forEach((e) => {
          if (e.isIntersecting) { e.target.classList.add('visible'); revealObserver!.unobserve(e.target) }
        })
      }, { threshold: 0.12, rootMargin: '0px 0px -30px 0px' })
      targets.forEach((el) => revealObserver!.observe(el))
    } else {
      targets.forEach((el) => el.classList.add('visible'))
    }
  }
})

onUnmounted(() => {
  if (clockTimer) clearInterval(clockTimer)
  if (sceneFlashTimer) clearTimeout(sceneFlashTimer)
  if (revealObserver) revealObserver.disconnect()
})
</script>

<template>
  <div class="home" ref="homeEl" @touchstart="onTouchStart" @touchend="onTouchEnd">
    <!-- 背景：极光 AI 生图交叉淡入，左右滑切换场景；vignette + grain 增加质感 -->
    <div class="bg" ref="bgEl">
      <div
        v-for="(s, i) in scenes"
        :key="i"
        class="bg-scene"
        :class="{ active: i === sceneIdx }"
        :style="{ backgroundImage: 'url(/aurora-home.png)', backgroundPosition: s.pos, filter: s.filter }"
      ></div>
      <div class="bg-overlay" :style="{ background: currentScene.overlay }"></div>
      <div class="bg-glow" :style="glowStyle"></div>
      <div class="vignette"></div>
      <div class="grain"></div>
    </div>

    <!-- 内容：上下滚动正常浏览（状态栏/顶栏随滚动沉浸式吸顶） -->
    <div class="content" ref="scrollContainer" @scroll="onScroll">
      <!-- 吸顶头部：状态栏 + 问候，滑到功能入口上方后钉住 -->
      <div class="sticky-header" :class="{ pinned: isPinned }" ref="stickyHeader">
        <!-- 仿状态栏 -->
        <div class="statusbar">
          <span class="clock">{{ clock }}</span>
          <div class="sb-icons">
            <svg width="17" height="12" viewBox="0 0 17 12" fill="rgba(255,255,255,.9)"><rect x="0" y="7" width="3" height="5" rx="1"/><rect x="4.5" y="4.5" width="3" height="7.5" rx="1"/><rect x="9" y="2" width="3" height="10" rx="1"/><rect x="13.5" y="0" width="3" height="12" rx="1"/></svg>
            <svg width="16" height="12" viewBox="0 0 16 12" fill="none" stroke="rgba(255,255,255,.9)" stroke-width="1.2"><path d="M1 4.2C4 1.4 12 1.4 15 4.2M3.6 7C5.4 5.4 10.6 5.4 12.4 7M6.3 9.6C7 9 9 9 9.7 9.6"/></svg>
            <svg width="24" height="12" viewBox="0 0 24 12" fill="rgba(255,255,255,.9)"><rect x="0.5" y="0.5" width="20" height="11" rx="3" fill="none" stroke="rgba(255,255,255,.9)"/><rect x="2" y="2" width="15" height="8" rx="1.5"/><rect x="21.5" y="3.5" width="2" height="5" rx="1"/></svg>
          </div>
        </div>

        <!-- 顶部栏：固定问候 -->
        <div class="topbar">
          <div class="topbar-left">
            <div class="greet">{{ greet() }}</div>
            <div class="weekdays">
              <span
                v-for="(day, i) in weekDays"
                :key="i"
                :class="['weekday', { active: i === todayIndex }]"
              >
                {{ day }}
              </span>
            </div>
          </div>
          <div class="topbar-right">
            <div class="icn-btn">
              <svg class="icn-svg" viewBox="0 0 24 24"><path d="M11 5 6 9H3v6h3l5 4z"/><path d="M16 9a3 3 0 0 1 0 6"/></svg>
            </div>
            <div class="icn-btn">
              <svg class="icn-svg" viewBox="0 0 24 24"><rect x="4" y="4" width="6" height="6" rx="1.5"/><rect x="14" y="4" width="6" height="6" rx="1.5"/><rect x="4" y="14" width="6" height="6" rx="1.5"/><rect x="14" y="14" width="6" height="6" rx="1.5"/></svg>
            </div>
          </div>
        </div>
      </div>

      <div class="scroll-wrapper">
        <!-- 首屏场景舞台：极光主导，入口沉底 -->
        <div class="hero-section" ref="heroSection">
          <div class="hero-spacer"></div>

          <!-- 切换场景指示器：仅切换时浮现于入口上方 -->
          <div class="scene-indicator" :class="{ show: sceneFlash }">
            <span class="scene-dot-live"></span>
            <span class="scene-name">{{ sceneFlashName || currentScene.label }}</span>
            <span class="scene-sep">·</span>
            <span class="scene-tip">左右滑动切换场景</span>
          </div>

          <!-- 场景圆点：默认隐藏，切换时浮现；吸顶后隐藏 -->
          <div class="scene-dots" :class="{ show: dotsShow && !isPinned }">
            <span
              v-for="(s, j) in scenes"
              :key="j"
              :class="['dot', { active: j === sceneIdx }]"
              @click="setScene(j)"
            />
          </div>

          <div class="entries-section" ref="entriesSection">
            <div v-for="e in entries" :key="e.label" class="entry" :data-fn="e.fn" @click="go(e.route)">
              <div class="entry-icon">
                <svg class="entry-svg" viewBox="0 0 24 24" v-html="e.svg"></svg>
              </div>
              <span class="entry-label">{{ e.label }}</span>
            </div>
          </div>
        </div>

        <div class="quote-card">
          <div class="quote-left">
            <div class="quote-date-box">
              <span class="quote-date">{{ String(quote.date).padStart(2, '0') }}</span>
              <span class="quote-month">{{ quote.month }}</span>
            </div>
          </div>
          <div class="quote-right">
            <p class="quote-text">{{ quote.text }}</p>
            <p class="quote-author">— {{ quote.author }}</p>
            <svg class="quote-icon" viewBox="0 0 24 24"><path d="M9 7H5a2 2 0 0 0-2 2v4a2 2 0 0 0 2 2h2v-2H5V9h4zM19 7h-4a2 2 0 0 0-2 2v4a2 2 0 0 0 2 2h2v-2h-4V9h4z"/></svg>
          </div>
        </div>

        <div class="tags reveal reveal-d2">
          <div v-for="tag in tags" :key="tag.label" class="tag" @click="go(tag.route)">
            <span class="tag-emoji">{{ tag.emoji }}</span>
            <span class="tag-text">{{ tag.label }}</span>
          </div>
        </div>

        <div
          v-for="(cat, ci) in categories"
          :key="cat.title"
          class="category reveal"
          :class="'reveal-d' + Math.min(ci + 2, 4)"
        >
          <div class="category-header">
            <div>
              <div class="category-title">{{ cat.title }}</div>
              <div class="category-subtitle">{{ cat.subtitle }}</div>
            </div>
            <span class="category-more">更多 ›</span>
          </div>
          <div class="category-items">
            <div v-for="item in cat.items" :key="item.title" class="category-item" @click="go('/audio')">
              <div class="category-item-img" :style="{ background: item.gradient }">
                <div class="cat-play"><svg viewBox="0 0 24 24"><path d="M7 5v14l12-7z"/></svg></div>
              </div>
              <div class="category-item-info">
                <div class="category-item-title">{{ item.title }}</div>
                <div class="category-item-desc">{{ item.desc }}</div>
              </div>
            </div>
          </div>
        </div>

        <div class="bottom-spacer"></div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.home {
  --ease: cubic-bezier(.22,.61,.36,1);
  --quote-peek: 36px;
  position: relative;
  height: 100vh;
  height: 100dvh;
  overflow: hidden;
  background: #0F172A;
}

.bg {
  position: fixed;
  inset: 0;
  z-index: 0;
  overflow: hidden;
  will-change: opacity;
  /* 触发独立合成层，滚动时只重绘 opacity，避免 fixed+transform 的合成层撕裂 */
  transform: translate3d(0, 0, 0);
}

.bg-scene {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  background-size: cover;
  background-repeat: no-repeat;
  opacity: 0;
  transition: opacity 0.6s ease;
}

.bg-scene.active {
  opacity: 1;
}

.bg-overlay {
  position: absolute;
  inset: 0;
  z-index: 1;
  background: linear-gradient(to bottom,
    rgba(67,56,202,0.18) 0%,
    rgba(20,12,40,0.06) 30%,
    rgba(0,0,0,0.08) 60%,
    rgba(124,58,237,0.24) 100%);
  transition: background .9s var(--ease);
}

.bg-glow {
  content: '';
  position: absolute;
  inset: 0;
  z-index: 1;
  pointer-events: none;
  animation: auroraBreath 14s ease-in-out infinite;
  transition: background .9s var(--ease);
}
@keyframes auroraBreath {
  0%, 100% { opacity: .82; transform: scale(1); }
  50% { opacity: 1; transform: scale(1.04); }
}

/* 边缘暗角，聚焦内容 */
.vignette {
  position: absolute;
  inset: 0;
  z-index: 2;
  pointer-events: none;
  background: radial-gradient(130% 90% at 50% 42%, transparent 58%, rgba(7,11,22,.28) 100%);
}
/* 细腻颗粒质感 */
.grain {
  position: absolute;
  inset: 0;
  z-index: 3;
  pointer-events: none;
  opacity: .045;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='120' height='120'%3E%3Cfilter id='n'%3E%3CfeTurbulence type='fractalNoise' baseFrequency='0.9' numOctaves='2' stitchTiles='stitch'/%3E%3C/filter%3E%3Crect width='100%25' height='100%25' filter='url(%23n)'/%3E%3C/svg%3E");
  mix-blend-mode: overlay;
}

/* 内容滚动区 */
.content {
  position: relative;
  z-index: 5;
  height: 100vh;
  height: 100dvh;
  overflow-y: auto;
  overflow-x: hidden;
  -webkit-overflow-scrolling: touch;
}

/* 吸顶头部：渐变背景 + 玻璃模糊，与下方极光连续过渡（无接缝） */
.sticky-header {
  position: sticky;
  top: 0;
  z-index: 10;
  padding-top: calc(18px + env(safe-area-inset-top, 0px));
  /* 顶部（状态栏）深一些保证可读 → 向下完全透明融入极光 */
  background: linear-gradient(to bottom,
    rgba(15, 23, 42, 0.55) 0%,
    rgba(15, 23, 42, 0.22) 45%,
    rgba(15, 23, 42, 0) 100%);
  backdrop-filter: blur(14px);
  -webkit-backdrop-filter: blur(14px);
  /* 不画底部边框，让头部与下方极光无缝衔接 */
  transition:
    background .45s var(--ease),
    box-shadow .45s var(--ease),
    backdrop-filter .45s var(--ease),
    -webkit-backdrop-filter .45s var(--ease);
}
.sticky-header.pinned {
  /* 吸顶后转为整体深色玻璃，与内容区做强分隔 */
  background: rgba(15, 23, 42, 0.88);
  backdrop-filter: blur(22px);
  -webkit-backdrop-filter: blur(22px);
  box-shadow: 0 6px 24px rgba(0, 0, 0, 0.32);
}
/* 吸顶时平滑收起问候栏：用 max-height 过渡收起，避免 display:none 造成的回流跳动 */
.sticky-header.pinned .topbar {
  opacity: 0;
  transform: translateY(-10px);
  max-height: 0;
  padding-top: 0;
  padding-bottom: 0;
  margin: 0;
  pointer-events: none;
  animation: none;
}

/* 仿状态栏 */
.statusbar {
  position: relative;
  z-index: 6;
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 26px;
  font-size: 13px;
  font-weight: 500;
  color: rgba(255, 255, 255, 0.92);
  pointer-events: none;
}
.statusbar .sb-icons {
  display: flex;
  gap: 6px;
  align-items: center;
}

/* 顶部栏 */
.topbar {
  position: relative;
  z-index: 6;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 6px 24px 14px;
  overflow: hidden;
  max-height: 160px;
  transition: opacity .35s var(--ease), transform .35s var(--ease), max-height .4s var(--ease), padding .35s var(--ease);
  animation: introRise .95s var(--ease) both;
}

.greet {
  font-family: 'Lora', 'Noto Serif SC', serif;
  font-size: 34px;
  font-weight: 400;
  color: rgba(255, 255, 255, 0.96);
  letter-spacing: 1px;
  line-height: 1.15;
  margin-bottom: 10px;
}

.weekdays {
  display: flex;
  gap: 12px;
}

.weekday {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.4);
  font-weight: 300;
}

.weekday.active {
  color: rgba(255, 255, 255, 0.92);
  font-weight: 500;
}

.topbar-right {
  display: flex;
  gap: 14px;
  align-items: center;
}

/* 玻璃圆钮 */
.icn-btn {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.08);
  border: 0.5px solid rgba(255, 255, 255, 0.12);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  transition: background .25s var(--ease), transform .2s var(--ease);
}
.icn-btn:hover {
  background: rgba(255, 255, 255, 0.12);
  transform: scale(1.06);
}
.icn-btn:active {
  transform: scale(0.92);
}
.icn-svg {
  width: 18px;
  height: 18px;
  stroke: rgba(255, 255, 255, 0.72);
  fill: none;
  stroke-width: 1.6;
  stroke-linecap: round;
  stroke-linejoin: round;
}

.scroll-wrapper {
  padding: 0 24px;
}

/* 首屏场景舞台：极光主导，入口沉底 */
/* 首屏场景舞台：极光主导，入口沉底
   高度必须扣除上方内容(18+safe-top)+状态栏+顶栏 与 底部固定 TabBar(56+safe-bottom)，
   否则金句卡 margin-top:-peek 露出的那一截会被底部 TabBar 完全挡住 */
.hero-section {
  min-height: calc(100dvh - 207px - env(safe-area-inset-bottom, 0px) - env(safe-area-inset-top, 0px));
  display: flex;
  flex-direction: column;
  padding-bottom: 24px;
}

.hero-spacer {
  flex: 1 1 auto;
  min-height: 0;
}

/* 切换场景指示器：平时隐藏，切换时浮现 */
.scene-indicator {
  align-self: flex-start;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.8);
  font-weight: 300;
  letter-spacing: 0.4px;
  padding: 7px 15px;
  border-radius: 14px;
    background: rgba(255, 255, 255, 0.12);
    border: 0.5px solid rgba(255, 255, 255, 0.12);
    backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  opacity: 0;
  transform: translateY(8px);
  pointer-events: none;
  margin-bottom: 0;
  transition: opacity 0.35s ease, transform 0.35s ease, margin-bottom 0.35s ease;
}
.scene-indicator.show {
  opacity: 1;
  transform: translateY(0);
  margin-bottom: 18px;
}
.scene-dot-live {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: #A78BFA;
  box-shadow: 0 0 10px 2px rgba(167, 139, 250, 0.8);
  animation: livePulse 2.4s ease-in-out infinite;
}
@keyframes livePulse {
  0%, 100% { opacity: 1; transform: scale(1); }
  50% { opacity: 0.45; transform: scale(0.8); }
}
.scene-sep { color: rgba(255, 255, 255, 0.35); }
.scene-tip { color: rgba(255, 255, 255, 0.55); }

/* 场景圆点：默认隐藏，切换时浮现 */
.scene-dots {
  display: flex;
  justify-content: center;
  gap: 8px;
  margin-bottom: 16px;
  opacity: 0;
  transition: opacity .4s var(--ease);
  pointer-events: none;
}
.scene-dots.show {
  opacity: 1;
  pointer-events: auto;
}
.dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.30);
  cursor: pointer;
  transition: all 0.35s var(--ease);
}
.dot.active {
  width: 20px;
  border-radius: 3px;
  background: linear-gradient(90deg, #C4B5FD, #ECEAFF);
  box-shadow: 0 0 12px rgba(167,139,250,.7), 0 0 4px rgba(196,181,253,.5);
}

.entries-section {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
  margin-bottom: 18px;
  animation: introRise 1s var(--ease) .22s both;
}

.entry {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  transition: transform 0.2s;
}
.entry:active {
  transform: scale(0.92);
}
.entry-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background: radial-gradient(circle at 50% 32%, rgba(167, 139, 250, 0.34), rgba(255, 255, 255, 0.06) 72%);
  border: 1px solid rgba(167, 139, 250, 0.30);
  box-shadow: 0 8px 22px rgba(67, 56, 202, 0.30), inset 0 1px 0 rgba(255, 255, 255, 0.18);
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: transform .25s var(--ease), box-shadow .25s var(--ease), border-color .25s var(--ease);
}
.entry-svg {
  width: 27px;
  height: 27px;
  stroke: rgba(255, 255, 255, 0.9);
  fill: none;
  stroke-width: 1.5;
  stroke-linecap: round;
  stroke-linejoin: round;
}
.entry-label {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.72);
  font-weight: 300;
  transition: color .25s var(--ease);
}

/* 各入口独立配色（对齐设计稿 data-fn） */
.entry[data-fn="focus"] .entry-icon {
  background: radial-gradient(circle at 50% 32%, rgba(99,102,241,.40), rgba(99,102,241,.08) 72%);
  border-color: rgba(99,102,241,.45);
  box-shadow: 0 8px 22px rgba(99,102,241,.28), inset 0 1px 0 rgba(255,255,255,.18);
}
.entry[data-fn="sleep"] .entry-icon {
  background: radial-gradient(circle at 50% 32%, rgba(167,139,250,.40), rgba(124,58,237,.08) 72%);
  border-color: rgba(167,139,250,.45);
  box-shadow: 0 8px 22px rgba(124,58,237,.28), inset 0 1px 0 rgba(255,255,255,.18);
}
.entry[data-fn="nap"] .entry-icon {
  background: radial-gradient(circle at 50% 32%, rgba(251,191,36,.32), rgba(245,158,11,.06) 72%);
  border-color: rgba(251,191,36,.35);
  box-shadow: 0 8px 22px rgba(245,158,11,.20), inset 0 1px 0 rgba(255,255,255,.18);
}
.entry[data-fn="breath"] .entry-icon {
  background: radial-gradient(circle at 50% 32%, rgba(45,212,191,.34), rgba(20,184,166,.08) 72%);
  border-color: rgba(45,212,191,.38);
  box-shadow: 0 8px 22px rgba(20,184,166,.24), inset 0 1px 0 rgba(255,255,255,.18);
}
.entry:hover .entry-icon { transform: translateY(-3px) scale(1.04); }
.entry:hover .entry-label { color: rgba(255,255,255,.95); }
.entry[data-fn="focus"]:hover .entry-icon {
  border-color: rgba(99,102,241,.65);
  box-shadow: 0 12px 28px rgba(99,102,241,.40), inset 0 1px 0 rgba(255,255,255,.22), 0 0 18px rgba(99,102,241,.35);
}
.entry[data-fn="sleep"]:hover .entry-icon {
  border-color: rgba(167,139,250,.65);
  box-shadow: 0 12px 28px rgba(124,58,237,.42), inset 0 1px 0 rgba(255,255,255,.22), 0 0 18px rgba(167,139,250,.35);
}
.entry[data-fn="nap"]:hover .entry-icon {
  border-color: rgba(251,191,36,.55);
  box-shadow: 0 12px 28px rgba(245,158,11,.32), inset 0 1px 0 rgba(255,255,255,.22), 0 0 18px rgba(251,191,36,.30);
}
.entry[data-fn="breath"]:hover .entry-icon {
  border-color: rgba(45,212,191,.60);
  box-shadow: 0 12px 28px rgba(20,184,166,.36), inset 0 1px 0 rgba(255,255,255,.22), 0 0 18px rgba(45,212,191,.32);
}

.quote-card {
  position: relative;
  display: flex;
  gap: 16px;
  background: rgba(255, 255, 255, 0.08);
  border: 0.5px solid rgba(255, 255, 255, 0.12);
  border-radius: 20px;
  padding: 20px 20px 20px 24px;
  margin-bottom: 18px;
  margin-top: calc(-1 * var(--quote-peek));
  overflow: hidden;
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  transition: transform .3s var(--ease), border-color .3s var(--ease);
}
/* 金句卡左侧光条 + 微光渐变底 */
.quote-card::before {
  content: '';
  position: absolute;
  left: 0;
  top: 16px;
  bottom: 16px;
  width: 3px;
  border-radius: 3px;
  background: linear-gradient(180deg, #A78BFA, #7C3AED, #6366F1);
  box-shadow: 0 0 14px rgba(167,139,250,.55), 0 0 4px rgba(124,58,237,.4);
}
.quote-card::after {
  content: '';
  position: absolute;
  inset: 0;
  z-index: 0;
  pointer-events: none;
  background:
    radial-gradient(ellipse at 12% 8%, rgba(167,139,250,.08) 0%, transparent 55%),
    radial-gradient(ellipse at 88% 92%, rgba(124,58,237,.06) 0%, transparent 50%);
}
.quote-card > * { position: relative; z-index: 1; }
.quote-card:hover { transform: translateY(-2px); border-color: rgba(167,139,250,.30); }
.quote-left {
  flex-shrink: 0;
}
.quote-date-box {
  width: 52px;
  height: 52px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.08);
  border: 0.5px solid rgba(255, 255, 255, 0.12);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 2px;
}
.quote-date {
  font-size: 22px;
  font-weight: 400;
  color: rgba(255, 255, 255, 0.9);
  line-height: 1;
}
.quote-month {
  font-size: 10px;
  color: rgba(255, 255, 255, 0.4);
  letter-spacing: 1px;
}
.quote-right {
  flex: 1;
  position: relative;
}
.quote-text {
  font-size: 15px;
  color: rgba(255, 255, 255, 0.86);
  line-height: 1.6;
  margin: 0 0 8px;
  font-weight: 300;
}
.quote-author {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.35);
  margin: 0;
}
.quote-icon {
  position: absolute;
  bottom: -2px;
  right: -2px;
  width: 26px;
  height: 26px;
  fill: rgba(255, 255, 255, 0.15);
}

.tags {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 24px;
}
.tag {
  display: flex;
  align-items: center;
  gap: 6px;
  background: rgba(255, 255, 255, 0.08);
  border: 0.5px solid rgba(255, 255, 255, 0.12);
  border-radius: 20px;
  padding: 10px 16px;
  cursor: pointer;
  transition: transform .2s var(--ease), background .25s var(--ease), border-color .25s var(--ease);
}
.tag:hover {
  transform: translateY(-2px);
  background: rgba(255, 255, 255, 0.12);
  border-color: rgba(167,139,250,.30);
  box-shadow: 0 4px 14px rgba(124,58,237,.15);
}
.tag:active {
  background: rgba(255, 255, 255, 0.12);
  transform: scale(0.95);
}
.tag-emoji {
  font-size: 14px;
  line-height: 1;
}
.tag-text {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.72);
  font-weight: 300;
  white-space: nowrap;
}

.category {
  margin-bottom: 38px;
}
.category-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}
.category-title {
  position: relative;
  padding-left: 14px;
  font-family: 'Lora', 'Noto Serif SC', serif;
  font-size: 20px;
  font-weight: 500;
  color: rgba(255, 255, 255, 0.94);
  margin-bottom: 4px;
  letter-spacing: 0.5px;
}
/* 标题左侧装饰线 */
.category-title::before {
  content: '';
  position: absolute;
  left: 0;
  top: 3px;
  bottom: 3px;
  width: 3px;
  border-radius: 2px;
  background: linear-gradient(180deg, #A78BFA, #7C3AED);
  opacity: .7;
}
.category-subtitle {
  padding-left: 14px;
  font-size: 13px;
  color: rgba(255, 255, 255, 0.35);
  font-weight: 300;
}
.category-more {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.5);
  font-weight: 300;
  cursor: pointer;
  padding: 5px 13px;
  border-radius: 20px;
  border: 0.5px solid rgba(255,255,255,.12);
  background: rgba(255,255,255,.06);
  white-space: nowrap;
  transition: all .25s var(--ease);
}
.category-more:hover {
  color: #ECEAFF;
  border-color: rgba(167,139,250,.4);
  background: rgba(167,139,250,.12);
}
.category-more:active { transform: scale(.95); }
.category-items {
  display: flex;
  gap: 12px;
  overflow-x: auto;
  padding-bottom: 6px;
}
.category-items::-webkit-scrollbar {
  display: none;
}
.category-item {
  position: relative;
  flex-shrink: 0;
  width: 150px;
  background: rgba(255, 255, 255, 0.08);
  border: 0.5px solid rgba(255, 255, 255, 0.12);
  border-radius: 16px;
  overflow: hidden;
  cursor: pointer;
  transition: transform .3s var(--ease), border-color .3s var(--ease), box-shadow .3s var(--ease);
}
.category-item::after {
  content: '';
  position: absolute;
  inset: 0;
  border-radius: 16px;
  pointer-events: none;
  opacity: 0;
  z-index: 1;
  box-shadow: inset 0 1px 0 rgba(255,255,255,.10);
  transition: opacity .3s ease;
}
.category-item:hover::after { opacity: 1; }
.category-item:hover {
  transform: translateY(-4px);
  border-color: rgba(167,139,250,.40);
  box-shadow: 0 12px 28px rgba(67,56,202,.28), 0 4px 12px rgba(0,0,0,.15);
}
.category-item-img {
  position: relative;
  width: 100%;
  height: 150px;
}
.cat-play {
  position: absolute;
  left: 10px;
  bottom: 10px;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.16);
  border: 0.5px solid rgba(255, 255, 255, 0.28);
  display: flex;
  align-items: center;
  justify-content: center;
  backdrop-filter: blur(6px);
  -webkit-backdrop-filter: blur(6px);
  transition: background .25s ease, transform .25s ease, box-shadow .25s ease, border-color .25s ease;
}
.cat-play svg {
  width: 13px;
  height: 13px;
  fill: rgba(255, 255, 255, 0.92);
  margin-left: 1.5px;
}
.category-item:hover .cat-play {
  background: rgba(167,139,250,.30);
  border-color: rgba(167,139,250,.50);
  box-shadow: 0 4px 12px rgba(124,58,237,.35);
  transform: scale(1.08);
}
.category-item-info {
  padding: 12px;
}
.category-item-title {
  font-size: 15px;
  font-weight: 400;
  color: rgba(255, 255, 255, 0.85);
  margin-bottom: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.category-item-desc {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.35);
}

.bottom-spacer {
  height: calc(96px + env(safe-area-inset-bottom, 0px));
}

/* 滚动渐入动画 */
.reveal {
  opacity: 0;
  transform: translateY(24px);
  transition: opacity .65s var(--ease), transform .65s var(--ease);
}
.reveal.visible {
  opacity: 1;
  transform: translateY(0);
}
.reveal-d1 { transition-delay: .05s; }
.reveal-d2 { transition-delay: .12s; }
.reveal-d3 { transition-delay: .20s; }
.reveal-d4 { transition-delay: .28s; }

/* 首屏入场动效（对齐设计稿 introRise） */
@keyframes introRise {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}
/* 尊重系统“减少动态效果”偏好 */
@media (prefers-reduced-motion: reduce) {
  .bg-glow, .scene-dot-live,
  .sticky-header .topbar, .entries-section {
    animation: none !important;
  }
  .reveal { transition-duration: .01ms !important; }
}
</style>
