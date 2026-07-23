<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

// 当前标签页
const activeTab = ref(0)
const tabNames = ['探索', '冥想', '睡眠', '声音']

// ===== 探索标签页内容 =====
const exploreCategories = [
  {
    title: '改善睡眠',
    subtitle: '精选助眠练习，让你睡得更好',
    items: [
      { title: '深度睡眠疗愈 I', desc: '7章 · 系列', cover: 'deep-sleep' },
      { title: 'YogaNidra快速深睡', desc: '15 分钟 · 时刻', cover: 'crescent-moon' },
      { title: 'YogaNidra睡眠冥想', desc: '20 分钟', cover: 'sleep-meditation' },
    ]
  },
  {
    title: '休息放松',
    subtitle: '稍作休整，再轻松出发',
    items: [
      { title: '缓解疲劳', desc: '5-20 分钟 · 时刻', cover: 'relax-fatigue', badge: '免费' },
      { title: '小憩', desc: '5-15 分钟 · 时刻', cover: 'nap' },
      { title: '日间提神', desc: '5-10 分钟', cover: 'energy-boost' },
    ]
  },
  {
    title: '提升效能',
    subtitle: '用对方法，事半功倍',
    items: [
      { title: '日间活力唤醒', desc: '5章 · 系列', cover: 'morning-energy' },
      { title: '提升专注', desc: '5章 · 系列', cover: 'focus-boost' },
      { title: '提高效率', desc: '5章 · 系列', cover: 'efficiency' },
    ]
  },
  {
    title: '调节情绪',
    subtitle: '学会辨别和应对各种情绪',
    items: [
      { title: 'STOP 暂停法', desc: '10 分钟 · 时刻', cover: 'stop-method', badge: '免费' },
      { title: '情绪感知', desc: '5-15 分钟 · 时刻', cover: 'emotion-aware', badge: '免费' },
      { title: '克服恐惧', desc: '10 分钟', cover: 'overcome-fear' },
    ]
  },
  {
    title: '冥想入门',
    subtitle: '简单易练的新手专区',
    items: [
      { title: '正念冥想 · 入门', desc: '8章 · 系列', cover: 'mindfulness-beginner' },
      { title: '7 天冥想入门', desc: '7章 · 系列', cover: '7-days-beginner', badge: '免费' },
      { title: '呼吸练习', desc: '5-15 分钟', cover: 'breath-practice' },
    ]
  },
]

// ===== 冥想标签页内容 =====
const meditationItems = [
  { title: '找回自己的节奏', desc: '15 分钟 · 时刻', cover: 'find-rhythm', badge: '新品' },
  { title: '平复纷乱思绪', desc: '10 分钟 · 时刻', cover: 'calm-thoughts' },
  { title: '信息过载', desc: '10 分钟 · 时刻', cover: 'info-overload' },
  { title: '声音疗愈', desc: '15 分钟 · 时刻', cover: 'sound-healing', badge: '免费' },
  { title: '回归内在安定', desc: '10 分钟 · 时刻', cover: 'inner-peace' },
  { title: '舒缓慢性压力', desc: '15 分钟 · 时刻', cover: 'chronic-stress' },
  { title: '释放愤怒', desc: '10 分钟 · 时刻', cover: 'release-anger' },
  { title: '应对困难情绪', desc: '15 分钟 · 时刻', cover: 'difficult-emotions' },
  { title: '5 分钟轻冥想', desc: '解压放松 恢复精力', cover: 'light-meditation' },
  { title: '释放身心压力', desc: '10-30 分钟 · 时刻', cover: 'release-stress' },
  { title: '倾听内心渴望', desc: '20 分钟 · 时刻', cover: 'inner-desire' },
]

// ===== 睡眠标签页内容 =====
const sleepItems = [
  { title: '生命树', desc: '45 分钟', cover: 'tree-life' },
  { title: '山野青梅', desc: '30 分钟', cover: 'mountain-plum' },
  { title: '十七颗晚星', desc: '30 分钟', cover: 'seventeen-stars' },
  { title: '鹭鸟北归', desc: '30 分钟', cover: 'egret-return' },
  { title: '悬浮', desc: '25 分钟', cover: 'floating' },
  { title: '夜坐图', desc: '30 分钟', cover: 'night-sitting' },
  { title: '蓝色时间', desc: '15 分钟', cover: 'blue-time' },
  { title: '蝴蝶猎人', desc: '30 分钟', cover: 'butterfly-hunter' },
]

// ===== 声音标签页内容 =====
const soundItems = [
  { title: '夏威夷海滩', desc: '814 人正在听', cover: 'hawaii-beach' },
  { title: '悠长假期', desc: '163 人正在听', cover: 'long-vacation' },
  { title: '声音浴', desc: '1180 人正在听', cover: 'sound-bath', badge: '免费' },
  { title: '四天王寺', desc: '1373 人正在听', cover: 'temple' },
  { title: '千岛湖', desc: '555 人正在听', cover: 'thousand-lakes' },
  { title: '假日', desc: '90 人正在听', cover: 'holiday' },
  { title: '猫的午后', desc: '1045 人正在听', cover: 'cat-afternoon', badge: '免费' },
  { title: '热带雨林', desc: '183 人正在听', cover: 'rainforest' },
  { title: '春雨', desc: '1486 人正在听', cover: 'spring-rain' },
  { title: '须臾', desc: '291 人正在听', cover: 'moment' },
]

// 为你推荐内容
const recommendations = [
  {
    user: 'Kooji',
    avatar: '',
    text: '以前在乡下住着，一直非常喜欢晚上乘凉的时候，听着虫鸣，吹着凉风，听外婆外公随意唠嗑。那时候真的很好啊。',
    audio: { title: '夏虫', type: '声音' },
  },
  {
    user: '安东尼',
    avatar: '🎨',
    text: '快考试了，常常抑制不住紧张，每次刷题前都会听一下，缓解紧张，头脑更清醒。',
    audio: { title: '学习压力', type: '冥想' },
  },
  {
    user: '小董',
    avatar: '🌸',
    text: '好有过年的感觉哦。以前过年吃完年夜饭都会和爸妈一起放烟花，可惜现在城市管理不给放了~',
    audio: { title: '焰火', type: '声音' },
  },
  {
    user: '拍了拍电视机',
    avatar: '📺',
    text: '昨晚听了这个冥想，觉得自己被温暖包围了。原来入睡也可以这样带着幸福感。',
    audio: { title: '5 天深度放松入眠', type: '冥想' },
  },
  {
    user: '996冬菇酱',
    avatar: '',
    text: '在室内待久了，打开「竹林」去听风吹过叶子的声音，顿时觉得明朗、凉快多了。',
    audio: { title: '竹林', type: '声音' },
  },
]

function goToAudio(item: any) {
  router.push('/audio')
}

function goToSound(item: any) {
  router.push('/audio')
}

onMounted(() => {
  // 初始化
})
</script>

<template>
  <div class="tide-explore-page">
    <!-- 顶部标签导航 -->
    <div class="explore-header">
      <div class="tab-navigation">
        <div
          v-for="(tab, index) in tabNames"
          :key="tab"
          :class="['tab-item', { active: activeTab === index }]"
          @click="activeTab = index"
        >
          {{ tab }}
          <div v-if="activeTab === index" class="tab-indicator" />
        </div>
      </div>
      <van-icon name="search" size="24" color="#666" class="search-icon" />
    </div>

    <!-- 探索标签页 -->
    <div v-show="activeTab === 0" class="tab-content">
      <!-- Banner 轮播 -->
      <van-swipe class="banner-swipe" :autoplay="3000" indicator-color="#666" :loop="true" :show-indicators="true">
        <van-swipe-item v-for="n in 5" :key="n">
          <div class="banner">
            <div class="banner-content">
              <div class="banner-tag">冥想上新</div>
              <div class="banner-title">找回自己的节奏</div>
            </div>
          </div>
        </van-swipe-item>
      </van-swipe>

      <!-- 改善睡眠 - 固定5个音频，横向滚动 -->
      <div class="category-section">
        <div class="section-header">
          <div>
            <h2 class="section-title">改善睡眠</h2>
            <p class="section-subtitle">精选助眠练习，让你睡得更好</p>
          </div>
          <span class="more-text">更多</span>
        </div>
        
        <div class="items-scroll">
          <div v-for="item in exploreCategories[0].items" 
               :key="item.title" 
               class="item-card"
               @click="goToAudio(item)">
            <div class="item-cover" :class="item.cover">
              <div v-if="item.badge" class="item-badge">{{ item.badge }}</div>
            </div>
            <div class="item-info">
              <div class="item-title">{{ item.title }}</div>
              <div class="item-desc">{{ item.desc }}</div>
            </div>
          </div>
        </div>
      </div>

      <!-- 休息放松 -->
      <div class="category-section">
        <div class="section-header">
          <div>
            <h2 class="section-title">休息放松</h2>
            <p class="section-subtitle">稍作休整，再轻松出发</p>
          </div>
          <span class="more-text">更多</span>
        </div>
        
        <div class="items-scroll">
          <div v-for="item in exploreCategories[1].items" 
               :key="item.title" 
               class="item-card"
               @click="goToAudio(item)">
            <div class="item-cover" :class="item.cover">
              <div v-if="item.badge" class="item-badge">{{ item.badge }}</div>
            </div>
            <div class="item-info">
              <div class="item-title">{{ item.title }}</div>
              <div class="item-desc">{{ item.desc }}</div>
            </div>
          </div>
        </div>
      </div>

      <!-- 声音灵感 -->
      <div class="inspiration-section">
        <div class="section-header">
          <h2 class="section-title">声音灵感</h2>
        </div>
        <div class="inspiration-card" @click="goToAudio({})">
          <div class="inspiration-icon">🎵</div>
          <div class="inspiration-content">
            <div class="inspiration-title">混音空间</div>
            <div class="inspiration-desc">创造属于你的声音场景</div>
          </div>
        </div>
      </div>

      <!-- 为你推荐 -->
      <div class="recommendations-section">
        <div class="section-header">
          <h2 class="section-title">为你推荐</h2>
        </div>
        <div class="recommendation-list">
          <div v-for="rec in recommendations" 
               :key="rec.user" 
               class="recommendation-card">
            <div class="rec-header">
              <div class="rec-avatar">{{ rec.avatar }}</div>
              <div class="rec-user">{{ rec.user }}</div>
            </div>
            <div class="rec-text">{{ rec.text }}</div>
            <div class="rec-audio" @click="goToAudio({})">
              <div class="audio-thumb"></div>
              <div class="audio-info">
                <div class="audio-title">{{ rec.audio.title }}</div>
                <div class="audio-type">{{ rec.audio.type }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 冥想标签页 -->
    <div v-show="activeTab === 1" class="tab-content">
      <!-- 筛选标签 -->
      <div class="filter-tags">
        <div class="filter-tag active">全部</div>
        <div class="filter-tag">新手</div>
        <div class="filter-tag">系列</div>
        <div class="filter-tag">助眠</div>
        <div class="filter-tag">情绪</div>
        <div class="filter-tag">减压</div>
      </div>

      <!-- 特色推荐 -->
      <div class="featured-card">
        <div class="featured-content">
          <div class="featured-title">好状态，呼吸法</div>
          <div class="featured-desc">减少压力 平衡身心</div>
        </div>
        <div class="featured-icon">🌬️</div>
      </div>

      <!-- 冥想列表 -->
      <div class="meditation-grid">
        <div v-for="item in meditationItems" 
             :key="item.title" 
             class="meditation-card"
             @click="goToAudio(item)">
          <div class="meditation-cover" :class="item.cover">
            <div v-if="item.badge" class="meditation-badge">{{ item.badge }}</div>
            <van-icon name="play-circle" size="24" color="#fff" class="play-icon" />
          </div>
          <div class="meditation-info">
            <div class="meditation-title">{{ item.title }}</div>
            <div class="meditation-desc">{{ item.desc }}</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 睡眠标签页 -->
    <div v-show="activeTab === 2" class="tab-content">
      <!-- 子标签 -->
      <div class="filter-tags">
        <div class="filter-tag active">📖 睡眠故事</div>
        <div class="filter-tag">🧘 助眠冥想</div>
        <div class="filter-tag">🎵 助眠声音</div>
      </div>

      <!-- Banner -->
      <div class="sleep-banner" @click="goToAudio({})">
        <div class="sleep-banner-content">
          <div class="sleep-banner-title">潮汐声音漫步</div>
          <div class="sleep-banner-desc">戴上耳机，一起去植物园声音漫步</div>
        </div>
      </div>

      <!-- 睡眠故事列表 -->
      <div class="sleep-grid">
        <div v-for="item in sleepItems" 
             :key="item.title" 
             class="sleep-card"
             @click="goToAudio(item)">
          <div class="sleep-cover" :class="item.cover" />
          <div class="sleep-info">
            <div class="sleep-title">{{ item.title }}</div>
            <div class="sleep-desc">{{ item.desc }}</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 声音标签页 -->
    <div v-show="activeTab === 3" class="tab-content">
      <!-- 筛选标签 -->
      <div class="filter-tags">
        <div class="filter-tag active">全部</div>
        <div class="filter-tag">免费</div>
        <div class="filter-tag">混音</div>
        <div class="filter-tag">旋律</div>
        <div class="filter-tag">自然</div>
        <div class="filter-tag">都市</div>
      </div>

      <!-- Banner -->
      <div class="sound-banner">
        <div class="sound-banner-content">
          <div class="sound-banner-title">潮汐声音漫步</div>
          <div class="sound-banner-desc">戴上耳机，一起去植物园声音漫步</div>
        </div>
      </div>

      <!-- 季节推荐 -->
      <div class="season-banner">
        <div class="season-icon">🌙</div>
        <div class="season-content">
          <div class="season-title">自然深睡 新年有梦</div>
          <div class="season-desc">Deep Sleep Filled With New Dreams</div>
        </div>
      </div>

      <!-- 声音网格 -->
      <div class="sound-grid">
        <div v-for="item in soundItems" 
             :key="item.title" 
             class="sound-card"
             @click="goToSound(item)">
          <div class="sound-cover" :class="item.cover">
            <div v-if="item.badge" class="sound-badge">{{ item.badge }}</div>
          </div>
          <div class="sound-info">
            <div class="sound-title">{{ item.title }}</div>
            <div class="sound-desc">{{ item.desc }}</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.tide-explore-page {
  background: #fff;
  min-height: 100vh;
  padding-bottom: 80px;
}

/* 顶部导航 */
.explore-header {
  position: sticky;
  top: 0;
  z-index: 100;
  background: #fff;
  padding: 12px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #f0f0f0;
}

.tab-navigation {
  display: flex;
  gap: 24px;
  flex: 1;
}

.tab-item {
  position: relative;
  font-size: 16px;
  color: #999;
  cursor: pointer;
  padding: 4px 0;
  transition: color 0.3s;
}

.tab-item.active {
  color: #333;
  font-weight: 600;
}

.tab-indicator {
  position: absolute;
  bottom: -8px;
  left: 50%;
  transform: translateX(-50%);
  width: 20px;
  height: 3px;
  background: #666;
  border-radius: 2px;
}

.search-icon {
  margin-left: 12px;
  flex-shrink: 0;
}

/* 内容区域 */
.tab-content {
  padding: 0 16px;
}

/* Banner */
.banner-swipe {
  margin: 16px 0;
  border-radius: 12px;
  overflow: hidden;
}

.banner {
  height: 160px;
  background: linear-gradient(135deg, #a8e6cf, #dcedc1);
  display: flex;
  align-items: flex-end;
  padding: 16px;
}

.banner-content {
  z-index: 1;
}

.banner-tag {
  font-size: 12px;
  color: #666;
  margin-bottom: 4px;
}

.banner-title {
  font-size: 24px;
  font-weight: 600;
  color: #333;
}

/* 分类区域 */
.category-section {
  margin-bottom: 32px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.section-title {
  font-size: 20px;
  font-weight: 600;
  color: #333;
  margin: 0;
  margin-bottom: 4px;
}

.section-subtitle {
  font-size: 14px;
  color: #999;
  margin: 0;
}

.more-text {
  font-size: 14px;
  color: #ff6b6b;
  flex-shrink: 0;
}

/* 横向滚动列表 */
.items-scroll {
  display: flex;
  gap: 12px;
  overflow-x: auto;
  padding-bottom: 8px;
}

.items-scroll::-webkit-scrollbar {
  display: none;
}

.item-card {
  flex-shrink: 0;
  width: 140px;
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  transition: transform 0.2s;
}

.item-card:active {
  transform: scale(0.98);
}

.item-cover {
  height: 100px;
  background: #e0e0e0;
  position: relative;
}

.item-badge {
  position: absolute;
  top: 8px;
  right: 8px;
  background: #ff6b6b;
  color: #fff;
  font-size: 10px;
  padding: 2px 6px;
  border-radius: 4px;
}

.item-info {
  padding: 12px;
}

.item-title {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  margin-bottom: 4px;
}

.item-desc {
  font-size: 12px;
  color: #999;
}

/* 声音灵感 */
.inspiration-section {
  margin-bottom: 32px;
}

.inspiration-card {
  background: linear-gradient(135deg, #ffd3b6, #ffaaa5, #a8e6cf);
  border-radius: 12px;
  padding: 16px;
  display: flex;
  align-items: center;
  gap: 16px;
  transition: transform 0.2s;
}

.inspiration-card:active {
  transform: scale(0.98);
}

.inspiration-icon {
  font-size: 32px;
}

.inspiration-content {
  flex: 1;
}

.inspiration-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 4px;
}

.inspiration-desc {
  font-size: 14px;
  color: #666;
}

/* 推荐列表 */
.recommendations-section {
  margin-bottom: 32px;
}

.recommendation-card {
  background: #f8f8f8;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 12px;
}

.rec-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.rec-avatar {
  font-size: 16px;
}

.rec-user {
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.rec-text {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
  margin-bottom: 12px;
}

.rec-audio {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px;
  background: #fff;
  border-radius: 8px;
  cursor: pointer;
}

.audio-thumb {
  width: 32px;
  height: 32px;
  background: #333;
  border-radius: 6px;
  flex-shrink: 0;
}

.audio-info {
  flex: 1;
}

.audio-title {
  font-size: 13px;
  font-weight: 500;
  color: #333;
}

.audio-type {
  font-size: 12px;
  color: #999;
}

/* 筛选标签 */
.filter-tags {
  display: flex;
  gap: 10px;
  margin: 16px 0;
  overflow-x: auto;
  padding: 4px 0;
}

.filter-tags::-webkit-scrollbar {
  display: none;
}

.filter-tag {
  flex-shrink: 0;
  padding: 8px 20px;
  background: #f5f5f5;
  border-radius: 20px;
  font-size: 14px;
  color: #666;
  cursor: pointer;
  transition: all 0.2s;
  white-space: nowrap;
}

.filter-tag:active {
  transform: scale(0.96);
}

.filter-tag.active {
  background: #7ec8e3;
  color: #fff;
}

/* 特色卡片 */
.featured-card {
  background: linear-gradient(135deg, #ffd3b6, #a8e6cf);
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.featured-content {
  flex: 1;
}

.featured-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-bottom: 4px;
}

.featured-desc {
  font-size: 14px;
  color: #666;
}

.featured-icon {
  font-size: 40px;
}

/* 冥想网格 */
.meditation-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.meditation-card {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  transition: transform 0.2s;
}

.meditation-card:active {
  transform: scale(0.98);
}

.meditation-cover {
  height: 120px;
  background: #e0e0e0;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}

.meditation-badge {
  position: absolute;
  top: 8px;
  right: 8px;
  background: #ff6b6b;
  color: #fff;
  font-size: 10px;
  padding: 2px 6px;
  border-radius: 4px;
}

.play-icon {
  opacity: 0.8;
}

.meditation-info {
  padding: 12px;
}

.meditation-title {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  margin-bottom: 4px;
}

.meditation-desc {
  font-size: 12px;
  color: #999;
}



.sleep-banner {
  background: linear-gradient(135deg, #a8e6cf, #dcedc1);
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 16px;
}

.sleep-banner-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 4px;
}

.sleep-banner-desc {
  font-size: 14px;
  color: #666;
}

.sleep-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.sleep-card {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  transition: transform 0.2s;
}

.sleep-card:active {
  transform: scale(0.98);
}

.sleep-cover {
  height: 100px;
  background: #e0e0e0;
}

.sleep-info {
  padding: 12px;
}

.sleep-title {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  margin-bottom: 4px;
}

.sleep-desc {
  font-size: 12px;
  color: #999;
}

/* 声音标签页 */
.sound-banner {
  background: linear-gradient(135deg, #a8e6cf, #dcedc1);
  border-radius: 12px;
  padding: 16px;
  margin: 16px 0;
}

.sound-banner-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 4px;
}

.sound-banner-desc {
  font-size: 14px;
  color: #666;
}

.season-banner {
  background: linear-gradient(135deg, #1a1a2e, #16213e);
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  gap: 16px;
}

.season-icon {
  font-size: 32px;
}

.season-title {
  font-size: 16px;
  font-weight: 600;
  color: #fff;
  margin-bottom: 4px;
}

.season-desc {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.7);
}

.sound-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.sound-card {
  background: #f0f0f0;
  border-radius: 12px;
  overflow: hidden;
  transition: transform 0.2s;
}

.sound-card:active {
  transform: scale(0.98);
}

.sound-cover {
  width: 100%;
  height: 100px;
  background: #e0e0e0;
  position: relative;
}

.sound-badge {
  position: absolute;
  top: 8px;
  right: 8px;
  background: #ff6b6b;
  color: #fff;
  font-size: 10px;
  padding: 2px 6px;
  border-radius: 4px;
}

.sound-info {
  padding: 12px;
}

.sound-title {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  margin-bottom: 4px;
}

.sound-desc {
  font-size: 12px;
  color: #999;
}
</style>