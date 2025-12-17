import { defineConfig } from 'vitest/config'
import vue from '@vitejs/plugin-vue'
import vuetify from 'vite-plugin-vuetify'

export default defineConfig({
  plugins: [
    vue(),
    vuetify({ autoImport: true })
  ],
  test: {
    globals: true,
    environment: 'happy-dom',
    setupFiles: ['./src/test/setup.js'],
    css: false,
    server: {
      deps: {
        inline: ['vuetify']
      }
    }
  },
  resolve: {
    alias: {
      '~': '/src'
    }
  }
})

