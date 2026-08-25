<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { showLoadingToast, closeToast } from 'vant'
import { getAudioList } from '@/api/audio'
import type { AudioItem } from '@/types/api'
import { DEFAULT_COVER } from '@/utils/constants'
import { usePersistedState } from '@/hooks/usePersistedState'

const searchQuery = ref('')
const categories = [
  { key: '', label: '全部' },
  { key: 'rain', label: '雨声' },
  { key: 'ocean', label: '海浪' },
  { key: 'forest', label: '森林' },
  { key: 'night', label: '夜晚' },
  { key: 'meditation', label: '冥想' },
  { key: 'cafe', label: '咖啡馆' }
]

const activeCategory = ref('')
const audioList = ref<AudioItem[]>([])
const loading = ref(false)

// ===== 持久化播放状态 =====
const currentPlaying = usePersistedState<AudioItem | null>('audio_currentPlaying', null)
const isPlaying = usePersistedState<boolean>('audio_isPlaying', false)

async function loadAudio() {
  const toast = showLoadingToast({
    message: '加载中...',
    forbidClick: true,
    duration: 0
  })
  try {
    loading.value = true
    const res = await getAudioList(activeCategory.value || undefined)
    audioList.value = res.data.records
  } catch {
    audioList.value = []
  } finally {
    loading.value = false
    closeToast()
  }
}

function togglePlay(audio: AudioItem) {
  if (currentPlaying.value?.id === audio.id) {
    isPlaying.value = !isPlaying.value
  } else {
    currentPlaying.value = audio
    isPlaying.value = true
  }
}

function formatCount(count: number): string {
  if (count >= 10000) {
    return (count / 10000).toFixed(1) + '万'
  }
  return String(count)
}

watch(activeCategory, () => {
  loadAudio()
})

onMounted(() => {
  loadAudio()
})
</script>

<template>
  <div class="audio-page">
    <!-- Search -->
    <van-search
      v-model="searchQuery"
      placeholder="搜索音频..."
      shape="round"
      background="#fff"
    />

    <!-- Category Tabs -->
    <van-tabs 
      v-model:active="activeCategory" 
      animated 
      sticky
      color="#666"
      title-active-color="#333"
      title-inactive-color="#999"
    >
      <van-tab
        v-for="cat in categories"
        :key="cat.key"
        :title="cat.label"
        :name="cat.key"
      />
    </van-tabs>

    <!-- Audio List -->
    <div class="audio-list">
      <div
        v-for="audio in audioList"
        :key="audio.id"
        class="audio-card"
        :class="{ 'is-active': currentPlaying?.id === audio.id }"
      >
        <van-image
          width="64"
          height="64"
          radius="12"
          :src="audio.coverUrl || DEFAULT_COVER"
        />
        <div class="audio-info">
          <div class="audio-title">{{ audio.title }}</div>
          <div class="audio-meta">
            <span class="audio-category">{{ audio.category }}</span>
            <span class="audio-count">{{ formatCount(audio.playCount) }}次播放</span>
          </div>
        </div>
        <div class="audio-action" @click="togglePlay(audio)">
          <AppIcon
            :name="currentPlaying?.id === audio.id && isPlaying ? 'pause-circle' : 'play-circle'"
            size="36"
            :color="currentPlaying?.id === audio.id ? '#666' : '#999'"
          />
        </div>
      </div>

      <van-empty v-if="!loading && audioList.length === 0" description="暂无音频" />
    </div>

    <!-- Mini Player (仅在有播放记录时显示) -->
    <div class="mini-player-fixed" v-if="currentPlaying">
      <div class="mini-player" @click="togglePlay(currentPlaying)">
        <div class="player-info">
          <div class="player-title">{{ currentPlaying.title }}</div>
          <div class="player-progress">{{ isPlaying ? '播放中' : '已暂停' }}</div>
        </div>
        <div class="player-controls">
          <AppIcon
            :name="isPlaying ? 'pause-circle' : 'play-circle'"
            size="36"
            color="#666"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.audio-page {
  min-height: 100vh;
  min-height: 100dvh;
  background: #f8f8f8;
  padding-bottom: 100px;
}

/* Audio List */
.audio-list {
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.audio-card {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 14px;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  transition: all 0.2s;
}

.audio-card:active {
  transform: scale(0.98);
}

.audio-card.is-active {
  background: #f5f0eb;
  border: 1px solid #e0d5cb;
  box-shadow: 0 2px 12px rgba(100, 80, 60, 0.12);
}

.audio-info {
  flex: 1;
  min-width: 0;
}

.audio-title {
  font-size: 15px;
  font-weight: 500;
  color: #333;
  margin-bottom: 6px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.audio-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 12px;
  color: #999;
}

.audio-action {
  flex-shrink: 0;
}

/* Mini Player */
.mini-player-fixed {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  z-index: 100;
  padding: 0 16px 16px;
  padding-bottom: calc(16px + env(safe-area-inset-bottom));
  pointer-events: none;
}

.mini-player {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  border-radius: 16px;
  background: #fff;
  border: 1px solid #e0e0e0;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  pointer-events: auto;
  cursor: pointer;
}

.player-info {
  flex: 1;
  min-width: 0;
}

.player-title {
  font-size: 15px;
  font-weight: 500;
  color: #333;
  margin-bottom: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.player-progress {
  font-size: 12px;
  color: #999;
}
</style>
