<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{
  csv: string
}>()

const output = computed(() => props.csv.split('\n').map((row) => row.split(',')))

const maxCols = computed(() => {
  return Math.max(...output.value.map((row) => row.length))
})
</script>
<template>
  <div class="overflow-hidden text-sm">
    <div class="m-[-1px] overflow-auto">
      <table>
