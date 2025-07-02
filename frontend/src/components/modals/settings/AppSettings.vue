<script lang="ts" setup>
import Divider from 'primevue/divider'
import { useConfig } from '@/composables/config.ts'
import Checkbox from 'primevue/checkbox'
import ThemeSwitcher from '@/components/ThemeSwitcher.vue'
import PasteVisibilityIcon from '@/components/PasteVisibilityIcon.vue'
import Select from 'primevue/select'
import LanguageSwitcher from '@/components/LanguageSwitcher.vue'

const config = useConfig()
</script>
<template>
  <div class="flex flex-col" @click.stop>
    <div class="flex items-center justify-between">
      <label for="animations" class="text-sm">{{ $t('settings.animations') }}</label>
      <Checkbox id="animations" v-model="config.animations" binary size="small" />
    </div>
    <Divider />

    <div class="flex items-center justify-between gap-2">
