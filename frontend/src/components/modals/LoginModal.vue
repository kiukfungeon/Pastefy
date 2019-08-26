<script lang="ts" setup>
import Dialog from 'primevue/dialog'
import { useCurrentUserStore } from '@/stores/current-user.ts'
const currentUserStore = useCurrentUserStore()

const visible = defineModel<boolean>('visible')
</script>
<template>
  <Dialog v-model:visible="visible" modal :header="$t('auth.login')" class="w-[30rem] max-w-full">
    <div class="flex flex-wrap justify-center gap-2">
      <a
        v-for="authType of currentUserStore.authTypes"
        :href="`/api/v2/auth/oauth2/${authType}`"
        :key="authType"
        class="flex aspect-square w-[6rem] flex-col items-center justify-center gap-2 rounded-xl border border-neutral-300 p-4 dark:border-neutral-700"
      >
        <svg
          v-if="authType === 'interaapps'"
          width="24"
