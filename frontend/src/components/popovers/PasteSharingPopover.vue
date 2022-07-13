<script setup lang="ts">
import Popover from 'primevue/popover'
import Button from 'primevue/button'
import InputText from 'primevue/inputtext'
import InputGroup from 'primevue/inputgroup'
import type { MultiPastePart, Paste } from '@/types/paste.ts'
import EmbedPasteModal from '@/components/modals/EmbedPasteModal.vue'
import ScreenshotPaste from '@/components/modals/ScreenshotPaste.vue'
import QRModal from '@/components/modals/QRModal.vue'
import { computed, ref, useTemplateRef } from 'vue'
import type { PopoverMethods } from 'primevue'
import { useClipboard } from '@vueuse/core'
import CopyButton from '@/components/CopyButton.vue'
import { findFromFileName } from '@/utils/lang-replacements.ts'
import { resolveSharePreviewType } from '@/utils/share-preview.ts'

const pasteEmbedOpened = ref(false)
const pasteScreenshotOpened = ref(false)

const clipboard = useClipboard()

const props = defineProps<{
  paste: Paste
  currentFileName: string
  asEmbed?: boolean
  content?: string
  pasteId: string
}>()

const pasteUrl = computed(() => `${origin}/${props.paste?.id}`)
const shareModesUrl = computed(() => `${origin}/${props.paste?.id}/share-modes`)

const markdownParts = computed(() => {
  if (props.paste?.type !== 'MULTI_PASTE') return []

  try {
    return (JSON.parse(props.paste.content || '[]') as MultiPastePart[]).filter(
      (part) => findFromFileName(part.name || '') === 'markdown',
    )
  } catch {
    return []
  }
})

const previewParts = computed(() => {
  if (props.paste?.type !== 'MULTI_PASTE') return []

  try {
    return (JSON.parse(props.paste.content || '[]') as MultiPastePart[]).filter((part) =>
      resolveSharePreviewType(part.name),
    )
  } catch {
    return []
  }
})

