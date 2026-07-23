<script setup lang="ts">
/**
 * FrameBasedAvatar - 逐帧精灵动画数字人组件（进阶版）
 *
 * 比 VideoAvatar 更高帧率的平滑动画。
 * 从视频帧中提取角色并制作精灵图集。
 *
 * 工作模式：
 *   1. MODE_CANVAS: 在 Canvas 上渲染精灵帧 + 口型覆盖层
 *   2. MODE_VIDEO: 回退到 VideoAvatar 的视频模式
 *
 * @author SuXia
 * @date 2026/06/01
 */
import { ref, computed, watch, onMounted, onUnmounted, nextTick } from 'vue'

const props = withDefaults(defineProps<{
  mood?: 'idle' | 'listen' | 'think' | 'answer'
  speaking?: boolean
  audioUrl?: string
}>(), {
  mood: 'idle',
  speaking: false,
})

const emit = defineEmits<{
  audioEnd: []
}>()

// ===== 状态 =====
const canvasRef = ref<HTMLCanvasElement>()
const currentMood = ref(props.mood)
const isTransitioning = ref(false)

// ===== 动效参数 =====
const bodyBob = ref(0)
const mouthOpen = ref(0)
const blinkState = ref(1)
const earLeftAngle = ref(0)
const earRightAngle = ref(0)
const tailAngle = ref(0)
const eyeLookX = ref(0)
const eyeLookY = ref(0)
const headAngle = ref(0)

let frame = 0
let animId = 0

// ===== Canvas 绘制循环 =====
function drawCharacter(ctx: CanvasRenderingContext2D, w: number, h: number) {
  ctx.clearRect(0, 0, w, h)

  const cx = w / 2
  const cy = h / 2

  // ---- 身体 ----
  ctx.save()
  ctx.translate(cx, cy + 22 + bodyBob.value)

  // 尾巴动画 — 颜色来自 Kling 视频帧分析
  ctx.save()
  ctx.translate(22, 5)
  ctx.rotate(tailAngle.value * Math.PI / 180)
  ctx.beginPath()
  ctx.moveTo(0, 0)
  ctx.quadraticCurveTo(20, -15, 25, -5)
  ctx.quadraticCurveTo(30, 5, 10, 10)
  ctx.closePath()
  ctx.fillStyle = '#ae856d'
  ctx.fill()
  ctx.strokeStyle = '#8e664f'
  ctx.lineWidth = 1
  ctx.stroke()
  ctx.restore()

  // 身体椭圆 — Kling色调（#ae856d暖棕）
  ctx.save()
  ctx.beginPath()
  ctx.ellipse(0, 0, 24, 20, 0, 0, Math.PI * 2)
  ctx.fillStyle = '#ae856d'
  ctx.fill()
  ctx.strokeStyle = '#8e664f'
  ctx.lineWidth = 1.5
  ctx.stroke()
  ctx.restore()

  // 肚子（浅色奶油区域 — Kling #f6e8ba）
  ctx.save()
  ctx.beginPath()
  ctx.ellipse(0, 6, 14, 12, 0, 0, Math.PI * 2)
  ctx.fillStyle = 'rgba(246, 232, 186, 0.25)'
  ctx.fill()
  ctx.restore()

  ctx.restore()

  // ---- 头部 ----
  ctx.save()
  ctx.translate(cx, cy - 10 + bodyBob.value * 0.5)
  ctx.rotate(headAngle.value * Math.PI / 180)

  // 左耳 — #ae856d 暖棕
  ctx.save()
  ctx.translate(-18, -20)
  ctx.rotate((-10 + earLeftAngle.value) * Math.PI / 180)
  ctx.beginPath()
  ctx.moveTo(-8, 0)
  ctx.lineTo(0, -22)
  ctx.lineTo(10, 0)
  ctx.closePath()
  ctx.fillStyle = '#ae856d'
  ctx.fill()
  ctx.strokeStyle = '#8e664f'
  ctx.lineWidth = 1
  ctx.stroke()
  // 内耳（浅粉色）
  ctx.beginPath()
  ctx.moveTo(-4, -2)
  ctx.lineTo(0, -16)
  ctx.lineTo(6, -2)
  ctx.closePath()
  ctx.fillStyle = 'rgba(218, 170, 148, 0.35)'
  ctx.fill()
  ctx.restore()

  // 右耳
  ctx.save()
  ctx.translate(18, -20)
  ctx.rotate((10 + earRightAngle.value) * Math.PI / 180)
  ctx.beginPath()
  ctx.moveTo(-10, 0)
  ctx.lineTo(0, -22)
  ctx.lineTo(8, 0)
  ctx.closePath()
  ctx.fillStyle = '#ae856d'
  ctx.fill()
  ctx.strokeStyle = '#8e664f'
  ctx.lineWidth = 1
  ctx.stroke()
  // 内耳
  ctx.beginPath()
  ctx.moveTo(-6, -2)
  ctx.lineTo(0, -16)
  ctx.lineTo(4, -2)
  ctx.closePath()
  ctx.fillStyle = 'rgba(218, 170, 148, 0.35)'
  ctx.fill()
  ctx.restore()

  // 脸部 — Kling 暖棕底色
  ctx.beginPath()
  ctx.ellipse(0, 0, 28, 24, 0, 0, Math.PI * 2)
  ctx.fillStyle = '#c99a7a'
  ctx.fill()
  ctx.strokeStyle = '#ae856d'
  ctx.lineWidth = 1
  ctx.stroke()

  // 白色脸部区域 — Kling #f6e8ba 奶油色
  ctx.beginPath()
  ctx.ellipse(0, 8, 20, 14, 0, 0, Math.PI)
  ctx.fillStyle = 'rgba(246, 232, 186, 0.35)'
  ctx.fill()

  // ---- 眼睛 ----
  const blink = blinkState.value
  // 左眼 — Kling 深色（#210d06）
  ctx.save()
  ctx.translate(-10, -2)
  ctx.scale(1, blink)
  ctx.beginPath()
  ctx.ellipse(0, 0, 5, 5.5, 0, 0, Math.PI * 2)
  ctx.fillStyle = '#210d06'
  ctx.fill()
  // 瞳孔
  ctx.beginPath()
  ctx.ellipse(eyeLookX.value, eyeLookY.value, 3, 3, 0, 0, Math.PI * 2)
  ctx.fillStyle = '#0a0300'
  ctx.fill()
  // 高光
  ctx.beginPath()
  ctx.arc(-1 + eyeLookX.value * 0.5, -1 + eyeLookY.value * 0.5, 1.8, 0, Math.PI * 2)
  ctx.fillStyle = 'rgba(255,255,255,0.9)'
  ctx.fill()
  ctx.restore()

  // 右眼
  ctx.save()
  ctx.translate(10, -2)
  ctx.scale(1, blink)
  ctx.beginPath()
  ctx.ellipse(0, 0, 5, 5.5, 0, 0, Math.PI * 2)
  ctx.fillStyle = '#210d06'
  ctx.fill()
  ctx.beginPath()
  ctx.ellipse(eyeLookX.value, eyeLookY.value, 3, 3, 0, 0, Math.PI * 2)
  ctx.fillStyle = '#0a0300'
  ctx.fill()
  ctx.beginPath()
  ctx.arc(-1 + eyeLookX.value * 0.5, -1 + eyeLookY.value * 0.5, 1.8, 0, Math.PI * 2)
  ctx.fillStyle = 'rgba(255,255,255,0.9)'
  ctx.fill()
  ctx.restore()

  // ---- 嘴巴 ----
  if (props.speaking || mouthOpen.value > 0) {
    // 说话时：椭圆形嘴巴（实时变化大小）
    const mouthH = 3 + mouthOpen.value * 8
    const mouthW = 6 + mouthOpen.value * 4
    ctx.save()
    ctx.translate(0, 8)
    ctx.scale(1, mouthOpen.value > 0.5 ? 1.2 : 1)
    ctx.beginPath()
    ctx.ellipse(0, 0, mouthW / 2, mouthH / 2, 0, 0, Math.PI * 2)
    ctx.fillStyle = '#89301f'  // Kling 视频提取的深红
    ctx.fill()
    ctx.restore()
  } else {
    // 安静时：一条弧线微笑
    ctx.save()
    ctx.translate(0, 8)
    ctx.beginPath()
    ctx.arc(0, 0, 5, 0.2, Math.PI - 0.2)
    ctx.strokeStyle = '#89301f'
    ctx.lineWidth = 2
    ctx.stroke()
    ctx.restore()
  }

  // ---- 鼻子 ----
  ctx.save()
  ctx.translate(0, 4)
  ctx.beginPath()
  ctx.ellipse(0, 0, 2, 1.5, 0, 0, Math.PI * 2)
  ctx.fillStyle = '#62413c'  // Kling 深棕
  ctx.fill()
  ctx.restore()

  // ---- 腮红 ----
  ctx.save()
  ctx.translate(0, 5)
  // 左腮红
  ctx.beginPath()
  ctx.ellipse(-13, 0, 5, 3, 0, 0, Math.PI * 2)
  ctx.fillStyle = 'rgba(218, 170, 148, 0.15)'
  ctx.fill()
  // 右腮红
  ctx.beginPath()
  ctx.ellipse(13, 0, 5, 3, 0, 0, Math.PI * 2)
  ctx.fillStyle = 'rgba(218, 170, 148, 0.15)'
  ctx.fill()
  ctx.restore()

  ctx.restore()

  // ---- 爪子 ----
  ctx.save()
  ctx.translate(cx, cy + 40 + bodyBob.value)
  // 左爪
  ctx.beginPath()
  ctx.ellipse(-12, 0, 5, 4, 0, 0, Math.PI * 2)
  ctx.fillStyle = '#ae856d'
  ctx.fill()
  // 右爪
  ctx.beginPath()
  ctx.ellipse(12, 0, 5, 4, 0, 0, Math.PI * 2)
  ctx.fillStyle = '#ae856d'
  ctx.fill()
  ctx.restore()
}

// ===== 动画循环 =====
function animate() {
  if (!canvasRef.value) return
  const canvas = canvasRef.value
  const ctx = canvas.getContext('2d')
  if (!ctx) return

  frame++

  // 呼吸浮动
  bodyBob.value = Math.sin(frame * 0.04) * 2.5

  // 眨眼（平均每 4 秒一次）
  blinkState.value = frame % 240 < 235 ? 1 : 0.05

  // 根据情绪驱动不同参数
  switch (currentMood.value) {
    case 'idle':
      // 尾巴缓慢摆动
      tailAngle.value = Math.sin(frame * 0.02) * 20
      // 耳朵随机轻微转动
      earLeftAngle.value = Math.sin(frame * 0.03) * 8
      earRightAngle.value = Math.sin(frame * 0.035 + 1) * 8
      // 眼珠轻微漂移
      eyeLookX.value = Math.sin(frame * 0.01) * 1
      eyeLookY.value = Math.sin(frame * 0.008) * 0.5
      // 头部微晃
      headAngle.value = Math.sin(frame * 0.015) * 2
      break

    case 'listen':
      // 身体前倾（头部角度偏移）
      headAngle.value = 4 + Math.sin(frame * 0.05) * 2
      // 耳朵转向左侧（认真听）
      earLeftAngle.value = -15 + Math.sin(frame * 0.04) * 5
      earRightAngle.value = -5 + Math.sin(frame * 0.04) * 3
      // 眼睛专注（不怎么动）
      eyeLookX.value = 2
      eyeLookY.value = -1
      // 尾巴微摆
      tailAngle.value = Math.sin(frame * 0.03) * 10
      break

    case 'think':
      // 歪头
      headAngle.value = 8 + Math.sin(frame * 0.06) * 3
      // 眼珠滴溜溜转
      eyeLookX.value = Math.sin(frame * 0.08) * 4
      eyeLookY.value = -1 + Math.sin(frame * 0.06) * 2
      // 尾巴卷起不动
      tailAngle.value = 5 + Math.sin(frame * 0.05) * 8
      // 耳朵竖直
      earLeftAngle.value = -5
      earRightAngle.value = 5
      break

    case 'answer':
      // 欢快弹跳
      bodyBob.value = Math.abs(Math.sin(frame * 0.12)) * 4
      // 尾巴快速摇摆
      tailAngle.value = Math.sin(frame * 0.15) * 25
      // 耳朵欢动
      earLeftAngle.value = Math.sin(frame * 0.12) * 15
      earRightAngle.value = Math.sin(frame * 0.13 + 1) * 15
      // 眼睛开心
      eyeLookX.value = Math.sin(frame * 0.1) * 2
      eyeLookY.value = -2
      // 头部跟着节奏动
      headAngle.value = Math.sin(frame * 0.08) * 5
      break
  }

  // 说话时口型开合
  if (props.speaking) {
    mouthOpen.value = 0.3 + Math.abs(Math.sin(frame * 0.15)) * 0.7
  } else if (mouthOpen.value > 0) {
    mouthOpen.value = Math.max(0, mouthOpen.value - 0.05)
  }

  // 绘制
  drawCharacter(ctx, canvas.width, canvas.height)
  animId = requestAnimationFrame(animate)
}

// ===== 状态切换 =====
watch(() => props.mood, (newMood) => {
  if (!newMood) return
  isTransitioning.value = true
  setTimeout(() => {
    currentMood.value = newMood
    isTransitioning.value = false
  }, 100)
})

// ===== 生命周期 =====
onMounted(() => {
  if (canvasRef.value) {
    canvasRef.value.width = 150
    canvasRef.value.height = 150
  }
  animId = requestAnimationFrame(animate)
})

onUnmounted(() => {
  cancelAnimationFrame(animId)
})
</script>

<template>
  <div class="avatar-wrapper" :class="`mood-${currentMood}`">
    <div class="canvas-container">
      <canvas
        ref="canvasRef"
        class="avatar-canvas"
        :class="{ transitioning: isTransitioning }"
      />
      <!-- 情绪粒子 -->
      <div class="fx-mood" v-if="currentMood === 'answer'">
        <span v-for="i in 3" :key="i" class="fx-star" :style="{ animationDelay: i * 0.15 + 's' }">✨</span>
      </div>
    </div>
    <div class="name-tag">小狐灵 🦊</div>
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

.canvas-container {
  position: relative;
  width: 150px;
  height: 150px;
}

.avatar-canvas {
  width: 150px;
  height: 150px;
  border-radius: 50%;
  transition: opacity 0.3s ease;
}

.avatar-canvas.transitioning {
  opacity: 0.7;
}

.fx-mood {
  position: absolute;
  top: 5px;
  right: 10px;
  pointer-events: none;
  z-index: 3;
}

.fx-star {
  display: inline-block;
  font-size: 14px;
  animation: fxFloat 0.6s ease-out forwards;
  margin-left: -4px;
}

@keyframes fxFloat {
  0% { opacity: 1; transform: translateY(0) scale(0.3); }
  100% { opacity: 0; transform: translateY(-20px) scale(1); }
}

.name-tag {
  font-size: 14px;
  font-weight: 600;
  margin-top: 2px;
}

/* Mood 风格 */
.mood-idle .avatar-canvas {
  filter: drop-shadow(0 2px 8px rgba(217, 157, 113, 0.15));
}
.mood-listen .avatar-canvas {
  filter: drop-shadow(0 2px 10px rgba(100, 180, 255, 0.15));
}
.mood-think .avatar-canvas {
  filter: drop-shadow(0 2px 10px rgba(255, 200, 100, 0.15));
}
.mood-answer .avatar-canvas {
  filter: drop-shadow(0 2px 10px rgba(255, 150, 200, 0.2));
}
</style>
