<script setup lang="ts">
import { computed, defineAsyncComponent } from 'vue'
import Tag from 'primevue/tag'
const Highlighted = defineAsyncComponent(() => import('@/components/Highlighted.vue'))

const props = defineProps<{
  playbook: string
}>()

const hosts = computed(() =>
  Array.from(props.playbook.matchAll(/^\s*hosts\s*:\s*(.+)$/gm)).map((match) => match[1].trim()),
)
const roles = computed(() =>
  Array.from(props.playbook.matchAll(/^\s*-\s*role\s*:\s*(.+)$/gm)).map((match) => match[1].trim()),
)
const tasks = computed(() =>
  Array.from(props.playbook.matchAll(/^\s*-\s*name\s*:\s*(.+)$/gm)).map((match) => match[1].trim()),
)
</script>

<template>
  <div class="overflow-hidden text-sm">
    <div
      class="flex flex-col gap-4 border-b border-neutral-200 bg-neutral-50 px-4 py-4 dark:border-neutral-700 dark:bg-neutral-900/70"
    >
      <div class="flex flex-wrap items-center justify-between gap-3">
        <div>
          <div class="text-xs font-semibold tracking-[0.2em] uppercase opacity-60">{{ $t('previews.ansibleViewer.ansibleView') }}</div>
          <div class="mt-1 text-xs opacity-60">{{ $t('previews.ansibleViewer.hostsRolesAndNamedTasksForOperationalPlaybooks') }}</div>
        </div>
