import './assets/main.css'

import { createApp, defineCustomElement, type App as VueApp, defineAsyncComponent } from 'vue'
import * as vueFunctions from 'vue'
import { createPinia } from 'pinia'
import theme from '@/theme.ts'

import App from './App.vue'
import router from './router'

import mitt, { type Emitter } from 'mitt'

import PrimeVue from 'primevue/config'
import Aura from '@primeuix/themes/aura'
import { definePreset } from '@primeuix/themes'
import axios from 'axios'
import ToastService from 'primevue/toastservice'
import Tooltip from 'primevue/tooltip'
import ConfirmationService from 'primevue/confirmationservice'
import { ViewTransitionsPlugin } from 'vue-view-transitions'
// @ts-ignore
import VAnimateCss from 'v-animate-css'

// eslint-disable-next-line
// @ts-ignore
import vue3Shortkey from 'vue3-shortkey'

import { useAppInfoStore } from '@/stores/app-info.ts'
import { useCurrentUserStore } from '@/stores/current-user.ts'
import { useConfig } from '@/composables/config.ts'
import { registerEventHandlers } from '@/utils/theme-logic.ts'
import type { Router } from 'vue-router'
import { createPlugin, plugins } from '@/plugins.ts'
import { useComponentInjectionStore } from '@/stores/component-injections.ts'
import { useAppStore } from '@/stores/app.ts'
import i18next from '@/i18n.ts'
import I18NextVue from 'i18next-vue'

declare global {
  interface Window {
    pastefy: {
      app: VueApp
      router: Router
      appInfoStore: typeof appInfoStore
      config: typeof config
      eventBus: Emitter<Events>
      createPlugin: typeof createPlugin
      client: typeof client
      componentInjections: typeof componentInjections
      vueFunctions: typeof vueFunctions
    }
    registerPastefyPlugin: { config: unknown; entrypoint: string }[]
  }
}

export const client = axios.create({
  baseURL: (import.meta.env.VITE_APP_BASE_URL as string) || undefined,
})
export const app = createApp(App)

app.use(createPinia())
app.use(router)
app.use(I18NextVue, { i18next })

