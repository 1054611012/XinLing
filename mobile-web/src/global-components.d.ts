import type AppIcon from './components/AppIcon.vue'

declare module 'vue' {
  interface GlobalComponents {
    AppIcon: typeof AppIcon
  }
}
