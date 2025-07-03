<script setup lang="ts">
import { useTranslation } from 'i18next-vue'
import Button from 'primevue/button'
import Message from 'primevue/message'
import Select from 'primevue/select'
import Textarea from 'primevue/textarea'
import { computed } from 'vue'
import { useClipboard, useStorage } from '@vueuse/core'
import UtilityShell from '@/components/tools/utility/UtilityShell.vue'
import UtilityResultActions from '@/components/tools/utility/UtilityResultActions.vue'

const { t } = useTranslation()
const clipboard = useClipboard()
const mode = useStorage<'escape' | 'unescape'>('pastefy-utility-json-string-mode', 'escape')
const input = useStorage('pastefy-utility-json-string-input', '{"title":"Pastefy","content":"Hello\\nWorld"}')
const modes = [
  { get label() { return t('utility.jsonStringTool.options.escape') }, value: 'escape' },
  { get label() { return t('utility.jsonStringTool.options.unescape') }, value: 'unescape' },
]

const state = computed(() => {
  try {
    return {
      value: mode.value === 'escape' ? JSON.stringify(input.value) : JSON.parse(input.value),
      error: undefined,
    }
  } catch {
    return {
      value: '',
      error: 'Input could not be parsed for JSON string unescaping.',
    }
  }
})
</script>

<template>
  <UtilityShell>
    <template #controls>
      <label class="text-sm font-medium">{{ $t('utility.jsonStringTool.mode') }}</label>
      <Select v-model="mode" :options="modes" option-label="label" option-value="value" />
      <label class="text-sm font-medium">{{ $t('utility.jsonStringTool.input') }}</label>
      <Textarea v-model="input" auto-resize rows="10" fluid />
