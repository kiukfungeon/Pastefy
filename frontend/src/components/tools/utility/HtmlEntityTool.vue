<script setup lang="ts">
import { useTranslation } from 'i18next-vue'
import Button from 'primevue/button'
import Select from 'primevue/select'
import Textarea from 'primevue/textarea'
import { computed } from 'vue'
import { useClipboard, useStorage } from '@vueuse/core'
import UtilityShell from '@/components/tools/utility/UtilityShell.vue'
import UtilityResultActions from '@/components/tools/utility/UtilityResultActions.vue'

const { t } = useTranslation()
const clipboard = useClipboard()
const mode = useStorage<'encode' | 'decode'>('pastefy-utility-html-entity-mode', 'encode')
const input = useStorage('pastefy-utility-html-entity-input', '<div class="card">Pastefy & tools</div>')
const modes = [
  { get label() { return t('utility.htmlEntityTool.options.encode') }, value: 'encode' },
  { get label() { return t('utility.htmlEntityTool.options.decode') }, value: 'decode' },
]

const state = computed(() => {
  if (typeof document === 'undefined') return { value: input.value }

  if (mode.value === 'encode') {
    const el = document.createElement('div')
    el.textContent = input.value
    return { value: el.innerHTML }
  }

  const el = document.createElement('textarea')
  el.innerHTML = input.value
  return { value: el.value }
})
</script>

<template>
  <UtilityShell>
    <template #controls>
      <label class="text-sm font-medium">{{ $t('utility.htmlEntityTool.mode') }}</label>
      <Select v-model="mode" :options="modes" option-label="label" option-value="value" />
      <label class="text-sm font-medium">{{ $t('utility.htmlEntityTool.input') }}</label>
      <Textarea v-model="input" auto-resize rows="10" fluid />
    </template>

