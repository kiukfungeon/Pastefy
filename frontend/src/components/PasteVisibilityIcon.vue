<script setup lang="ts">
import type { PasteVisibility } from '@/types/paste.ts'

defineProps<{
  visibility: PasteVisibility
}>()
</script>
<template>
  <template v-if="visibility === 'UNLISTED'">
