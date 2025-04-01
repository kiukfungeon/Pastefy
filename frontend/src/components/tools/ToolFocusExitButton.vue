<script setup lang="ts">
import Button from 'primevue/button'

defineProps<{
  visible: boolean
}>()

const emit = defineEmits<{
  close: []
}>()
</script>

<template>
  <Button
    v-if="visible"
    icon="ti ti-minimize"
    severity="contrast"
