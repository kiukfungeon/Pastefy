import { useConfig } from '@/composables/config.ts'
import { watch } from 'vue'
import { useFavicon } from '@vueuse/core'

export type SelectedTheme = 'light' | 'dark' | 'system'

const config = useConfig()

export function toggleTheme() {
  let darkClass = false
  if (config.value.theme === 'system') {
