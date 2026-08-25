import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import AppIcon from './components/AppIcon.vue'
import './assets/variables.css'
import './assets/main.css'
// 仅按需引入被「函数式调用」的 Vant 组件样式（Toast / Dialog）。
// 其余 Vant 组件样式由 unplugin-vue-components 的 VantResolver 在编译期自动按需注入，
// 因此无需再全量引入 'vant/lib/index.css'，可显著减小首屏 CSS 体积。
import 'vant/es/toast/style/index'
import 'vant/es/dialog/style/index'

const app = createApp(App)

app.use(createPinia())
app.use(router)
// 全局注册统一线性图标组件，全项目直接使用 <AppIcon name="..." />（无需逐文件 import）
app.component('AppIcon', AppIcon)

app.mount('#app')
