<script setup lang="ts">
import { useTranslation } from 'i18next-vue'
import { computed, ref, watch } from 'vue'
import { useAsyncState, useTitle } from '@vueuse/core'
import { useRoute } from 'vue-router'
import { useRouteQuery } from '@vueuse/router'
import Button from 'primevue/button'
import Message from 'primevue/message'
import CryptoJS from 'crypto-js'
import { client, eventBus } from '@/main.ts'
import type { MultiPastePart, Paste } from '@/types/paste.ts'
import ErrorContainer from '@/components/ErrorContainer.vue'
import LoadingContainer from '@/components/LoadingContainer.vue'
import MarkdownViewer from '@/components/previews/MarkdownViewer.vue'
import PasteStarButton from '@/components/paste/PasteStarButton.vue'
import { findFromFileName } from '@/utils/lang-replacements.ts'

const { t } = useTranslation()
const route = useRoute()
const selectedPart = useRouteQuery<string>('part', '')

const password = ref<string | undefined>(undefined)
const decrypted = ref(false)
const paste = ref<Paste | undefined>(undefined)
const title = ref<string>('')
const markdown = ref<string>('')

const multiPasteParts = computed(() => {
  if (!paste.value || paste.value.type !== 'MULTI_PASTE') return []

  try {
    return JSON.parse(paste.value.content || '[]') as MultiPastePart[]
  } catch {
    return []
  }
})

const markdownParts = computed(() =>
  multiPasteParts.value.filter((part) => findFromFileName(part.name || '') === 'markdown'),
)

const activeMarkdownPart = computed(() => {
  if (paste.value?.type !== 'MULTI_PASTE') return undefined

  return (
    markdownParts.value.find((part) => part.name === selectedPart.value) || markdownParts.value[0]
  )
})

const isMarkdownPaste = computed(() => {
  if (!paste.value) return false
  if (paste.value.type === 'PASTE') return findFromFileName(paste.value.title || '') === 'markdown'
  return markdownParts.value.length > 0
})

const formatDate = (value?: string) => {
  if (!value) return ''
  return new Date(value.replace(' ', 'T')).toLocaleDateString(undefined, {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
  })
}

