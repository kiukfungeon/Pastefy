<script setup lang="ts">
defineProps<{
  top: string | number | undefined
  description: string
}>()
</script>
<template>
  <div
    class="flex flex-col gap-2 rounded-xl border border-neutral-300 bg-neutral-50 p-3 dark:border-neutral-700 dark:bg-neutral-800"
