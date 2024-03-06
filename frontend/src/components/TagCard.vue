<script setup lang="ts">
import type { Tag } from '@/types/tags.ts'

defineProps<{
  tag: Tag
  small?: boolean
  selected?: boolean
}>()
</script>
<template>
  <router-link
    :to="{ name: 'tag', params: { tag: tag.tag } }"
    class="flex w-full flex-col justify-between overflow-hidden rounded-lg border"
    :class="
      selected
        ? 'border-neutral-300 bg-neutral-200 hover:bg-neutral-300 dark:border-neutral-600 dark:bg-neutral-700 dark:hover:bg-neutral-600'
        : 'border-neutral-200 bg-neutral-100 hover:bg-neutral-200 dark:border-neutral-700 dark:bg-neutral-800 dark:hover:bg-neutral-700'
    "
  >
    <div class="flex items-center gap-1 p-2 pl-3" :class="small ? 'p-1 text-sm' : ''">
      <i v-if="'icon' in tag" :class="`ti ti-${tag.icon}`" />
      <span>{{ tag.display_name || tag.tag }}</span>
    </div>

    <div class="flex justify-end">
      <img
