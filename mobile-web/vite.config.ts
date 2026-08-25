import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { VantResolver } from '@vant/auto-import-resolver'
import viteCompression from 'vite-plugin-compression2'
import { resolve } from 'path'

export default defineConfig({
  plugins: [
    vue(),
    AutoImport({
      resolvers: [VantResolver()]
    }),
    Components({
      resolvers: [VantResolver()]
    }),
    // 生成 .gz / .br 压缩产物，配合支持静态压缩的服务器进一步减小传输体积
    viteCompression({ algorithm: 'gzip' }),
    viteCompression({ algorithm: 'brotliCompress' })
  ],
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src')
    }
  },
  server: {
    port: 5173,
    host: '0.0.0.0',
    proxy: {
      '/api': {
        target: 'http://localhost:9080',
        changeOrigin: true
      },
      '/uploads': {
        target: 'http://localhost:9080',
        changeOrigin: true
      }
    }
  },
  build: {
    // 生产环境不生成 sourcemap，减小产物体积并避免源码泄露
    sourcemap: false,
    // 小于 4KB 的资源内联为 base64，减少请求数
    assetsInlineLimit: 4096,
    // 提高分包体积告警阈值，避免噪音
    chunkSizeWarningLimit: 1500,
    rollupOptions: {
      output: {
        // 仅把「所有页面都依赖的核心框架」归并为稳定 vendor 块，利于浏览器长缓存；
        // Vant / axios 等保持 Vite 默认的按需拆分，避免单次加载整库导致首屏变大
        manualChunks(id) {
          if (id.includes('node_modules')) {
            if (
              id.includes('/vue/') ||
              id.includes('/@vue/') ||
              id.includes('/vue-router/') ||
              id.includes('/pinia/')
            ) {
              return 'vue-vendor'
            }
          }
        }
      }
    }
  }
})
