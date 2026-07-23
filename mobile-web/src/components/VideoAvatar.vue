<script setup lang="ts">
/**
 * VideoAvatar - AI数字人视频角色组件
 *
 * 基于 Kling 视频驱动的小狐灵虚拟角色：
 * - 根据 mood 状态播放对应的动画视频片段
 * - 支持 TTS 语音播放，播放时叠加口型动画
 * - 使用 mask-image 实现圆形/圆角透明效果
 * - 平滑过渡切换
 *
 * @author SuXia
 * @date 2026/06/01
 */
import { ref, computed, watch, onMounted, onUnmounted } from 'vue'

const props = withDefaults(defineProps<{
  /** 当前情绪: idle | listen | think | answer */
  mood?: 'idle' | 'listen' | 'think' | 'answer'
  /** 是否正在说话（播放语音时） */
  speaking?: boolean
  /** 音频 URL（自动播放） */
  audioUrl?: string
  /** 说话时的文本内容（用于口型动画微调） */
  speechText?: string
}>(), {
  mood: 'idle',
  speaking: false,
})

const emit = defineEmits<{
  audioEnd: []
}>()

// ---- 视频引用 ----
const videoRef = ref<HTMLVideoElement>()
// ---- 状态 ----
const currentMood = ref(props.mood)
const isTransitioning = ref(false)
const showMouth = ref(false)
const mouthOpen = ref(0)
const earTwitch = ref(0)
const bodyBob = ref(0)
const idlePhase = ref(0)

// ---- 帧动画 ----
let frame = 0
let animFrameId: number
function animationLoop() {
  frame++
  // 呼吸浮动
  bodyBob.value = Math.sin(frame * 0.03) * 2
  // 说话时口型开合（基于音频能量模拟）
  if (props.speaking || showMouth.value) {
    mouthOpen.value = 0.3 + Math.abs(Math.sin(frame * 0.15)) * 0.7
  } else {
    mouthOpen.value = 0
  }
  // 耳朵随机转动（只在 idle 时）
  if (currentMood.value === 'idle') {
    earTwitch.value = Math.sin(frame * 0.05 + Math.sin(frame * 0.02) * 2) * 8
  }
  animFrameId = requestAnimationFrame(animationLoop)
}

// ---- 视频控制 ----
// 每个 mood 对应的视频源路径
// 优先从 assets 加载（Vite 打包优化），回退到 public 目录
const videoSrcMap: Record<string, string> = (() => {
  // Vite 会在构建时处理这些 URL
  try {
    return {
      idle:   new URL('@/assets/avatar/idle.mp4', import.meta.url).href,
      listen: new URL('@/assets/avatar/listen.mp4', import.meta.url).href,
      think:  new URL('@/assets/avatar/think.mp4', import.meta.url).href,
      answer: new URL('@/assets/avatar/answer.mp4', import.meta.url).href,
    }
  } catch {
    // fallback: 从 public 目录加载
    return {
      idle:   '/avatar/idle.mp4',
      listen: '/avatar/listen.mp4',
      think:  '/avatar/think.mp4',
      answer: '/avatar/answer.mp4',
    }
  }
})()

const currentVideoSrc = computed(() => {
  return videoSrcMap[currentMood.value] || videoSrcMap.idle
})

// 监听 mood 变化，切换视频
watch(() => props.mood, (newMood) => {
  if (!newMood) return
  isTransitioning.value = true
  setTimeout(() => {
    currentMood.value = newMood
    isTransitioning.value = false
    if (videoRef.value) {
      videoRef.value.currentTime = 0
      videoRef.value.play().catch(() => {})
    }
  }, 150)
})

// 视频循环播放
function onVideoEnded() {
  if (videoRef.value) {
    videoRef.value.currentTime = 0
    videoRef.value.play().catch(() => {})
  }
}

// ---- 音频播放 ----
const audioRef = ref<HTMLAudioElement>()

watch(() => props.audioUrl, (url) => {
  if (!url) return
  showMouth.value = true
  // 播放语音
  if (audioRef.value) {
    audioRef.value.pause()
    audioRef.value = undefined
  }
  const audio = new Audio(url)
  audioRef.value = audio
  audio.onended = () => {
    showMouth.value = false
    emit('audioEnd')
  }
  audio.play().catch(() => {
    showMouth.value = false
  })
})

onMounted(() => {
  animationLoop()
  // 预加载所有视频
  Object.values(videoSrcMap).forEach(src => {
    const link = document.createElement('link')
    link.rel = 'prefetch'
    link.href = src
    document.head.appendChild(link)
  })
})

onUnmounted(() => {
  cancelAnimationFrame(animFrameId)
  if (audioRef.value) {
    audioRef.value.pause()
  }
})

// ---- 状态文本 ----
const statusText = computed(() => {
  const map: Record<string, string> = {
    idle: '✨ 来聊吧！',
    listen: '👂 你说你说～',
    think: '💭 马上想到啦～',
    answer: '🎤 听我说～',
  }
  return map[currentMood.value] || map.idle
})
</script>

<template>
  <div class="avatar-wrapper" :class="[`mood-${currentMood}`, { speaking: showMouth }]">
    <!-- 角色舞台 -->
    <div class="stage" :style="{ transform: `translateY(${bodyBob}px)` }">
      <!-- 视频角色（圆形遮罩） -->
      <div class="video-container">
        <video
          ref="videoRef"
          :src="currentVideoSrc"
          autoplay
          muted
          loop
          playsinline
          class="avatar-video"
          @ended="onVideoEnded"
          :class="{ transitioning: isTransitioning }"
        />
        <!-- 圆形渐变遮罩 -->
        <div class="video-mask" />

        <!-- 口型动画叠加层（说话时显示） -->
        <div class="mouth-overlay" :class="{ active: showMouth && speaking }">
          <div class="mouth-shape" :style="{ transform: `scaleY(${mouthOpen})` }">
            <svg viewBox="0 0 40 20" class="mouth-svg">
              <ellipse cx="20" cy="10" rx="12" ry="6" fill="#c46a5a" :opacity="0.6 + mouthOpen * 0.4"/>
            </svg>
          </div>
        </div>

        <!-- 情绪粒子特效 -->
        <div class="fx-mood" v-if="currentMood === 'answer'">
          <span v-for="i in 3" :key="i" class="fx-star" :style="{ animationDelay: i * 0.15 + 's' }">✨</span>
        </div>
      </div>
    </div>

    <!-- 名称 + 状态文字 -->
    <div class="name-tag">小狐灵 🦊</div>
    <div class="mood-text">{{ statusText }}</div>
  </div>
</template>

<style scoped>
.avatar-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 12px 20px 2px;
  flex-shrink: 0;
  position: relative;
  z-index: 1;
  user-select: none;
}

.stage {
  position: relative;
  width: 150px;
  height: 150px;
  transition: transform 0.3s ease;
  margin-bottom: 4px;
}

/* 视频容器 */
.video-container {
  width: 140px;
  height: 140px;
  position: relative;
  margin: 5px auto;
  border-radius: 50%;
  overflow: hidden;
  box-shadow:
    0 4px 20px rgba(217, 157, 113, 0.2),
    0 0 0 2px rgba(255, 209, 150, 0.3),
    inset 0 0 30px rgba(255, 209, 150, 0.05);
  transition: box-shadow 0.4s ease;
}

.mood-listen .video-container {
  box-shadow:
    0 4px 25px rgba(100, 180, 255, 0.2),
    0 0 0 2px rgba(100, 180, 255, 0.3);
}

.mood-think .video-container {
  box-shadow:
    0 4px 25px rgba(255, 200, 100, 0.2),
    0 0 0 2px rgba(255, 200, 100, 0.3);
}

.mood-answer .video-container {
  box-shadow:
    0 4px 25px rgba(255, 150, 200, 0.25),
    0 0 0 2px rgba(255, 150, 200, 0.3);
}

.avatar-video {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: opacity 0.3s ease;
}

.avatar-video.transitioning {
  opacity: 0.5;
}

.video-mask {
  position: absolute;
  inset: 0;
  border-radius: 50%;
  background: radial-gradient(
    circle at 50% 50%,
    transparent 55%,
    rgba(255, 245, 235, 0.06) 70%,
    transparent 80%
  );
  pointer-events: none;
  z-index: 1;
  /* 柔和光晕 */
  box-shadow: inset 0 0 40px rgba(255, 209, 150, 0.08);
}

/* 口型叠加层 */
.mouth-overlay {
  position: absolute;
  bottom: 28%;
  left: 50%;
  transform: translateX(-50%);
  z-index: 2;
  opacity: 0;
  transition: opacity 0.15s ease;
  pointer-events: none;
}

.mouth-overlay.active {
  opacity: 1;
}

.mouth-shape {
  width: 28px;
  height: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: transform 0.08s ease;
}

.mouth-svg {
  width: 100%;
  height: 100%;
}

/* 情绪粒子 */
.fx-mood {
  position: absolute;
  top: -5px;
  right: 5px;
  z-index: 3;
  pointer-events: none;
}

.fx-star {
  display: inline-block;
  font-size: 14px;
  animation: fxFloat 0.6s ease-out forwards;
  margin-left: -6px;
}

@keyframes fxFloat {
  0% {
    opacity: 1;
    transform: translateY(0) scale(0.3);
  }
  100% {
    opacity: 0;
    transform: translateY(-20px) scale(1);
  }
}

/* 名称标签 */
.name-tag {
  font-size: 14px;
  font-weight: 600;
  color: var(--app-text-primary);
  line-height: 1.3;
}

/* 状态文字 */
.mood-text {
  font-size: 11px;
  color: var(--app-text-secondary);
  margin-top: 2px;
  line-height: 1.2;
}

/* --- Mood 风格动画 --- */
/* idle: 轻柔呼吸 */
.mood-idle .stage {
  animation: idleBreathe 3.5s ease-in-out infinite;
}
@keyframes idleBreathe {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-3px); }
}

/* listen: 微微前倾 */
.mood-listen .stage {
  animation: listenLean 0.8s ease-in-out infinite alternate;
}
@keyframes listenLean {
  from { transform: translate(0, 0) scale(1); }
  to { transform: translate(4px, -2px) scale(1.02); }
}

/* think: 轻轻摇晃 */
.mood-think .stage {
  animation: thinkRock 0.85s ease-in-out infinite alternate;
}
@keyframes thinkRock {
  from { transform: translate(0, 0) rotate(0deg); }
  to { transform: translate(3px, -3px) rotate(3deg); }
}

/* answer: 欢快弹跳 */
.mood-answer .stage {
  animation: answerBounce 0.5s ease-in-out infinite alternate;
}
@keyframes answerBounce {
  from { transform: translateY(0) scale(1); }
  to { transform: translateY(-5px) scale(1.03); }
}
</style>
