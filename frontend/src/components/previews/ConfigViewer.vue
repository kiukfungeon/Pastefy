<script setup lang="ts">
import { computed, defineAsyncComponent, ref } from 'vue'
import { useClipboard } from '@vueuse/core'
import Button from 'primevue/button'
import InputText from 'primevue/inputtext'
import Tag from 'primevue/tag'
const Highlighted = defineAsyncComponent(() => import('@/components/Highlighted.vue'))

const props = defineProps<{
  contents: string
  fileName?: string
  type: 'yaml' | 'toml' | 'properties' | 'ini'
}>()

const clipboard = useClipboard()
const search = ref('')

const lines = computed(() =>
  props.contents.split('\n').map((line, index) => {
    const trimmed = line.trim()
    const commentPrefixes =
      props.type === 'ini' || props.type === 'properties' ? ['#', ';'] : ['#']

    const isComment = commentPrefixes.some((prefix) => trimmed.startsWith(prefix))
    const isSection =
      props.type === 'ini'
        ? /^\[[^\]]+\]$/.test(trimmed)
        : props.type === 'toml'
          ? /^\[\[?.+\]?\]$/.test(trimmed)
          : false

    const separatorIndex = trimmed.indexOf('=')
    const yamlSeparatorIndex = trimmed.indexOf(':')

    let key = ''
    let value = ''

    if (!isComment && !isSection && separatorIndex > -1) {
      key = trimmed.slice(0, separatorIndex).trim()
      value = trimmed.slice(separatorIndex + 1).trim()
    } else if (!isComment && !isSection && props.type === 'yaml' && yamlSeparatorIndex > -1) {
      key = trimmed.slice(0, yamlSeparatorIndex).trim()
      value = trimmed.slice(yamlSeparatorIndex + 1).trim()
    }

    return {
      index,
      line,
      trimmed,
      key,
      value,
      isComment,
