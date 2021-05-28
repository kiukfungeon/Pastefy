<script setup lang="ts">
import Button from 'primevue/button'
import Divider from 'primevue/divider'
import { useAppInfoStore } from '@/stores/app-info.ts'
import ThemeSwitcher from '@/components/ThemeSwitcher.vue'
import { useAppStore } from '@/stores/app.ts'
import ComponentInjection from '@/components/ComponentInjection.vue'
import { defineAsyncComponent } from 'vue'
import LoadingContainer from '@/components/LoadingContainer.vue'
import LanguageSwitcher from '@/components/LanguageSwitcher.vue'

const CreatePaste = defineAsyncComponent(() => import('@/components/forms/CreatePaste.vue'))

defineProps<{
  isHidden?: boolean
}>()

const appInfo = useAppInfoStore()
const appStore = useAppStore()
</script>
<template>
  <div
    class="flex h-full flex-col justify-between overflow-auto border-r border-neutral-200 bg-neutral-100 p-2 dark:border-neutral-800 dark:bg-neutral-900"
    v-shortkey="['meta', 'k']"
    @shortkey="appStore.searchShown = true"
  >
    <div class="flex w-full flex-col gap-3 pt-[3.5rem]">
      <nav class="flex w-full gap-2">
        <Button
          as="router-link"
          :to="{ name: 'home-page' }"
          class="border-neutral-400 bg-gradient-to-b from-[transparent] to-neutral-100 dark:border-neutral-600 dark:to-neutral-900"
          size="small"
          icon="ti ti-home text-lg"
          :label="$t('nav.home').toLowerCase()"
          severity="contrast"
