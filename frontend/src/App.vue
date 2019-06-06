<script setup lang="ts">
import Toast from 'primevue/toast'
import Button from 'primevue/button'
import ConfirmDialog from 'primevue/confirmdialog'
import Dialog from 'primevue/dialog'
import { useCurrentUserStore } from '@/stores/current-user.ts'

const currentUserStore = useCurrentUserStore()
</script>

<template>
  <router-view />
  <Toast />
  <ConfirmDialog>
    <template #container="{ message, acceptCallback, rejectCallback }">
      <div class="flex flex-col items-center rounded p-8">
        <span class="mb-3 block text-xl font-bold">{{ message.header }}</span>
        <p class="mb-0">{{ message.message }}</p>
        <div class="mt-6 flex items-center gap-2">
          <Button
            severity="contrast"
            :label="$t('common.confirm')"
            @click="acceptCallback"
            class="w-32 max-w-full"
            size="small"
          />
          <Button
            :label="$t('common.cancel')"
            outlined
            @click="rejectCallback"
            class="w-32 max-w-full"
            severity="contrast"
