<script setup lang="ts">
import type { PasteVisibility } from '@/types/paste.ts'

defineProps<{
  visibility: PasteVisibility
}>()
</script>
<template>
  <template v-if="visibility === 'UNLISTED'">
    <i class="ti ti-eye-dotted" />
  </template>
  <template v-else-if="visibility === 'PRIVATE'">
    <i class="ti ti-lock" />
