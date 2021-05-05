<script setup lang="ts">
import type { Folder } from '@/types/folder.ts'

defineProps<{
  folder: Folder
  disabled?: boolean
}>()
</script>
<template>
  <component
    :is="disabled ? 'div' : 'router-link'"
    :to="{ name: 'folder', params: { folder: folder.id } }"
