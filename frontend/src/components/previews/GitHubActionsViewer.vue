<script setup lang="ts">
import { computed, defineAsyncComponent } from 'vue'
import Tag from 'primevue/tag'
const Highlighted = defineAsyncComponent(() => import('@/components/Highlighted.vue'))

const props = defineProps<{
  workflow: string
}>()

const triggers = computed(() =>
  Array.from(props.workflow.matchAll(/^\s{0,2}([a-z_]+)\s*:/gim))
    .map((match) => match[1])
    .filter((name) => ['push', 'pull_request', 'workflow_dispatch', 'schedule'].includes(name)),
)

const jobs = computed(() =>
  Array.from(props.workflow.matchAll(/^\s{2}([a-zA-Z0-9_-]+)\s*:\s*$/gm)).map((match) => ({
    name: match[1],
  })),
)

const steps = computed(() =>
  Array.from(props.workflow.matchAll(/^\s{6,}-\s+name:\s+(.+)$/gm)).map((match) => match[1]),
)
</script>

<template>
  <div class="overflow-hidden text-sm">
    <div
      class="flex flex-col gap-4 border-b border-neutral-200 bg-neutral-50 px-4 py-4 dark:border-neutral-700 dark:bg-neutral-900/70"
    >
      <div class="flex flex-wrap items-center justify-between gap-3">
        <div>
          <div class="text-xs font-semibold tracking-[0.2em] uppercase opacity-60">
