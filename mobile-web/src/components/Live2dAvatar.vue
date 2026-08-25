<script setup lang="ts">
/**
 * Live2dAvatar - Live2D 数字人角色组件（未来升级）
 *
 * 在拥有 Live2D 模型后，用此组件替换 VideoAvatar。
 * 模型要求：
 *   需要从 Kling 视频提取角色设计图，
 *   在 Live2D Cubism Editor 中拆分部件并绑定。
 *
 * 前置条件：
 *   yarn add pixi-live2d-display pixi.js@^7
 *   将 .model3.json + .moc3 + 纹理文件放入 public/live2d/fox/
 *
 * @author SuXia
 * @date 2026/06/01
 */
import { ref, computed, watch, onMounted, onUnmounted } from 'vue'

// 占位：Live2D 逻辑见下方注释块；此处声明模板所需的 canvas 引用，避免类型报错
const canvasRef = ref<HTMLCanvasElement>()

/*
// ====== 取消注释以下代码即可使用 ======

import * as PIXI from 'pixi.js'
import { Live2DModel } from 'pixi-live2d-display'
import { MOOD_TO_LIVE2D, computeLipSyncFromAudio } from '@/services/live2d/Live2dAnimationController'
import type { AvatarMood } from '@/services/live2d/Live2dAnimationController'

// --- Props ---
const props = withDefaults(defineProps<{
  mood?: AvatarMood
  speaking?: boolean
  audioUrl?: string
}>(), {
  mood: 'idle',
  speaking: false,
})

const emit = defineEmits<{
  audioEnd: []
}>()

// --- Live2D ---
const canvasRef = ref<HTMLCanvasElement>()
let app: PIXI.Application
let model: Live2DModel

// --- Audio analyser for lip sync ---
let audioCtx: AudioContext
let analyser: AnalyserNode
let source: MediaElementAudioSourceNode | null = null

// --- Init PIXI + Live2D ---
onMounted(async () => {
  if (!canvasRef.value) return

  // Create PIXI application
  app = new PIXI.Application({
    view: canvasRef.value,
    width: 300,
    height: 300,
    transparent: true,
    antialias: true,
    resolution: 2,
  })

  try {
    // Load Live2D model (replace path with your model)
    model = await Live2DModel.from('/live2d/fox/fox.model3.json')
    model.anchor.set(0.5, 0.5)
    model.position.set(app.screen.width / 2, app.screen.height / 2 + 30)
    model.scale.set(0.5)
    app.stage.addChild(model)

    // Start idle animation
    applyMood('idle')
  } catch (e) {
    console.error('Live2D model load failed:', e)
  }
})

onUnmounted(() => {
  if (audioCtx) audioCtx.close()
  if (app) app.destroy(true)
})

// --- Mood change ---
function applyMood(mood: AvatarMood) {
  if (!model) return
  const expression = MOOD_TO_LIVE2D[mood]
  if (expression.motion) {
    model.motion(expression.motion)
  }
  if (expression.expression) {
    model.expression(expression.expression)
  }
  if (expression.parameters) {
    Object.entries(expression.parameters).forEach(([key, value]) => {
      try { model.internalModel.coreModel.setParameterValueById(key, value) } catch {}
    })
  }
}

watch(() => props.mood, (m) => {
  if (m) applyMood(m)
})

// --- Audio playback + lip sync ---
let audioEl: HTMLAudioElement | null = null
let animationFrame = 0

function startLipSync(url: string) {
  stopLipSync()

  if (!audioCtx) {
    audioCtx = new AudioContext()
    analyser = audioCtx.createAnalyser()
    analyser.fftSize = 256
  }

  audioEl = new Audio(url)
  source = audioCtx.createMediaElementSource(audioEl)
  source.connect(analyser)
  analyser.connect(audioCtx.destination)
  audioEl.play()

  function syncLoop() {
    if (!model || !audioEl || audioEl.paused) return
    const params = computeLipSyncFromAudio(analyser)
    model.internalModel.coreModel.setParameterValueById('ParamMouthOpenY', params.mouthOpen)
    model.internalModel.coreModel.setParameterValueById('ParamMouthForm', params.mouthForm)
    animationFrame = requestAnimationFrame(syncLoop)
  }
  syncLoop()

  audioEl.onended = () => {
    stopLipSync()
    emit('audioEnd')
  }
}

function stopLipSync() {
  if (audioEl) { audioEl.pause(); audioEl = null }
  cancelAnimationFrame(animationFrame)
  // Reset mouth
  if (model) {
    model.internalModel.coreModel.setParameterValueById('ParamMouthOpenY', 0)
  }
}

watch(() => props.audioUrl, (url) => {
  if (url) startLipSync(url)
})

watch(() => props.speaking, (v) => {
  if (!v) stopLipSync()
})
*/

</script>

<template>
  <!--
  使用示例：
  <Live2dAvatar
    :mood="mood"
    :speaking="speaking"
    :audio-url="currentAudioUrl"
    @audio-end="onAudioEnd"
  />
  -->
  <div class="live2d-wrapper">
    <canvas ref="canvasRef" class="live2d-canvas" />
    <div class="placeholder-text" v-if="!canvasRef">
      🦊 Live2D 模型就绪后自动激活
    </div>
  </div>
</template>

<style scoped>
.live2d-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 12px 20px 2px;
  flex-shrink: 0;
  position: relative;
  z-index: 1;
}

.live2d-canvas {
  width: 150px;
  height: 150px;
  border-radius: 50%;
}

.placeholder-text {
  position: absolute;
  bottom: 30px;
  font-size: 12px;
  color: var(--app-text-secondary);
  opacity: 0.6;
}
</style>
