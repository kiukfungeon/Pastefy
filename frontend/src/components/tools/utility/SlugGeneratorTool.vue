<script setup lang="ts">
import Button from 'primevue/button'
import Textarea from 'primevue/textarea'
import { computed } from 'vue'
import { useClipboard, useStorage } from '@vueuse/core'
import UtilityShell from '@/components/tools/utility/UtilityShell.vue'
import UtilityResultActions from '@/components/tools/utility/UtilityResultActions.vue'

const clipboard = useClipboard()
const input = useStorage('pastefy-utility-slug-input', 'Pastefy makes snippet sharing feel fast & elegant')

const state = computed(() => {
  const words = input.value
    .trim()
    .normalize('NFKD')
    .replace(/[\u0300-\u036f]/g, '')
    .replace(/[^a-zA-Z0-9]+/g, ' ')
    .trim()
    .split(/\s+/)
    .filter(Boolean)
    .map((word) => word.toLowerCase())

  return {
    kebab: words.join('-'),
    snake: words.join('_'),
    constant: words.join('_').toUpperCase(),
    camel: words
      .map((word, index) =>
        index === 0 ? word : `${word.charAt(0).toUpperCase()}${word.slice(1)}`,
      )
      .join(''),
    pascal: words.map((word) => `${word.charAt(0).toUpperCase()}${word.slice(1)}`).join(''),
  }
})

const result = computed(() => JSON.stringify(state.value, null, 2))
</script>

<template>
  <UtilityShell>
    <template #controls>
      <label class="text-sm font-medium">{{ $t('utility.slugGeneratorTool.sourceText') }}</label>
      <Textarea v-model="input" auto-resize rows="8" fluid />
    </template>

    <template #result>
      <div class="grid gap-3">
        <div
