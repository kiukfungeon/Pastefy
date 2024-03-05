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
    ensureToolState(tool.value.slug, tool.value)
    return drafts.value[tool.value.slug] || tool.value.example
  },
  set(value: string) {
    if (!tool.value) return
    drafts.value = {
      ...drafts.value,
      [tool.value.slug]: value,
    }
  },
})

const currentOutputFileName = computed({
  get() {
    if (!tool.value) return ''
    ensureToolState(tool.value.slug, tool.value)
    return outputNames.value[tool.value.slug] || tool.value.targetFileName
  },
  set(value: string) {
    if (!tool.value) return
    outputNames.value = {
      ...outputNames.value,
      [tool.value.slug]: value,
    }
  },
})

const sourceMode = ref<CodeMirror.EditorConfiguration['mode']>('markdown')

const loadSourceMode = async () => {
  if (!tool.value) return
  let extension = tool.value.sourceFileName.split('.').pop()
  if (tool.value.sourceFileName === '.env') extension = 'properties'
  if (extension === 'geojson') extension = 'json'
  if (extension === 'md') extension = 'markdown'
  if (extension === 'htm') extension = 'html'

  const modeByType: Record<string, string> = {
    markdown: 'markdown',
    json: 'javascript',
    html: 'htmlmixed',
    xml: 'xml',
    yaml: 'yaml',
    csv: 'spreadsheet',
    diff: 'diff',
    http: 'http',
    properties: 'properties',
    log: 'shell',
  }

  let mode = extension ? CodeMirrorLib.findModeByExtension(extension)?.mode : undefined
  if (!mode) mode = modeByType[tool.value.sourceType] || 'markdown'

  if (mode && codemirrorLanguageImports[mode]) {
    await codemirrorLanguageImports[mode]()
  }

  sourceMode.value = mode || 'markdown'
}

watch(tool, loadSourceMode, { immediate: true })

const sourceOptions = computed<CodeMirror.EditorConfiguration>(() => ({
  mode: sourceMode.value,
  theme: 'pastefy',
  autofocus: false,
  lineNumbers: true,
  autoCloseBrackets: true,
  autoCloseTags: true,
  matchBrackets: true,
}))

const conversionState = computed(() => {
  if (!tool.value) {
    return {
      output: '',
      error: undefined as string | undefined,
    }
  }

  try {
    return {
      output: runConversion(tool.value, currentContents.value).output,
      error: undefined,
    }
  } catch (error) {
    return {
      output: '',
      error: error instanceof Error ? error.message : 'Could not convert this input.',
    }
  }
})

const outputLang = computed(() => findFromFileName(currentOutputFileName.value || '') || tool.value?.targetType)
