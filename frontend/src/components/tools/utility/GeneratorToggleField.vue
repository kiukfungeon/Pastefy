<script setup lang="ts">
import Checkbox from 'primevue/checkbox'

defineProps<{
  label: string
  help?: string
}>()

const model = defineModel<boolean>({ required: true })
</script>

<template>
  <label class="flex items-start gap-2 rounded-xl border border-neutral-200 bg-white px-3 py-2 dark:border-neutral-700 dark:bg-neutral-900">
