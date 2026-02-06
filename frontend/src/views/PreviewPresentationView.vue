<script setup lang="ts">
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
import PastePreview from '@/components/PastePreview.vue'
import PasteStarButton from '@/components/paste/PasteStarButton.vue'
import {
  getSharePreviewMeta,
  resolveSharePreviewType,
  toSharePreviewLabel,
} from '@/utils/share-preview.ts'

const route = useRoute()
const selectedPart = useRouteQuery<string>('part', '')

const password = ref<string | undefined>(undefined)
const decrypted = ref(false)
const paste = ref<Paste | undefined>(undefined)
const title = ref<string>('')
const previewContents = ref<string>('')
const previewFileName = ref<string>('')

const multiPasteParts = computed(() => {
  if (!paste.value || paste.value.type !== 'MULTI_PASTE') return []

  try {
    return JSON.parse(paste.value.content || '[]') as MultiPastePart[]
  } catch {
    return []
  }
})

const previewableParts = computed(() =>
  multiPasteParts.value.filter((part) => resolveSharePreviewType(part.name)),
)

const activePreviewPart = computed(() => {
  if (paste.value?.type !== 'MULTI_PASTE') return undefined

  return (
    previewableParts.value.find((part) => part.name === selectedPart.value) ||
    previewableParts.value[0]
  )
})

const activePreviewType = computed(() => {
  if (!paste.value) return undefined

  if (paste.value.type === 'MULTI_PASTE') {
    return resolveSharePreviewType(activePreviewPart.value?.name)
  }

  return resolveSharePreviewType(paste.value.title)
})

const previewMeta = computed(() =>
  activePreviewType.value ? getSharePreviewMeta(activePreviewType.value) : undefined,
)

const isSupportedPresentation = computed(() => {
  if (!paste.value) return false
  if (paste.value.type === 'MULTI_PASTE') return previewableParts.value.length > 0
  return !!resolveSharePreviewType(paste.value.title)
})

const setValues = () => {
  if (!paste.value) return

  if (paste.value.type === 'MULTI_PASTE') {
    const part = activePreviewPart.value
    title.value = toSharePreviewLabel(part?.name || paste.value.title)
    previewContents.value = part?.contents || ''
    previewFileName.value = part?.name || ''
  } else {
    title.value = toSharePreviewLabel(paste.value.title)
    previewContents.value = paste.value.content
    previewFileName.value = paste.value.title
  }

  useTitle(`${title.value} | Pastefy`)
}

const formatDate = (value?: string) => {
  if (!value) return ''
  return new Date(value.replace(' ', 'T')).toLocaleDateString(undefined, {
    year: 'numeric',
    month: 'long',
