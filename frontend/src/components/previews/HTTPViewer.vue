<script setup lang="ts">
import { computed, defineAsyncComponent } from 'vue'
import Tag from 'primevue/tag'
const Highlighted = defineAsyncComponent(() => import('@/components/Highlighted.vue'))

const props = defineProps<{
  request: string
}>()

const parsed = computed(() => {
  const source = props.request.trim()

  if (source.startsWith('curl ')) {
    const methodMatch = source.match(/-X\s+([A-Z]+)/)
    const urlMatch = source.match(/(https?:\/\/[^\s'"]+)/)
    const headerMatches = Array.from(source.matchAll(/-H\s+['"]([^'"]+)['"]/g)).map((m) => m[1])
    return {
      kind: 'curl',
      method: methodMatch?.[1] || 'GET',
      url: urlMatch?.[1] || '',
      headers: headerMatches,
      body: source.match(/-d\s+['"]([\s\S]*?)['"]/)?.[1] || '',
    }
  }

  const [requestLine, ...rest] = source.split('\n')
  const requestMatch = requestLine.match(/^(GET|POST|PUT|PATCH|DELETE|HEAD|OPTIONS)\s+(\S+)/)
  const separatorIndex = rest.findIndex((line) => line.trim() === '')
  const headerLines = (separatorIndex === -1 ? rest : rest.slice(0, separatorIndex)).filter(Boolean)
  const bodyLines = separatorIndex === -1 ? [] : rest.slice(separatorIndex + 1)

  return {
    kind: 'http',
    method: requestMatch?.[1] || '',
    url: requestMatch?.[2] || '',
    headers: headerLines,
    body: bodyLines.join('\n'),
  }
})
</script>

<template>
  <div class="overflow-hidden text-sm">
    <div
