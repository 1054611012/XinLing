<script setup lang="ts">
import { computed } from 'vue'

const props = withDefaults(
  defineProps<{
    name: string
    size?: number | string
    color?: string
  }>(),
  { size: 24, color: 'currentColor' }
)

// 统一线性图标库（24x24，stroke 风格），name 与 Vant 原图标名保持一致以便无缝替换
const ICONS: Record<string, string> = {
  arrow: '<path d="M5 12h13"/><path d="M12 6l6 6-6 6"/>',
  'arrow-down': '<path d="M12 5v13"/><path d="M6 12l6 6 6-6"/>',
  'arrow-left': '<path d="M19 12H6"/><path d="M11 6l-6 6 6 6"/>',
  'wap-home-o': '<path d="M4 11l8-7 8 7"/><path d="M6 10v9h12v-9"/><path d="M10 19v-5h4v5"/>',
  'compass-o': '<circle cx="12" cy="12" r="9"/><path d="M15.5 8.5l-2 5-5 2 2-5z"/>',
  'contact-o': '<circle cx="12" cy="8" r="4"/><path d="M5 20a7 7 0 0114 0"/>',
  contact: '<circle cx="12" cy="8" r="4"/><path d="M5 20a7 7 0 0114 0"/>',
  success: '<circle cx="12" cy="12" r="9"/><path d="M8 12l3 3 5-6"/>',
  'info-o': '<circle cx="12" cy="12" r="9"/><path d="M12 11v5"/><circle cx="12" cy="8" r="0.7" fill="currentColor" stroke="none"/>',
  edit: '<path d="M4 20l4-1L19 8l-3-3L5 16z"/><path d="M14 6l3 3"/>',
  'user-circle-o': '<circle cx="12" cy="12" r="9"/><circle cx="12" cy="10" r="3"/><path d="M6 18a6 6 0 0112 0"/>',
  'setting-o': '<circle cx="12" cy="12" r="3"/><path d="M12 3v3M12 18v3M3 12h3M18 12h3M5.6 5.6l2.1 2.1M16.3 16.3l2.1 2.1M18.4 5.6l-2.1 2.1M7.7 16.3l-2.1 2.1"/>',
  'desktop-o': '<rect x="3" y="5" width="18" height="11" rx="1"/><path d="M8 20h8M12 16v4"/>',
  'bar-chart-o': '<path d="M6 20V11M12 20V5M18 20v-8"/>',
  ascending: '<path d="M4 18l5-5 4 4 7-8"/><path d="M15 5h5v5"/>',
  'medal-o': '<path d="M12 3l3 5-3 2-3-2z"/><path d="M12 10v8"/><path d="M7 13l-2 7 7-3 7 3-2-7"/>',
  'vip-card-o': '<rect x="3" y="7" width="18" height="10" rx="1.5"/><path d="M3 10h18"/><path d="M7 14h4"/>',
  'orders-o': '<path d="M6 3h9l3 3v15H6z"/><path d="M9 8h6M9 12h6M9 16h4"/>',
  'bell-o': '<path d="M6 9a6 6 0 0112 0c0 5 2 6 2 6H4s2-1 2-6"/><path d="M10 20a2 2 0 004 0"/>',
  'chat-o': '<path d="M4 5h16v10H9l-4 4V5z"/>',
  photograph: '<rect x="4" y="5" width="16" height="14" rx="1.5"/><path d="M4 15l4-4 4 4 3-3 5 5"/><circle cx="9" cy="9" r="1.5"/>',
  'eye-o': '<path d="M2 12s4-7 10-7 10 7 10 7-4 7-10 7-10-7-10-7z"/><circle cx="12" cy="12" r="3"/>',
  'lock-o': '<rect x="6" y="11" width="12" height="9" rx="1.5"/><path d="M8 11V8a4 4 0 018 0v3"/>',
  'star-o': '<path d="M12 3l2.7 5.5 6 .9-4.3 4.2 1 6-5.4-2.8-5.4 2.8 1-6L3.3 9.4l6-.9z"/>',
  'clock-o': '<circle cx="12" cy="12" r="9"/><path d="M12 7v5l3 2"/>',
  'cloud-o': '<path d="M7 18a4 4 0 010-8 5 5 0 019.5-1.5A4 4 0 0117 18z"/>',
  'water-o': '<path d="M12 3s6 6 6 11a6 6 0 01-12 0c0-5 6-11 6-11z"/>',
  'volume-o': '<path d="M4 9h3l4-3v12l-4-3H4z"/><path d="M14 9a3 3 0 010 6"/><path d="M16.5 7a6 6 0 010 10"/>',
  'bulb-o': '<path d="M9 18h6M10 21h4"/><path d="M12 3a6 6 0 00-4 10c1 1 1 2 1 3h6c0-1 0-2 1-3a6 6 0 00-4-10z"/>',
  pause: '<path d="M9 5v14M15 5v14"/>',
  stop: '<rect x="6" y="6" width="12" height="12" rx="1.5"/>',
  play: '<path d="M8 5v14l11-7z"/>',
  search: '<circle cx="11" cy="11" r="7"/><path d="M20 20l-4-4"/>',
  'play-circle': '<circle cx="12" cy="12" r="9"/><path d="M10 9l5 3-5 3z"/>',
  'pause-circle': '<circle cx="12" cy="12" r="9"/><path d="M10 9v6M14 9v6"/>',
  'like-o': '<path d="M12 20s-7-4.5-9-9c-1.5-3 1-6 4-5 1.5.5 2.5 2 2.5 2 .5-.5 1.5-1.5 2.5-2 3-1 5.5 2 9 9z"/>',
  'award-o': '<path d="M7 4h10v4a5 5 0 01-10 0z"/><path d="M9 13l3 7 3-7"/>',
  android: '<path d="M6 10a4 4 0 018 0v4a2 2 0 01-2 2H8a2 2 0 01-2-2z"/><path d="M9 6L7 4M15 6l2-2M9 13v1M15 13v1"/>',
  apple: '<path d="M13 5c2 0 3 1 3 3 0 2-1 3-1 4 1 1 2 3 2 5 0 3-2 5-5 5-2 0-3-1-4-1-1 0-2 1-4 1-3 0-5-2-5-5 0-3 2-5 3-6-1-2 0-4 3-5 1-1 3-1 4 1z"/>',
  'phone-o': '<rect x="7" y="3" width="10" height="18" rx="2"/><path d="M11 18h2"/>',
  'apps-o': '<circle cx="6" cy="6" r="1.3"/><circle cx="12" cy="6" r="1.3"/><circle cx="18" cy="6" r="1.3"/><circle cx="6" cy="12" r="1.3"/><circle cx="12" cy="12" r="1.3"/><circle cx="18" cy="12" r="1.3"/><circle cx="6" cy="18" r="1.3"/><circle cx="12" cy="18" r="1.3"/><circle cx="18" cy="18" r="1.3"/>',
  quotes: '<path d="M9 7c-2.5 0-4 1.8-4 4 0 2 1.5 3.5 3.5 3.5.3 0 .5 0 .5-.3 0-1.5-1-2.5-2.3-2.5C8 12.2 9 11 9 9.5 9 8 8 7 9 7z"/><path d="M19 7c-2.5 0-4 1.8-4 4 0 2 1.5 3.5 3.5 3.5.3 0 .5 0 .5-.3 0-1.5-1-2.5-2.3-2.5C18 12.2 19 11 19 9.5 19 8 18 7 19 7z"/>',
  focus: '<circle cx="12" cy="12" r="8.5"/><circle cx="12" cy="12" r="2.2"/>',
  sleep: '<path d="M20.5 13.2A8 8 0 1 1 10.8 3.5a6.2 6.2 0 0 0 9.7 9.7z"/>',
  nap: '<path d="M5 8h12v4.5A4.5 4.5 0 0 1 12.5 17h-3A4.5 4.5 0 0 1 5 12.5z"/><path d="M17 9.5h2.2A2.3 2.3 0 0 1 19.2 14H17"/><path d="M8 3.5c0 1-1 1.2-1.6 2M11.5 3.5c0 1-1 1.2-1.6 2"/>',
  breathe: '<path d="M3 12c2.2-3.5 4.3-3.5 6.5 0s4.3 3.5 6.5 0 4.3-3.5 4.5-1.5"/><path d="M3 7.5c2.2-3.5 4.3-3.5 6.5 0"/>',
}

const inner = computed(() => ICONS[props.name] ?? '<circle cx="12" cy="12" r="5"/>')
const sizePx = computed(() => (typeof props.size === 'number' ? props.size + 'px' : props.size))
</script>

<template>
  <svg
    class="app-icon"
    :width="sizePx"
    :height="sizePx"
    viewBox="0 0 24 24"
    fill="none"
    :style="{ color }"
    xmlns="http://www.w3.org/2000/svg"
    aria-hidden="true"
  >
    <g
      v-html="inner"
      fill="none"
      stroke="currentColor"
      stroke-width="1.6"
      stroke-linecap="round"
      stroke-linejoin="round"
    />
  </svg>
</template>

<style scoped>
.app-icon {
  display: inline-block;
  vertical-align: middle;
  flex-shrink: 0;
}
</style>
