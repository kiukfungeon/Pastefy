<script setup lang="ts">
import { computed, ref } from 'vue'
import InputText from 'primevue/inputtext'
import Select from 'primevue/select'
import Tag from 'primevue/tag'

const props = defineProps<{
  logs: string
}>()

const search = ref('')
const levelFilter = ref<'ALL' | 'ERROR' | 'WARN' | 'INFO' | 'DEBUG'>('ALL')

const entries = computed(() =>
  props.logs.split('\n').map((line, index) => {
    const levelMatch = line.match(/\b(ERROR|WARN|WARNING|INFO|DEBUG|TRACE|FATAL)\b/i)
    const level = (levelMatch?.[1] || 'RAW').toUpperCase().replace('WARNING', 'WARN')
    return { index, line, level }
  }),
)

const filteredEntries = computed(() => {
  return entries.value.filter((entry) => {
    if (levelFilter.value !== 'ALL' && entry.level !== levelFilter.value) return false
    if (search.value.trim() && !entry.line.toLowerCase().includes(search.value.trim().toLowerCase()))
      return false
    return true
  })
})

const stats = computed(() => {
  const count = (level: string) => entries.value.filter((entry) => entry.level === level).length
  return {
    error: count('ERROR') + count('FATAL'),
    warn: count('WARN'),
    info: count('INFO'),
    debug: count('DEBUG') + count('TRACE'),
  }
})

const levelOptions = ['ALL', 'ERROR', 'WARN', 'INFO', 'DEBUG']

const levelClass = (level: string) => {
  if (level === 'ERROR' || level === 'FATAL') return 'text-red-600 dark:text-red-300'
  if (level === 'WARN') return 'text-amber-600 dark:text-amber-300'
  if (level === 'INFO') return 'text-sky-600 dark:text-sky-300'
  if (level === 'DEBUG' || level === 'TRACE') return 'text-emerald-600 dark:text-emerald-300'
  return 'text-neutral-500 dark:text-neutral-400'
}
</script>

<template>
  <div class="overflow-hidden text-sm">
    <div
      class="flex flex-col gap-4 border-b border-neutral-200 bg-neutral-50 px-4 py-4 dark:border-neutral-700 dark:bg-neutral-900/70"
    >
      <div class="flex flex-wrap items-center justify-between gap-3">
        <div>
          <div class="text-xs font-semibold tracking-[0.2em] uppercase opacity-60">{{ $t('previews.logViewer.logView') }}</div>
