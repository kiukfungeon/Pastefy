<script setup lang="ts">
import type { Paste } from '@/types/paste.ts'
import Highlighted from '@/components/Highlighted.vue'
import { computed } from 'vue'
import CSVViewer from '@/components/previews/CSVViewer.vue'
import { useConfig } from '@/composables/config.ts'
import { getIconByFileName } from '@/utils/lang-information.ts'

const props = defineProps<{
  paste: Paste
  previewMaxHeight?: string
  selected?: boolean
  linkClass?: string
}>()

const pasteContents = computed(() => {
  if (props.paste.type === 'MULTI_PASTE') {
    try {
      let json = props.paste.content.trim()

      // Json is being stripped when over 100chars so complete the json
      if (json.endsWith('"}...')) json += ']'
      if (json.endsWith('\\"...')) json += '"}]'
      if (json.endsWith('"...')) json += '}]'
      if (json.endsWith('...')) json += '"}]'

      return JSON.parse(json)[0]?.contents
    } catch {
      return ''
    }
  }

  return props.paste.content
})

const config = useConfig()
const getCopyContents = async () => {
  return await fetch(
    `${(import.meta.env.VITE_APP_BASE_Url as string) || ''}/${props.paste.id}/raw`,
    {
      headers: {
        Authorization: `Bearer ${config.value.apiKey}`,
      },
    },
  ).then((t) => t.text())
}

const icon = computed(() => getIconByFileName(props.paste.title))
</script>
<template>
  <router-link
    :to="{ name: 'paste', params: { paste: paste.id } }"
    class="relative block rounded-xl border transition-all"
    :class="
      (linkClass ? `${linkClass} ` : '') +
      (selected
        ? 'border-neutral-300 bg-neutral-200 hover:bg-neutral-300 dark:border-neutral-600 dark:bg-neutral-700 dark:hover:bg-neutral-600'
        : 'border-neutral-200 bg-neutral-100 hover:bg-neutral-200 dark:border-neutral-700 dark:bg-neutral-800 dark:hover:bg-neutral-700')
    "
  >
    <div class="flex justify-between gap-2 p-3 pb-0">
      <div v-if="paste.encrypted">
        <i class="ti ti-lock" />
        <span class="font-bold">{{ $t('paste.encryptedPaste') }}</span>
      </div>
      <span
        class="flex items-center gap-1 truncate font-bold"
        v-else-if="paste.title.trim()"
