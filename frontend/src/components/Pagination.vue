<script setup lang="ts">
import Button from 'primevue/button'

defineProps<{
  loading?: boolean
  nextDisabled?: boolean
}>()

const page = defineModel<number>('page', {
  required: true,
})

const next = () => {
  page.value++
}
const previous = () => {
  page.value--
}
</script>
<template>
  <div class="flex items-center justify-between gap-3">
    <div class="flex items-center gap-2 text-sm text-neutral-500 dark:text-neutral-400">
