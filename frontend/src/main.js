import { createApp } from 'vue'
import App from './App.vue'
import { createVuetify } from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'
import 'vuetify/styles'
import '@mdi/font/css/materialdesignicons.css'

const vuetify = createVuetify({
  components,
  directives,
  theme: {
    defaultTheme: 'customLight',
    themes: {
      customLight: {
        dark: false,
        colors: {
          primary: '#4749ff',      // Custom blue/purple
          secondary: '#5f6368',    // Gray for secondary actions
          success: '#10b981',      // Green for migrated status (or use '#14b8a6' for teal)
          warning: '#ff7773',      // Orange/amber for rollback
          error: '#ef4444',        // Red for errors
          info: '#3b82f6',         // Blue for info
          background: '#f9fafc',   // Light background
          surface: '#ffffff',      // White for cards
        }
      }
    }
  }
})

const app = createApp(App)
app.use(vuetify)
app.mount('#app')

