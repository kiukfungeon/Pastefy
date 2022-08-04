<script setup lang="ts">
import type { PasteCommentMarker } from '@/types/paste-comment.ts'

defineProps<{
  marker: PasteCommentMarker
}>()
</script>

<template>
  <span class="flex items-center pl-1">
    <span
      v-for="profile of marker.profiles"
      :key="profile.id"
