<script lang="ts" setup>
import { computed } from 'vue'
import type { PasteAiInfo } from '@/types/paste.ts'
import SeverityScore from '@/components/paste/SeverityScore.vue'

const props = defineProps<{
  aiInfo: PasteAiInfo
}>()

const averageSeverity = computed(() => {
  const warnings = props.aiInfo.warnings || []
  if (!warnings.length) return 0

  return Math.round(warnings.reduce((sum, warning) => sum + warning.severity, 0) / warnings.length)
})
</script>

<template>
  <div
