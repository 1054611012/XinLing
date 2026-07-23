<template>
  <div class="animated-bg">
    <!-- Base gradient layers -->
    <div class="bg-layer bg-gradient-base" />
    <div class="bg-layer bg-gradient-overlay" />
    <div class="bg-layer bg-gradient-glow" />

    <!-- Floating particles (CSS only, no canvas overhead) -->
    <div
      v-for="i in particleCount"
      :key="i"
      class="particle"
      :style="getParticleStyle(i)"
    />
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

interface Props {
  intensity?: 'light' | 'medium' | 'heavy'
}

const props = withDefaults(defineProps<Props>(), {
  intensity: 'medium'
})

const particleCount = computed(() => {
  switch (props.intensity) {
    case 'light': return 15
    case 'heavy': return 35
    default: return 24
  }
})

const particles = computed(() => {
  return Array.from({ length: particleCount.value }, (_, i) => i)
})

function getParticleStyle(i: number) {
  const seed = i * 7.3 + 13.7
  const top = ((seed * 1.1) % 1) * 100
  const left = ((seed * 0.7 + 0.3) % 1) * 100
  const size = 2 + ((seed * 3.1) % 4)
  const delay = -((seed * 2.3) % 6)
  const duration = 4 + ((seed * 1.7) % 4)
  const opacity = 0.15 + ((seed * 0.5) % 0.35)

  return {
    top: `${top}%`,
    left: `${left}%`,
    width: `${size}px`,
    height: `${size}px`,
    opacity,
    animationDelay: `${delay}s`,
    animationDuration: `${duration}s`
  }
}
</script>

<style scoped>
.animated-bg {
  position: fixed;
  inset: 0;
  z-index: 0;
  overflow: hidden;
}

.bg-layer {
  position: absolute;
  inset: -50%;
  transition: opacity 0.8s ease;
}

.bg-gradient-base {
  background: linear-gradient(
    135deg,
    #0a0a2e 0%,
    #1a0a3e 25%,
    #0a0a1a 50%,
    #0a0a3e 75%,
    #0a0a2e 100%
  );
  animation: bg-drift 20s ease-in-out infinite alternate;
}

.bg-gradient-overlay {
  background: radial-gradient(
    ellipse at 30% 20%,
    rgba(124, 92, 255, 0.12) 0%,
    transparent 60%
  );
  animation: glow-pulse 8s ease-in-out infinite alternate;
}

.bg-gradient-glow {
  background: radial-gradient(
    ellipse at 70% 80%,
    rgba(0, 200, 150, 0.08) 0%,
    transparent 50%
  );
  animation: glow-pulse 10s ease-in-out infinite alternate-reverse;
}

@keyframes bg-drift {
  0% { transform: translate(0, 0) rotate(0deg); }
  100% { transform: translate(2%, 1%) rotate(2deg); }
}

@keyframes glow-pulse {
  0% { opacity: 0.6; }
  100% { opacity: 1; }
}

.particle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.6);
  animation: float linear infinite;
  pointer-events: none;
}

@keyframes float {
  0% {
    transform: translateY(0) translateX(0) scale(1);
    opacity: 0;
  }
  10% {
    opacity: 1;
  }
  90% {
    opacity: 1;
  }
  100% {
    transform: translateY(-100vh) translateX(30px) scale(0.5);
    opacity: 0;
  }
}
</style>
