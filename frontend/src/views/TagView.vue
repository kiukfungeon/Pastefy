<script setup lang="ts">
import { useAsyncState, useTitle } from '@vueuse/core'
import { useRoute } from 'vue-router'
import { client } from '@/main.ts'
import ErrorContainer from '@/components/ErrorContainer.vue'
import LoadingContainer from '@/components/LoadingContainer.vue'
import PasteList from '@/components/lists/PasteList.vue'
import type { Tag } from '@/types/tags.ts'

useTitle(`Tag | Pastefy`)

const route = useRoute()

const {
  isLoading,
  state: tag,
  error,
} = useAsyncState(async () => {
  const tag = (await client.get(`/api/v2/public/tags/${route.params.tag}` as string)).data as Tag

  useTitle(`${tag.display_name || tag.tag} | Pastefy`)
  return tag
}, undefined)
</script>
<template>
  <main class="mx-auto w-full max-w-[1200px]">
    <ErrorContainer v-if="error" :error="error as any" />
    <LoadingContainer v-else-if="isLoading" class="flex items-center justify-center p-3" />
    <div v-else-if="tag">
      <div
        class="mb-9 flex flex-col items-center justify-between gap-5 rounded-md border border-neutral-200 object-cover p-3 md:flex-row-reverse md:gap-3 dark:border-neutral-700"
      >
        <img
          v-if="tag.image_url"
          :src="tag.image_url"
          class="w-[21rem] max-w-full rounded-md border border-neutral-200 object-cover md:h-[7rem] md:w-[14rem] dark:border-neutral-700"
        />
        <div v-else />
