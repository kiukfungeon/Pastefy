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
