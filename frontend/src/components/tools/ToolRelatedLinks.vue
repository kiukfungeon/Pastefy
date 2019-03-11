<script setup lang="ts">
defineProps<{
  items: Array<{
    slug: string
    icon: string
    title: string
    meta: string
    to: {
      name: string
      params?: Record<string, string>
      hash?: string
    }
  }>
}>()
</script>

<template>
  <section class="grid gap-4 md:grid-cols-3">
    <router-link
      v-for="item of items"
      :key="item.slug"
      :to="item.to"
