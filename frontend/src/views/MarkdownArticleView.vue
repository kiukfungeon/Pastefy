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

const toArticleLabel = (value?: string) => {
  if (!value) return t('views.markdownArticleView.untitled')
  return value.replace(/\.md$/i, '')
}

const setValues = () => {
  if (!paste.value) return
  if (paste.value.type === 'MULTI_PASTE') {
    const part = activeMarkdownPart.value
    title.value = toArticleLabel(part?.name || paste.value.title || 'Untitled Article')
    markdown.value = part?.contents || ''
  } else {
    title.value = toArticleLabel(paste.value.title || 'Untitled Article')
    markdown.value = paste.value.content
  }
  useTitle(`${title.value} | Pastefy`)
}

watch(activeMarkdownPart, () => {
  if (paste.value?.type === 'MULTI_PASTE' && activeMarkdownPart.value) {
    setValues()
  }
})

const decrypt = () => {
  if (!paste.value || !password.value) return

  const decryptedContent = CryptoJS.AES.decrypt(paste.value.content, password.value).toString(
    CryptoJS.enc.Utf8,
  )
  if (decryptedContent === '') return

  paste.value.title = CryptoJS.AES.decrypt(paste.value.title, password.value).toString(
    CryptoJS.enc.Utf8,
  )
  paste.value.content = decryptedContent
  decrypted.value = true
  setValues()
}

const { isLoading, error } = useAsyncState(async () => {
  const pasteRes = (
    await client.get(`/api/v2/paste/${route.params.paste as string}`, {
      params: {
        from_frontend: 'true',
      },
    })
  ).data as Paste

  paste.value = pasteRes

  if (pasteRes.type === 'MULTI_PASTE' && !selectedPart.value) {
    selectedPart.value = markdownParts.value[0]?.name || ''
  }

  const hashPw = window.location.hash?.replace('#', '')
  if (hashPw && paste.value.encrypted) {
    password.value = hashPw
    decrypt()
  } else {
    setValues()
  }

  eventBus.emit('pageLoaded', 'paste')
}, undefined)
