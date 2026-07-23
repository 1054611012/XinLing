<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const scenes = [
  { video: '/avatar/篝火.mp4', label: '篝火' },
  { video: '/avatar/海洋.mp4', label: '海洋' },
]

const sceneIdx = ref(0)
const currentVideo = computed(() => scenes[sceneIdx.value].video)
const currentLabel = computed(() => scenes[sceneIdx.value].label)

const scrollContainer = ref<HTMLElement>()
const entriesSection = ref<HTMLElement>()
const videoBlur = ref(0)
const topbarTranslateY = ref(0)
const topbarOpacity = ref(1)
const heroActionOpacity = ref(1)
const heroActionScale = ref(1)

let touchX = 0
let touchY = 0
let isSwitching = false
const dotsVisible = ref(false)
let dotsTimer: ReturnType<typeof setTimeout> | null = null

function showDots() {
  dotsVisible.value = true
  if (dotsTimer) clearTimeout(dotsTimer)
  dotsTimer = setTimeout(() => {
    dotsVisible.value = false
  }, 1500)
}

function onTouchStart(e: TouchEvent) {
  touchX = e.touches[0].clientX
  touchY = e.touches[0].clientY
}
function onTouchEnd(e: TouchEvent) {
  if (isSwitching) return
  if (scrollContainer.value && scrollContainer.value.scrollTop > 5) return

  const dx = e.changedTouches[0].clientX - touchX
  const dy = e.changedTouches[0].clientY - touchY
  if (Math.abs(dx) > Math.abs(dy) && Math.abs(dx) > 60) {
    isSwitching = true
    showDots()
    if (dx < 0) {
      sceneIdx.value = (sceneIdx.value + 1) % scenes.length
    } else {
      sceneIdx.value = (sceneIdx.value - 1 + scenes.length) % scenes.length
    }
    setTimeout(() => { isSwitching = false }, 300)
  }
}

function onScroll() {
  if (!scrollContainer.value) return
  const scrollTop = scrollContainer.value.scrollTop
  const windowHeight = window.innerHeight

  const heroActionStartOffset = 0
  const heroActionEndOffset = windowHeight * 0.3
  const heroActionProgress = Math.min(1, Math.max(0, (scrollTop - heroActionStartOffset) / (heroActionEndOffset - heroActionStartOffset)))
  
  heroActionOpacity.value = Math.max(0, 1 - heroActionProgress * 3)
  heroActionScale.value = Math.max(0.7, 1 - heroActionProgress * 0.3)

  if (entriesSection.value) {
    const topbarHeight = 120
    const entriesViewportTop = entriesSection.value.offsetTop - scrollTop
    const topbarBottom = topbarHeight

    const overflow = topbarBottom - entriesViewportTop

    if (overflow <= 0) {
      topbarTranslateY.value = 0
      topbarOpacity.value = 1
    } else {
      topbarTranslateY.value = -overflow
      topbarOpacity.value = Math.max(0, 1 - overflow / (windowHeight * 0.4))
    }
  }

  const blurProgress = Math.min(1, scrollTop / (windowHeight * 0.4))
  videoBlur.value = blurProgress * 20
}

function greet() {
  const h = new Date().getHours()
  if (h < 6) return '夜深了'
  if (h < 12) return '早上好'
  if (h < 14) return '中午好'
  if (h < 18) return '下午好'
  return '晚上好'
}

const weekDays = ['日', '一', '二', '三', '四', '五', '六']
const todayIndex = new Date().getDay()

const quote = {
  text: '到头来，有意义的并不是结果，而是我们度过的那些无可替代的时间。',
  author: '星野道夫',
  date: new Date().getDate(),
  month: 'JUN',
}

const entries = [
  { label: '专注', icon: 'aim', route: '/focus' },
  { label: '睡眠', icon: 'moon-o', route: '/sleep' },
  { label: '小憩', icon: 'pause-circle-o', route: '/explore' },
  { label: '呼吸', icon: 'leaf-o', route: '/explore' },
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
      { title: '平复烦躁', desc: '5-15分钟 · 冥想练习', gradient: 'linear-gradient(135deg, #1a3a3a 0%, #0d2b2b 100%)' },
      { title: '舒缓悲伤', desc: '10分钟 · 冥想练习', gradient: 'linear-gradient(135deg, #2a2a3a 0%, #1a1a2a 100%)' },
      { title: '接纳自己', desc: '10分钟 · 冥想练习', gradient: 'linear-gradient(135deg, #3a2a2a 0%, #2a1a1a 100%)' },
    ]
  },
  {
    title: '日间减压',
    subtitle: '通过冥想和放松缓解压力',
    items: [
      { title: '日间提神', desc: '5-10分钟 · 冥想练习', gradient: 'linear-gradient(135deg, #3a3520 0%, #2a2510 100%)' },
      { title: '身心清理', desc: '15分钟 · 冥想练习', gradient: 'linear-gradient(135deg, #203a35 0%, #102a25 100%)' },
      { title: '午后小憩', desc: '10分钟 · 助眠', gradient: 'linear-gradient(135deg, #35203a 0%, #25102a 100%)' },
    ]
  },
  {
    title: '唤醒活力一整天',
    subtitle: '消除疲劳，活力充沛一整天',
    items: [
      { title: '正念冥想·入门', desc: '8章 · 冥想练习', gradient: 'linear-gradient(135deg, #20353a 0%, #10252a 100%)' },
      { title: '雨天', desc: '5-15分钟 · 冥想练习', gradient: 'linear-gradient(135deg, #2a2a3a 0%, #1a1a2a 100%)' },
      { title: '唤醒清晨', desc: '10分钟 · 冥想练习', gradient: 'linear-gradient(135deg, #3a3a20 0%, #2a2a10 100%)' },
    ]
  },
]

function go(r: string) { router.push(r) }

onMounted(() => {
  if (authStore.isLoggedIn) authStore.fetchUserInfo()
})
</script>

<template>
  <div class="home" @touchstart="onTouchStart" @touchend="onTouchEnd">
    <div class="bg">
      <video
        :key="sceneIdx"
        class="bg-video"
        :src="currentVideo"
        autoplay muted loop playsinline
        :style="{ filter: `blur(${videoBlur}px)` }"
      ></video>
      <div class="bg-overlay"></div>
    </div>

    <div class="topbar" 
      :style="{ 
        transform: `translateY(${topbarTranslateY}px)`, 
        opacity: topbarOpacity 
      }">
      <div class="topbar-left">
        <div class="greet">{{ greet() }}</div>
        <div class="weekdays">
          <span v-for="(day, i) in weekDays" :key="i"
            :class="['weekday', { active: i === todayIndex }]">
            {{ i === todayIndex ? '今' : day }}
          </span>
        </div>
      </div>
      <div class="topbar-right">
        <van-icon name="volume-o" size="18" color="rgba(255,255,255,0.7)" />
        <van-icon name="apps-o" size="18" color="rgba(255,255,255,0.7)" class="ml-12" />
      </div>
    </div>

    <div class="content" ref="scrollContainer" @scroll="onScroll">
      <div class="scroll-wrapper">
        <div class="hero-section">
          <div class="hero-action" 
            @click="go('/explore')"
            :style="{ 
              opacity: heroActionOpacity,
              transform: `scale(${heroActionScale})`
            }">
            <span class="hero-action-icon">🧘♀️</span>
            <span class="hero-action-text">找回自己的节奏</span>
            <van-icon name="arrow" size="12" color="rgba(255,255,255,0.5)" />
          </div>

          <div class="scene-dots" :class="{ visible: dotsVisible }">
            <span
              v-for="(s, i) in scenes"
              :key="i"
              :class="['scene-dot', { active: i === sceneIdx }]"
            />
          </div>

          <div class="entries-section" ref="entriesSection">
          <div v-for="e in entries" :key="e.label" class="entry" @click="go(e.route)">
            <div class="entry-icon">
              <van-icon :name="e.icon" size="28" color="rgba(255,255,255,0.9)" />
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
            <p class="quote-author">{{ quote.author }}</p>
            <van-icon name="quotes" size="16" color="rgba(255,255,255,0.15)" class="quote-icon" />
          </div>
        </div>

        <div class="tags">
          <div v-for="tag in tags" :key="tag.label" class="tag" @click="go(tag.route)">
            <span class="tag-emoji">{{ tag.emoji }}</span>
            <span class="tag-text">{{ tag.label }}</span>
          </div>
        </div>

        <div v-for="cat in categories" :key="cat.title" class="category">
          <div class="category-header">
            <div>
              <div class="category-title">{{ cat.title }}</div>
              <div class="category-subtitle">{{ cat.subtitle }}</div>
            </div>
            <span class="category-more">更多</span>
          </div>
          <div class="category-items">
            <div v-for="item in cat.items" :key="item.title" class="category-item" @click="go('/audio')">
              <div class="category-item-img" :style="{ background: item.gradient }"></div>
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
  position: relative;
  height: 100vh;
  height: 100dvh;
  overflow: hidden;
  background: #0a0a0a;
}

.bg {
  position: fixed;
  inset: 0;
  z-index: 0;
  overflow: hidden;
}

.bg-video {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.bg-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(to bottom,
    rgba(0,0,0,0.35) 0%,
    rgba(0,0,0,0.1) 40%,
    rgba(0,0,0,0.1) 60%,
    rgba(0,0,0,0.5) 100%);
  z-index: 1;
}

.topbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 10;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: calc(44px + env(safe-area-inset-top, 16px)) 24px 16px;
  transition: opacity 0.1s ease;
}

.greet {
  font-size: 36px;
  font-weight: 300;
  color: rgba(255,255,255,0.95);
  letter-spacing: 1px;
  line-height: 1.2;
  margin-bottom: 8px;
}

.weekdays {
  display: flex;
  gap: 12px;
}

.weekday {
  font-size: 13px;
  color: rgba(255,255,255,0.4);
  font-weight: 300;
}

.weekday.active {
  color: rgba(255,255,255,0.9);
  font-weight: 500;
}

.topbar-right {
  display: flex;
  gap: 12px;
  margin-top: 8px;
}

.topbar-right .van-icon {
  cursor: pointer;
  transition: opacity 0.2s;
}

.topbar-right .van-icon:active {
  opacity: 0.6;
}

.ml-12 {
  margin-left: 12px;
}

.content {
  position: relative;
  z-index: 5;
  height: 100vh;
  height: 100dvh;
  overflow-y: auto;
  overflow-x: hidden;
  scroll-snap-type: y mandatory;
  scroll-behavior: smooth;
}

.scroll-wrapper {
  padding: 0 24px;
}

.hero-section {
  height: calc(95dvh - 50px - env(safe-area-inset-bottom, 0px));
  display: flex;
  flex-direction: column;
  padding-top: calc(140px + env(safe-area-inset-top, 16px));
  padding-bottom: 13px;
  scroll-snap-align: start;
}

.hero-action {
  align-self: flex-start;
  display: inline-flex;
  align-items: center;
  gap: 6px;
  background: rgba(255,255,255,0.1);
  border: 0.5px solid rgba(255,255,255,0.12);
  border-radius: 20px;
  padding: 8px 16px;
  margin-bottom: 32px;
  cursor: pointer;
  transition: all 0.1s ease;
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
}

.hero-action:active {
  background: rgba(255,255,255,0.18);
  transform: scale(0.96);
}

.hero-action-icon {
  font-size: 14px;
}

.hero-action-text {
  font-size: 13px;
  color: rgba(255,255,255,0.75);
  font-weight: 300;
}

.scene-dots {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 8px;
  margin: 16px 0 24px;
  padding: 6px 12px;
  border-radius: 12px;
  background: rgba(255,255,255,0.08);
  border: 0.5px solid rgba(255,255,255,0.1);
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  align-self: center;
  opacity: 0;
  transition: opacity 0.3s ease;
  pointer-events: none;
}

.scene-dots.visible {
  opacity: 1;
}

.scene-dot {
  width: 5px;
  height: 5px;
  border-radius: 50%;
  background: rgba(255,255,255,0.35);
  transition: all 0.3s ease;
}

.scene-dot.active {
  background: rgba(255,255,255,0.9);
  box-shadow: 0 0 4px rgba(255,255,255,0.4);
}

.entries-section {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
  margin-top: auto;
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
  background: rgba(255,255,255,0.12);
  backdrop-filter: blur(6px);
  -webkit-backdrop-filter: blur(6px);
  display: flex;
  align-items: center;
  justify-content: center;
}

.entry-label {
  font-size: 13px;
  color: rgba(255,255,255,0.7);
  font-weight: 300;
}

.quote-card {
  display: flex;
  gap: 16px;
  background: rgba(255,255,255,0.06);
  border: 0.5px solid rgba(255,255,255,0.08);
  border-radius: 20px;
  padding: 20px;
  margin-top: 6px;
  margin-bottom: 24px;
  scroll-snap-align: start;
}

.quote-left {
  flex-shrink: 0;
}

.quote-date-box {
  width: 52px;
  height: 52px;
  border-radius: 12px;
  background: rgba(255,255,255,0.08);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 2px;
}

.quote-date {
  font-size: 22px;
  font-weight: 400;
  color: rgba(255,255,255,0.9);
  line-height: 1;
}

.quote-month {
  font-size: 10px;
  color: rgba(255,255,255,0.4);
  letter-spacing: 1px;
}

.quote-right {
  flex: 1;
  position: relative;
}

.quote-text {
  font-size: 15px;
  color: rgba(255,255,255,0.85);
  line-height: 1.6;
  margin: 0 0 8px;
  font-weight: 300;
}

.quote-author {
  font-size: 12px;
  color: rgba(255,255,255,0.35);
  margin: 0;
}

.quote-icon {
  position: absolute;
  bottom: 0;
  right: 0;
}

.tags {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 32px;
}

.tag {
  display: flex;
  align-items: center;
  gap: 6px;
  background: rgba(255,255,255,0.06);
  border: 0.5px solid rgba(255,255,255,0.08);
  border-radius: 20px;
  padding: 10px 16px;
  cursor: pointer;
  transition: all 0.2s;
}

.tag:active {
  background: rgba(255,255,255,0.12);
  transform: scale(0.95);
}

.tag-emoji {
  font-size: 14px;
  line-height: 1;
}

.tag-text {
  font-size: 14px;
  color: rgba(255,255,255,0.75);
  font-weight: 300;
  white-space: nowrap;
}

.category {
  margin-bottom: 32px;
}

.category-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.category-title {
  font-size: 20px;
  font-weight: 400;
  color: rgba(255,255,255,0.9);
  margin-bottom: 4px;
  letter-spacing: 0.5px;
}

.category-subtitle {
  font-size: 13px;
  color: rgba(255,255,255,0.35);
  font-weight: 300;
}

.category-more {
  font-size: 13px;
  color: rgba(255,255,255,0.3);
  cursor: pointer;
}

.category-items {
  display: flex;
  gap: 12px;
  overflow-x: auto;
  padding-bottom: 8px;
}

.category-items::-webkit-scrollbar {
  display: none;
}

.category-item {
  flex-shrink: 0;
  width: 150px;
  background: rgba(255,255,255,0.06);
  border: 0.5px solid rgba(255,255,255,0.08);
  border-radius: 16px;
  overflow: hidden;
  cursor: pointer;
  transition: transform 0.2s;
}

.category-item:active {
  transform: scale(0.97);
}

.category-item-img {
  width: 100%;
  height: 150px;
}

.category-item-info {
  padding: 12px;
}

.category-item-title {
  font-size: 15px;
  font-weight: 400;
  color: rgba(255,255,255,0.85);
  margin-bottom: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.category-item-desc {
  font-size: 12px;
  color: rgba(255,255,255,0.35);
}

.bottom-spacer {
  height: calc(70px + env(safe-area-inset-bottom, 20px));
}
</style>
