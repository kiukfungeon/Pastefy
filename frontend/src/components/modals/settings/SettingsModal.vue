<script lang="ts" setup>
import Dialog from 'primevue/dialog'
import Button from 'primevue/button'
import AppSettings from '@/components/modals/settings/AppSettings.vue'
import { ref } from 'vue'

const visible = defineModel<boolean>('visible')
const tab = ref('app-settings')
</script>
<template>
  <Dialog
    v-model:visible="visible"
    modal
    :header="$t('settings.title')"
    class="h-[20rem] w-[46rem] max-w-full overflow-hidden"
  >
    <template #container="{ closeCallback }">
      <div class="relative h-full md:grid md:grid-cols-[14rem_1fr]" @click.stop>
        <Button
          text
