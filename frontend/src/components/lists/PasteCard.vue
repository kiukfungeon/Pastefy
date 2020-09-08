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
