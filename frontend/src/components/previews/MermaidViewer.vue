<script setup lang="ts">
import mermaid from 'mermaid'
import Button from 'primevue/button'
import { onMounted, ref, watch } from 'vue'
import { useDebounceFn } from '@vueuse/core'
import { useConfig } from '@/composables/config.ts'

const props = defineProps<{
  mermaidCode: string
}>()

const config = useConfig()

const getBaseConfig = () => ({
  startOnLoad: false,
  fontFamily: "'Plus Jakarta Sans Variable', sans-serif",
  theme: (
    config.value.theme === 'dark'
      ? 'dark'
      : config.value.theme === 'system'
        ? window.matchMedia('(prefers-color-scheme: dark)').matches
          ? 'dark'
          : 'default'
        : 'default'
  ) as 'dark' | 'default',
})

const resolveMermaidInput = (raw: string) => {
  const directiveMatch = raw.match(/^%%\{init:\s*([\s\S]*?)\s*\}%%\s*/)
  if (!directiveMatch) {
    return {
      code: raw,
      init: undefined as Record<string, unknown> | undefined,
      error: '',
    }
  }

  try {
    return {
      code: raw.slice(directiveMatch[0].length).trimStart(),
      init: JSON.parse(directiveMatch[1]) as Record<string, unknown>,
      error: '',
    }
