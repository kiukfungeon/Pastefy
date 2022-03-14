<script setup lang="ts">
import { computed } from 'vue'
import 'diff2html/bundles/css/diff2html.min.css'
import { html } from 'diff2html'
import { ColorSchemeType } from 'diff2html/lib/types'
import { usePreferredColorScheme } from '@vueuse/core'
import { useConfig } from '@/composables/config.ts'

const prefTheme = usePreferredColorScheme()
const config = useConfig()

const props = defineProps<{
  diff: string
  inEditor?: boolean
}>()

const renderedDiff = computed(() => {
  if (config.value && prefTheme.value) {
    // Do nothing with it, just to trigger re-computation
  }

  return html(props.diff, {
    colorScheme: document.documentElement.classList.contains('dark')
