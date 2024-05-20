<script setup lang="ts">
import Button from 'primevue/button'
import InputText from 'primevue/inputtext'
import Message from 'primevue/message'
import Codemirror from 'codemirror-editor-vue3'
import { computed, ref, watch } from 'vue'
import { useAsyncState, useClipboard, useStorage } from '@vueuse/core'
import { useRoute, useRouter } from 'vue-router'
import type CodeMirror from 'codemirror'
import CodeMirrorLib from 'codemirror'

import 'codemirror/addon/display/placeholder.js'
import 'codemirror/addon/edit/closebrackets.js'
import 'codemirror/addon/edit/closetag.js'
import 'codemirror/addon/edit/matchbrackets.js'
import 'codemirror/addon/edit/matchtags.js'
import 'codemirror/mode/meta.js'
import 'codemirror/addon/mode/loadmode.js'

import { client } from '@/main.ts'
import PastePreview from '@/components/PastePreview.vue'
import Highlighted from '@/components/Highlighted.vue'
import ToolFocusExitButton from '@/components/tools/ToolFocusExitButton.vue'
import ToolPinButton from '@/components/tools/ToolPinButton.vue'
import ToolRelatedLinks from '@/components/tools/ToolRelatedLinks.vue'
import { useCurrentPasteStore } from '@/stores/current-paste.ts'
import type { Paste } from '@/types/paste.ts'
import codemirrorLanguageImports from '@/utils/codemirror-language-imports.ts'
import {
  conversionTools,
  findConversionTool,
  runConversion,
} from '@/utils/conversion-tools.ts'
import { findFromFileName } from '@/utils/lang-replacements.ts'
import { useSEO } from '@/composables/seo.ts'
import { useTranslation } from 'i18next-vue'
import { localizeTool } from '@/utils/tool-catalog-i18n.ts'

const route = useRoute()
const router = useRouter()
const currentPaste = useCurrentPasteStore()
const clipboard = useClipboard()
const showEditor = ref(true)
const isExpanded = ref(false)
const { t } = useTranslation()

const drafts = useStorage<Record<string, string>>('pastefy-conversion-drafts', {})
const outputNames = useStorage<Record<string, string>>('pastefy-conversion-output-files', {})
const viewMode = ref<'preview' | 'code'>('code')

const tool = computed(() => {
  const definition = findConversionTool(String(route.params.tool || ''))
  return definition ? localizeTool('conversion', definition, t) : undefined
})

useSEO({
  title: computed(() =>
    tool.value ? `${tool.value.title} • Pastefy Tools` : 'Pastefy Tools • Conversion',
  ),
  description: computed(() =>
    tool.value
      ? `${tool.value.description} Convert ${tool.value.sourceFileName} into ${tool.value.targetFileName} and share the result when you are ready.`
      : 'Use Pastefy conversion tools to transform structured data and create a paste from the result.',
  ),
})

const ensureToolState = (slug: string, nextTool: NonNullable<typeof tool.value>) => {
  if (!drafts.value[slug]) {
    drafts.value = {
      ...drafts.value,
      [slug]: nextTool.example,
    }
  }

  if (!outputNames.value[slug]) {
    outputNames.value = {
      ...outputNames.value,
      [slug]: nextTool.targetFileName,
    }
  }
}

watch(
  tool,
  (nextTool) => {
    if (!nextTool) return
    ensureToolState(nextTool.slug, nextTool)
  },
  { immediate: true },
)

const currentContents = computed({
  get() {
    if (!tool.value) return ''
