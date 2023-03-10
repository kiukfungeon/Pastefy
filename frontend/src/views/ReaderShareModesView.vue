<script setup lang="ts">
import { useTranslation } from 'i18next-vue'
import { computed, ref } from 'vue'
import { useAsyncState, useTitle } from '@vueuse/core'
import { useRoute } from 'vue-router'
import Button from 'primevue/button'
import Message from 'primevue/message'
import CryptoJS from 'crypto-js'
import { client, eventBus } from '@/main.ts'
import type { MultiPastePart, Paste } from '@/types/paste.ts'
import ErrorContainer from '@/components/ErrorContainer.vue'
import LoadingContainer from '@/components/LoadingContainer.vue'
import CopyButton from '@/components/CopyButton.vue'
import InputText from 'primevue/inputtext'
import InputGroup from 'primevue/inputgroup'
import { findFromFileName } from '@/utils/lang-replacements.ts'
import { resolveSharePreviewType, toSharePreviewLabel } from '@/utils/share-preview.ts'

const { t } = useTranslation()
const route = useRoute()
const password = ref<string | undefined>(undefined)
const decrypted = ref(false)
const paste = ref<Paste | undefined>(undefined)

useTitle('Reader & Share Modes | Pastefy')

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

const presentationParts = computed(() =>
  multiPasteParts.value.filter((part) => resolveSharePreviewType(part.name)),
)

const modes = computed(() => {
  if (!paste.value) return []

  const origin = window.location.origin
  const items: Array<{
    key: string
    label: string
    description: string
    icon: string
    url: string
  }> = [
    {
      key: 'default',
      get label() { return t('views.readerShareModesView.options.defaultPaste') },
      get description() { return t('views.readerShareModesView.descriptions.theFullPastefyPastePageWithCodeActionsMetadataAndSharingTools') },
      icon: 'file-code',
      url: `${origin}/${paste.value.id}`,
    },
    {
      key: 'raw',
      get label() { return t('views.readerShareModesView.options.rawFile') },
      get description() { return t('views.readerShareModesView.descriptions.thePlainRawFileUrlForDirectDownloadsScriptsAndIntegrations') },
      icon: 'file-text',
      url: paste.value.raw_url || `${origin}/${paste.value.id}/raw`,
    },
  ]

  if (paste.value.type === 'PASTE' && findFromFileName(paste.value.title || '') === 'markdown') {
    items.push({
      key: 'article',
      get label() { return t('views.readerShareModesView.options.articleView') },
      get description() { return t('views.readerShareModesView.descriptions.aReaderFriendlyLayoutForSharingMarkdownLikeaPolishedArticle') },
      icon: 'article',
      url: `${origin}/${paste.value.id}/article`,
    })
  }

  if (paste.value.type === 'PASTE' && resolveSharePreviewType(paste.value.title)) {
    items.push({
      key: 'presentation',
      get label() { return t('views.readerShareModesView.options.presentationView') },
      get description() { return t('views.readerShareModesView.descriptions.aFocusedPreviewPageForTablesDiagramsMapsSvgsDiffsJsonHtmlAndTerminalReplays') },
      icon: 'presentation',
      url: `${origin}/${paste.value.id}/presentation`,
    })
  }

  if (paste.value.type === 'MULTI_PASTE' && markdownParts.value.length) {
    for (const part of markdownParts.value) {
      items.push({
        key: `article-${part.name}`,
        label: `Article: ${toSharePreviewLabel(part.name)}`,
        get description() { return t('views.readerShareModesView.descriptions.aDirectArticleLinkToThisMarkdownFileInsideTheMultiPaste') },
        icon: 'article',
        url: `${origin}/${paste.value.id}/article?part=${encodeURIComponent(part.name)}`,
      })
    }
  }

  if (paste.value.type === 'MULTI_PASTE' && presentationParts.value.length) {
    for (const part of presentationParts.value) {
      items.push({
        key: `presentation-${part.name}`,
        label: `Presentation: ${toSharePreviewLabel(part.name)}`,
        get description() { return t('views.readerShareModesView.descriptions.aDirectPresentationLinkToThisPreviewableFileInsideTheMultiPaste') },
        icon: 'presentation',
        url: `${origin}/${paste.value.id}/presentation?part=${encodeURIComponent(part.name)}`,
      })
    }
  }

  return items
})

const decrypt = () => {
  if (!paste.value || !password.value) return

  const decryptedContent = CryptoJS.AES.decrypt(paste.value.content, password.value).toString(
    CryptoJS.enc.Utf8,
  )
  if (decryptedContent === '') return

  paste.value.title = CryptoJS.AES.decrypt(paste.value.title, password.value).toString(
