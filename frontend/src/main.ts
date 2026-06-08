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
