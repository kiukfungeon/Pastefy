<script setup lang="ts">
import Button from 'primevue/button'
import { useAppInfoStore } from '@/stores/app-info.ts'

const appInfo = useAppInfoStore()
</script>
<template>
  <div class="mx-auto max-w-[1200px]">
    <div class="flex flex-col gap-4">
      <h1 class="text-2xl font-bold">{{ $t('views.adminLayout.admin') }}</h1>
      <div class="flex gap-1">
        <Button
          as="router-link"
