<script setup lang="ts">
import InputText from 'primevue/inputtext'
import TreeSelect from 'primevue/treeselect'
import Checkbox from 'primevue/checkbox'
import DatePicker from 'primevue/datepicker'
import Select from 'primevue/select'
import Button from 'primevue/button'
import Message from 'primevue/message'
import ButtonGroup from 'primevue/buttongroup'
import { computed, ref, watch } from 'vue'

import Codemirror from 'codemirror-editor-vue3'

import 'codemirror/addon/display/placeholder.js'
import 'codemirror/mode/javascript/javascript.js'
import 'codemirror/mode/markdown/markdown.js'
import 'codemirror/addon/display/placeholder.js'
import 'codemirror/addon/edit/closebrackets.js'
import 'codemirror/addon/edit/closetag.js'
import 'codemirror/addon/edit/continuelist.js'
import 'codemirror/addon/edit/matchbrackets.js'
import 'codemirror/addon/edit/matchtags.js'
import 'codemirror/mode/meta.js'
import 'codemirror/addon/mode/loadmode.js'

import 'codemirror/addon/hint/show-hint.js'
import 'codemirror/addon/hint/javascript-hint.js'
import 'codemirror/addon/hint/html-hint.js'
import 'codemirror/addon/hint/sql-hint.js'
import 'codemirror/addon/hint/xml-hint.js'
import 'codemirror/addon/hint/css-hint.js'

import CodeMirror from 'codemirror'
import emmet from '@emmetio/codemirror-plugin'

import codemirrorLanguageImports from '@/utils/codemirror-language-imports.ts'
import { useCurrentPasteStore } from '@/stores/current-paste.ts'
import { useAsyncState, useDebounceFn } from '@vueuse/core'
import { client } from '@/main.ts'
import type { Paste } from '@/types/paste.ts'
import router from '@/router'
import PasteVisibilityIcon from '@/components/PasteVisibilityIcon.vue'
import { getIconByFileName } from '@/utils/lang-information.ts'
import PastePreview from '@/components/PastePreview.vue'
import { useAppInfoStore } from '@/stores/app-info.ts'
import { useUserFoldersStore } from '@/stores/user-folders.ts'
import type { Folder } from '@/types/folder.ts'
import { useCurrentUserStore } from '@/stores/current-user.ts'
import { estimateTitle } from '@/utils/estimate-title.ts'
import { useConfig } from '@/composables/config.ts'
import { useAppStore } from '@/stores/app.ts'

const currentPaste = useCurrentPasteStore()
const currentUserStore = useCurrentUserStore()

const config = useConfig()
const appStore = useAppStore()

emmet(CodeMirror)

const cmOptions = ref({
  mode: 'javascript',
  theme: 'pastefy',
  autofocus: true,
  autoCloseBrackets: true,
  autoCloseTags: true,
  matchBrackets: true,
  extraKeys: {
    Tab: 'emmetExpandAbbreviation',
    Esc: 'emmetResetAbbreviation',
    Enter: 'emmetInsertLineBreak',
  },
} as CodeMirror.EditorConfiguration)
const isFullscreen = ref(false)

const currentTitle = computed(() =>
  currentPaste.type === 'MULTI_PASTE'
    ? currentPaste.getCurrentMultiPart()?.name || currentPaste.title
    : currentPaste.title,
)

watch(
  currentTitle,
  useDebounceFn(async () => {
    let ext = currentTitle.value.split('.').pop()
    let lang: string | undefined = undefined
    if (ext) {
      if (ext === 'geojson') ext = 'json'
      const cmExt = CodeMirror.findModeByExtension(ext)
      if (cmExt) {
        await codemirrorLanguageImports[cmExt.mode]?.()
      }
      cmOptions.value.mode = cmExt?.mode
      lang = cmExt?.mode?.replace('htmlmixed', 'html') || ext
    }

    if (
      !currentPaste.tags ||
      currentPaste.tags.length === 0 ||
      currentPaste.tags[0].startsWith('lang-')
    ) {
      lang = (!['clike'].includes(lang || 'clike') && lang?.replace('htmlmixed', 'html')) || ext

      if (lang && currentTitle.value.includes('.')) {
        currentPaste.tags = [`lang-${lang}`]
      } else {
        currentPaste.tags = []
      }
    }
  }, 200),
)

const showSettings = ref(false)

const { execute: submitPaste, isLoading: isPasting } = useAsyncState(
  async () => {
    if (currentPaste.currentId) {
      await client.put(`/api/v2/paste/${currentPaste.currentId}`, currentPaste.toPaste())
      currentPaste.clear()
      router.go(0)
      return
    }

    const passwordBefore = currentPaste.password
    const paste = (await client.post('/api/v2/paste', currentPaste.toPaste())).data.paste as Paste

    router.push({
      name: 'paste',
      params: {
        paste: paste.id,
      },
      hash: passwordBefore
        ? undefined
        : currentPaste.password
          ? `#${currentPaste.password}`
          : undefined,
    })
    currentPaste.clear()
    isFullscreen.value = false
  },
  undefined,
  {
    immediate: false,
  },
)
const appInfo = useAppInfoStore()
const userFoldersStore = useUserFoldersStore()

type FolderEntry = {
  key: string
  label: string
  data: string
  icon: string
  children: FolderEntry[]
}
const folderToEntry = (folder: Folder): FolderEntry => ({
  key: folder.id,
  label: folder.name,
  data: folder.id,
  icon: 'ti ti-folder',
  children: folder.children.map(folderToEntry) as FolderEntry[],
})

const userFolders = computed(() => userFoldersStore.folders?.map(folderToEntry) || [])

const pasteEvent = async (e: unknown) => {
  if (e instanceof ClipboardEvent) {
    const data = e.clipboardData?.getData('text')
    if (data && !(e.target as HTMLTextAreaElement)?.value && currentPaste.title === '') {
      const inData = data.trim()

      if (inData.length > 100_000) {
        return
      }

      const title = estimateTitle(inData)

      if (!title) return

      if (currentPaste.type === 'MULTI_PASTE') {
        const currentPart = currentPaste.getCurrentMultiPart()

        if (currentPart) {
          currentPart.name = title
          return
        }
      }
      currentPaste.title = title
    }
  }
}

const isUrl = ref(false)
const ignoreUrlCheck = ref(false)
const checkUrl = useDebounceFn(() => {
  if (currentPaste.contents) {
    isUrl.value = !!currentPaste.contents.match(
      /^https?:\/\/(www\.)?[-a-zA-Z0-9@:%._\+~#=]{1,256}\.[a-zA-Z0-9()]{1,6}\b([-a-zA-Z0-9()@:%_\+.~#?&//=]*)$/,
    )
  }
}, 500)

watch(
  () => currentPaste.contents,
  () => {
    if (!ignoreUrlCheck.value) checkUrl()
  },
)

watch(
  () => appStore.createPasteFullscreenRequested,
  (requested) => {
    if (!requested) return
    isFullscreen.value = true
    appStore.createPasteFullscreenRequested = false
