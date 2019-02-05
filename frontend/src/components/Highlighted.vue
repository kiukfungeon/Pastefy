<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { findFromFileName } from '@/utils/lang-replacements.ts'
import { highlight } from '@/utils/highlight.ts'
import '@/utils/highlight-imports.ts'
import highlightWorker from '@/utils/workers/highlight.worker.ts?worker'
import CopyButton from '@/components/CopyButton.vue'
import PasteLineCommentAvatars from '@/components/paste/PasteLineCommentAvatars.vue'
import type { PasteCommentMarker } from '@/types/paste-comment.ts'

const props = defineProps<{
  contents: string
  fileName?: string
  lang?: string
  hideDivider?: boolean
  startingLineNumber?: number
  hideLineNumbering?: boolean
  hideColorPreview?: boolean
  showCopyButton?: boolean
  getCopyContents?: () => Promise<string>
  lineCommentMarkers?: PasteCommentMarker[]
  enableLineComments?: boolean
}>()

const emit = defineEmits<{
  lineClick: [event: Event, line: number, target: HTMLElement]
}>()

const highlightedContents = ref<string | undefined>(undefined)
const markerByLine = computed(
  () => new Map(props.lineCommentMarkers?.map((marker) => [marker.line, marker]) || []),
)
const lineCount = computed(() => {
  let count = 1
  for (let index = 0; index < props.contents.length; index++) {
    if (props.contents.charCodeAt(index) === 10) count++
  }
  return count
})

const id = Math.random().toString(36).substring(7)

const selectGutterLine = (event: MouseEvent) => {
  if (!props.enableLineComments) return

  const target = event.target
