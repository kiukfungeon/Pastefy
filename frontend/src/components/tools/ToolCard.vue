<script setup lang="ts">
import ToolPinButton from '@/components/tools/ToolPinButton.vue'
import type { ToolCatalogEntry } from '@/utils/tool-categories.ts'

defineProps<{
  tool: ToolCatalogEntry
}>()
</script>

<template>
  <router-link
    :to="tool.to"
    class="group flex h-full flex-col justify-between rounded-xl border border-neutral-200 bg-neutral-100 p-5 transition-all hover:-translate-y-0.5 hover:bg-white dark:border-neutral-700 dark:bg-neutral-800 dark:hover:bg-neutral-900"
  >
    <div class="flex flex-col gap-4">
      <div class="flex items-start justify-between gap-3">
        <div class="flex items-center gap-3">
          <div
            class="flex h-12 w-12 items-center justify-center rounded-xl bg-white text-neutral-700 dark:bg-neutral-900 dark:text-neutral-200"
          >
            <i :class="`ti ti-${tool.icon} text-2xl`" />
          </div>
          <div>
            <h3 class="font-semibold">{{ tool.shortTitle }}</h3>
            <p class="text-xs uppercase tracking-[0.2em] text-neutral-400">
