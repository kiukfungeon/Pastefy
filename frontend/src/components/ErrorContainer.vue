<script setup lang="ts">
import Message from 'primevue/message'
defineProps<{
  error: { response: { data: { exception: string } } }
}>()
</script>
<template>
