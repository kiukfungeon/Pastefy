<script setup lang="ts">
import Select from 'primevue/select'
import { computed } from 'vue'
import { useConfig } from '@/composables/config.ts'
import {
  changeLanguage,
  languageOptions,
  type LanguagePreference,
  resolveLanguagePreference,
} from '@/i18n.ts'
import { useTranslation } from 'i18next-vue'

const config = useConfig()
const { t } = useTranslation()
const localizedLanguageOptions = computed(() =>
  languageOptions.map((option) =>
    option.value === 'browser' ? { ...option, label: t('settings.browserLanguage') } : option,
  ),
)
const language = computed({
  get: () => config.value.language || 'browser',
  set: (value: LanguagePreference) => {
    config.value.language = value
    void changeLanguage(value)
  },
})

const getCurrentLanguageIcon = (l: string) => {
  if (l === 'browser') l = resolveLanguagePreference()

  return languageOptions.find((a) => a.value === l)?.icon
}
</script>

<template>
