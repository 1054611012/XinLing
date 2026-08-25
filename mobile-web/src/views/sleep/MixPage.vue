<script setup lang="ts">
import { ref } from 'vue'
import { showToast } from 'vant'
import SleepShell from './SleepShell.vue'

interface Track {
  key: string
  name: string
  desc: string
  mutedDesc: string
  volume: number
  gradient: string
  icon: string
}

const tracks = ref<Track[]>([
  {
    key: 'rain',
    name: '雨声',
    desc: '屋檐细雨 · 稳定白噪',
    mutedDesc: '已静音',
    volume: 60,
    gradient: 'linear-gradient(135deg,#818CF8,#4338CA)',
    icon: '<svg viewBox="0 0 24 24" fill="none" stroke="#fff" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 3c3.5 4 5.5 7 5.5 10a5.5 5.5 0 0 1-11 0c0-3 2-6 5.5-10Z"/></svg>'
  },
  {
    key: 'wave',
    name: '海浪',
    desc: '潮汐起伏 · 慢节律',
    mutedDesc: '已静音',
    volume: 35,
    gradient: 'linear-gradient(135deg,#38BDF8,#6366F1)',
    icon: '<svg viewBox="0 0 24 24" fill="none" stroke="#fff" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M2 9c2-2 4-2 6 0s4 2 6 0 4-2 6 0M2 15c2-2 4-2 6 0s4 2 6 0 4-2 6 0"/></svg>'
  },
  {
    key: 'forest',
    name: '森林',
    desc: '夜风松涛 · 低频',
    mutedDesc: '已静音',
    volume: 0,
    gradient: 'linear-gradient(135deg,#34D399,#4338CA)',
    icon: '<svg viewBox="0 0 24 24" fill="none" stroke="#fff" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 3 6 12h3l-3 5h12l-3-5h3L12 3Z"/><path d="M12 17v4"/></svg>'
  },
  {
    key: 'fire',
    name: '营火',
    desc: '噼啪暖意 · 低频',
    mutedDesc: '已静音',
    volume: 45,
    gradient: 'linear-gradient(135deg,#FB923C,#7C3AED)',
    icon: '<svg viewBox="0 0 24 24" fill="none" stroke="#fff" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 3c1 3-1 4-1 6a3 3 0 0 0 6 0c0-1-.5-2-1-3 2 1.5 3 4 3 6a7 7 0 0 1-14 0c0-3 2-5 4-7 .5 2 2 2 3 1Z"/></svg>'
  }
])

function saveSoundscape() {
  showToast('音景已保存')
}
</script>

<template>
  <SleepShell variant="faint">
    <div class="mix-page">
      <div class="mix-head">
        <h2>混音实验室</h2>
        <div class="s">调配属于你的安眠声景</div>
      </div>

      <div class="tracks">
        <div
          v-for="track in tracks"
          :key="track.key"
          class="track"
          :class="{ active: track.volume > 0, off: track.volume === 0 }"
        >
          <div class="ticon" :style="{ background: track.gradient }" v-html="track.icon" />
          <div class="tinfo">
            <div class="tname">{{ track.name }}</div>
            <div class="tdesc">{{ track.volume === 0 ? track.mutedDesc : track.desc }}</div>
            <div class="range" :style="{ '--p': track.volume + '%' }">
              <input type="range" min="0" max="100" step="1" v-model.number="track.volume" />
            </div>
          </div>
          <div class="tpct">{{ track.volume }}%</div>
        </div>
      </div>

      <button class="save" @click="saveSoundscape">
        <svg viewBox="0 0 24 24" fill="none" stroke="#fff" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <path d="M5 4h11l3 3v13H5z" />
          <path d="M9 4v5h6" />
          <path d="M9 14h6" />
        </svg>
        保存音景
      </button>
    </div>
  </SleepShell>
</template>

<style scoped>
.mix-page {
  display: flex;
  flex-direction: column;
  min-height: calc(100dvh - 64px - env(safe-area-inset-top, 0px) - 100px);
}
.mix-head h2 {
  font-family: var(--font-title);
  font-size: 23px;
  font-weight: 600;
  color: #fff;
}
.mix-head .s {
  color: var(--sub);
  font-size: 12.5px;
  margin-top: 7px;
}
.tracks {
  display: flex;
  flex-direction: column;
  gap: 13px;
  margin-top: 20px;
}
.track {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 13px 15px;
  border-radius: 18px;
  background: var(--card);
  border: 1px solid rgba(255, 255, 255, 0.05);
  transition: border-color 0.2s ease, box-shadow 0.2s ease, opacity 0.2s ease;
}
.track.active {
  border: 1px solid rgba(167, 139, 250, 0.6);
  box-shadow: 0 0 0 1px rgba(167, 139, 250, 0.18), 0 10px 30px rgba(124, 58, 237, 0.16);
}
.track.off {
  opacity: 0.5;
}
.ticon {
  width: 46px;
  height: 46px;
  border-radius: 50%;
  flex: 0 0 auto;
  display: flex;
  align-items: center;
  justify-content: center;
}
.ticon :deep(svg) {
  width: 23px;
  height: 23px;
}
.tinfo {
  flex: 1;
  min-width: 0;
}
.tname {
  font-size: 13.5px;
  font-weight: 600;
  color: #fff;
}
.tdesc {
  font-size: 10.5px;
  color: var(--sub);
  margin: 2px 0 9px;
}
/* 原生滑块：渐变填充 + 发光滑块，替代 Vant Slider 以规避组件/响应式问题 */
.range {
  position: relative;
  padding: 0 2px;
}
.range input {
  -webkit-appearance: none;
  appearance: none;
  width: 100%;
  height: 6px;
  margin: 0;
  border-radius: 3px;
  outline: none;
  background-color: rgba(255, 255, 255, 0.1);
  background-image: linear-gradient(90deg, #6366f1, #a78bfa);
  background-repeat: no-repeat;
  background-size: var(--p) 100%;
  cursor: pointer;
}
.range input::-webkit-slider-thumb {
  -webkit-appearance: none;
  appearance: none;
  width: 14px;
  height: 14px;
  border: none;
  border-radius: 50%;
  background: #fff;
  box-shadow: 0 0 9px rgba(167, 139, 250, 0.85);
  cursor: pointer;
}
.range input::-moz-range-thumb {
  width: 14px;
  height: 14px;
  border: none;
  border-radius: 50%;
  background: #fff;
  box-shadow: 0 0 9px rgba(167, 139, 250, 0.85);
  cursor: pointer;
}
.tpct {
  flex: 0 0 auto;
  width: 42px;
  text-align: right;
  font-size: 13px;
  font-weight: 600;
  color: var(--lilac);
  font-family: var(--font-body);
}
.track.off .tpct {
  color: var(--sub);
}
.save {
  margin-top: 22px;
  background: linear-gradient(90deg, var(--purple), var(--indigo2));
  border: none;
  color: #fff;
  font-family: var(--font-body);
  font-weight: 600;
  font-size: 14.5px;
  padding: 15px;
  border-radius: 16px;
  letter-spacing: 0.5px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  box-shadow: 0 12px 30px rgba(124, 58, 237, 0.35);
  cursor: pointer;
  -webkit-tap-highlight-color: transparent;
}
.save svg {
  width: 18px;
  height: 18px;
}
.save:active {
  transform: scale(0.98);
}
</style>
