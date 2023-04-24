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
