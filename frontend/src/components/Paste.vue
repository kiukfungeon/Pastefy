<script lang="ts" setup>
import Button from 'primevue/button'
import InputText from 'primevue/inputtext'
import Popover from 'primevue/popover'
import { useAsyncState, useClipboard, useTitle } from '@vueuse/core'
import { client, eventBus } from '@/main.ts'

import { computed, defineAsyncComponent, ref, useTemplateRef, watch } from 'vue'
import { useCurrentPasteStore } from '@/stores/current-paste.ts'
import PasteVisibilityIcon from '@/components/PasteVisibilityIcon.vue'
import type { MultiPastePart, Paste } from '@/types/paste.ts'
import { getIconByFileName } from '@/utils/lang-information.ts'
import CryptoJS from 'crypto-js'
import { findFromFileName } from '@/utils/lang-replacements.ts'
import { useConfirm } from 'primevue/useconfirm'
import router from '@/router'
import { useAppInfoStore } from '@/stores/app-info.ts'
import ErrorContainer from '@/components/ErrorContainer.vue'
import LoadingContainer from '@/components/LoadingContainer.vue'
import { useCurrentUserStore } from '@/stores/current-user.ts'
import PastePreview from '@/components/PastePreview.vue'
import { useConfig } from '@/composables/config.ts'
import { useTagsStore } from '@/stores/tags-store.ts'
import type { PopoverMethods } from 'primevue'
import ComponentInjection from '@/components/ComponentInjection.vue'
import PasteSharingPopover from '@/components/popovers/PasteSharingPopover.vue'
import PasteStarButton from '@/components/paste/PasteStarButton.vue'
import { useTranslation } from 'i18next-vue'
import PasteAiSummary from '@/components/PasteAiSummary.vue'
import SeverityScore from '@/components/paste/SeverityScore.vue'
import PasteComments from '@/components/paste/PasteComments.vue'
import type { PasteCommentMarker } from '@/types/paste-comment.ts'
const Highlighted = defineAsyncComponent(() => import('@/components/Highlighted.vue'))

const props = defineProps<{
  pasteId: string
  asEmbed?: boolean
  hideTitle?: boolean
  hideActions?: boolean
  hideEmbedBottom?: boolean
}>()

const password = ref<string | undefined>(undefined)

const currentPasteStore = useCurrentPasteStore()

const clipboard = useClipboard()

const content = ref<string | undefined>()
const title = ref<string | undefined>()

const currentFileName = ref<string | undefined>()

const multiPasteParts = ref<MultiPastePart[] | undefined>(undefined)
const multiPartIndex = ref(0)

const emit = defineEmits(['update-height'])

const selectMultiPart = (index: number) => {
  content.value = multiPasteParts.value![index]?.contents ?? ''
  currentFileName.value = multiPasteParts.value![index]?.name ?? ''
  multiPartIndex.value = index
  emit('update-height')
}
const origin = window.location.origin

const paste = ref<Paste | undefined>(undefined)

const tagsStore = useTagsStore()

const { isLoading, error } = useAsyncState(async () => {
  const pasteRes = (
    await client.get(`/api/v2/paste/${props.pasteId}`, {
      params: {
        from_frontend: 'true',
        with_ai_analysis: 'true',
      },
    })
  ).data as Paste

  paste.value = pasteRes
  if (password.value) {
    decrypt()
  } else {
    setValues()
  }

  const hashPw = window.location.hash?.replace('#', '')
  if (hashPw) {
    password.value = hashPw
    decrypt()
  }
  eventBus.emit('pageLoaded', 'paste')
}, undefined)

const { isLoading: aiSummaryLoading, execute: generateAiSummary } = useAsyncState(
  async () => {
    await client.post(`/api/v2/paste/${props.pasteId}/ai-analysis`)
  },
  undefined,
  { immediate: false },
)

const fetchTags = () => {
  let i = 0
  return paste.value?.tags?.filter(() => ++i < 8).forEach(tagsStore.fetchIfNeeded)
}

const setValues = () => {
  if (!paste.value) return

  content.value = paste.value.content
  title.value = paste.value.title

  currentFileName.value = paste.value.title

  useTitle(`${title.value || 'Untitled Paste'} | Pastefy`)

  if (paste.value.type === 'MULTI_PASTE' && (!paste.value.encrypted || decrypted.value)) {
    multiPasteParts.value = JSON.parse(content.value!) as MultiPastePart[]
    selectMultiPart(0)
  }

  emit('update-height')
  setTimeout(() => emit('update-height'), 500)
}

const copied = ref(false)

const decrypted = ref(false)

