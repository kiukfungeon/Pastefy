<script setup lang="ts">
import InputText from 'primevue/inputtext'
import Select from 'primevue/select'
import Button from 'primevue/button'
import Message from 'primevue/message'
import Codemirror from 'codemirror-editor-vue3'
import { computed, ref, watch } from 'vue'
import { useAsyncState, useClipboard, useStorage } from '@vueuse/core'
import { useRoute, useRouter } from 'vue-router'
import type CodeMirror from 'codemirror'
import CodeMirrorLib from 'codemirror'
import emmet from '@emmetio/codemirror-plugin'

import 'codemirror/addon/display/placeholder.js'
import 'codemirror/addon/edit/closebrackets.js'
import 'codemirror/addon/edit/closetag.js'
import 'codemirror/addon/edit/matchbrackets.js'
import 'codemirror/addon/edit/matchtags.js'
import 'codemirror/mode/meta.js'
import 'codemirror/addon/mode/loadmode.js'

import { client } from '@/main.ts'
import PastePreview from '@/components/PastePreview.vue'
import ToolFocusExitButton from '@/components/tools/ToolFocusExitButton.vue'
import ToolPinButton from '@/components/tools/ToolPinButton.vue'
import ToolRelatedLinks from '@/components/tools/ToolRelatedLinks.vue'
import { findPreviewTool, previewTools } from '@/utils/preview-tools.ts'
import codemirrorLanguageImports from '@/utils/codemirror-language-imports.ts'
import { findFromFileName } from '@/utils/lang-replacements.ts'
import { useCurrentPasteStore } from '@/stores/current-paste.ts'
import type { Paste, PasteVisibility } from '@/types/paste.ts'
import { useSEO } from '@/composables/seo.ts'
import { useTranslation } from 'i18next-vue'
import { localizeTool } from '@/utils/tool-catalog-i18n.ts'

emmet(CodeMirrorLib)

const route = useRoute()
const router = useRouter()
const currentPaste = useCurrentPasteStore()
const clipboard = useClipboard()
const showEditor = ref(true)
const isExpanded = ref(false)
const { t } = useTranslation()

const drafts = useStorage<Record<string, string>>('pastefy-tool-drafts', {})
const fileNames = useStorage<Record<string, string>>('pastefy-tool-file-names', {})
const visibilityStates = useStorage<Record<string, PasteVisibility>>('pastefy-tool-visibilities', {})

const tool = computed(() => {
  const definition = findPreviewTool(String(route.params.tool || ''))
  return definition ? localizeTool('preview', definition, t) : undefined
})

useSEO({
  title: computed(() =>
    tool.value ? `${tool.value.title} • Pastefy Tools` : 'Pastefy Tools • Preview Lab',
  ),
  description: computed(() =>
    tool.value
      ? `${tool.value.description} Edit ${tool.value.fileName} live and preview the result before creating a paste.`
      : 'Open a Pastefy preview tool to edit and inspect structured content before sharing it.',
  ),
})

const visibilityOptions = computed(() => [
  {
    label: t('paste.visibility.unlisted'),
    value: 'UNLISTED',
  },
  {
    label: t('paste.visibility.public'),
    value: 'PUBLIC',
  },
  {
    label: t('paste.visibility.private'),
    value: 'PRIVATE',
  },
])

const ensureToolState = (slug: string, nextTool: NonNullable<typeof tool.value>) => {
  if (!drafts.value[slug]) {
    drafts.value = {
      ...drafts.value,
      [slug]: nextTool.example,
    }
  }

  if (!fileNames.value[slug]) {
    fileNames.value = {
      ...fileNames.value,
      [slug]: nextTool.fileName,
    }
  }

  if (!visibilityStates.value[slug]) {
    visibilityStates.value = {
      ...visibilityStates.value,
      [slug]: 'UNLISTED',
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

const currentFileName = computed({
  get() {
    if (!tool.value) return ''
    ensureToolState(tool.value.slug, tool.value)
    return fileNames.value[tool.value.slug] || tool.value.fileName
  },
  set(value: string) {
    if (!tool.value) return
    fileNames.value = {
      ...fileNames.value,
      [tool.value.slug]: value,
    }
  },
})

const currentVisibility = computed<PasteVisibility>({
  get() {
    if (!tool.value) return 'UNLISTED'
    ensureToolState(tool.value.slug, tool.value)
    return visibilityStates.value[tool.value.slug] || 'UNLISTED'
  },
  set(value) {
    if (!tool.value) return
    visibilityStates.value = {
      ...visibilityStates.value,
      [tool.value.slug]: value,
    }
  },
})

const cmOptions = ref<CodeMirror.EditorConfiguration>({
  mode: 'markdown',
  theme: 'pastefy',
  autofocus: false,
  lineNumbers: true,
  autoCloseBrackets: true,
  autoCloseTags: true,
  matchBrackets: true,
})
