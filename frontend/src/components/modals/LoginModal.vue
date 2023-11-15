<script lang="ts" setup>
import Dialog from 'primevue/dialog'
import { useCurrentUserStore } from '@/stores/current-user.ts'
const currentUserStore = useCurrentUserStore()

const visible = defineModel<boolean>('visible')
</script>
<template>
  <Dialog v-model:visible="visible" modal :header="$t('auth.login')" class="w-[30rem] max-w-full">
