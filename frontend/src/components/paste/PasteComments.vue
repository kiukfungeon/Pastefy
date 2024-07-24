<script setup lang="ts">
import Drawer from 'primevue/drawer'
import Divider from 'primevue/divider'
import Popover from 'primevue/popover'
import { useAsyncState } from '@vueuse/core'
import { client } from '@/main.ts'
import { computed, ref, useTemplateRef, watch } from 'vue'
import type { PopoverMethods } from 'primevue'
import type { CreatePasteComment, PasteComment, PasteCommentMarker } from '@/types/paste-comment.ts'
import { useCurrentUserStore } from '@/stores/current-user.ts'
import ErrorContainer from '@/components/ErrorContainer.vue'
import LoadingContainer from '@/components/LoadingContainer.vue'
import Pagination from '@/components/Pagination.vue'
import PasteCommentForm from '@/components/paste/PasteCommentForm.vue'
import PasteCommentItem from '@/components/paste/PasteCommentItem.vue'

const props = defineProps<{
  pasteId: string
  pasteUserId?: string
  pasteContents?: string
  pasteFileName?: string
  enableLineComments?: boolean
}>()

const emit = defineEmits<{
  markersUpdated: [markers: PasteCommentMarker[]]
}>()

const page = ref(1)
const pageLimit = 10
const formKey = ref(0)
const selectedLine = ref<number>()
const dialogVisible = ref(false)
const currentUser = useCurrentUserStore()
const lineCommentPopover = useTemplateRef<PopoverMethods>('lineCommentPopover')

const canDelete = computed(
  () =>
    currentUser.user?.type === 'ADMIN' ||
    (!!props.pasteUserId && currentUser.user?.id === props.pasteUserId),
)

const {
  state: comments,
  isLoading,
  error,
  execute: loadComments,
} = useAsyncState(async () => {
  return (
    await client.get(`/api/v2/paste/${props.pasteId}/comments`, {
      params: { page: page.value, page_limit: pageLimit },
    })
  ).data as PasteComment[]
}, undefined)

const { execute: loadMarkers } = useAsyncState(async () => {
  if (!props.enableLineComments) return []
  const markers = (await client.get(`/api/v2/paste/${props.pasteId}/comments/markers`))
    .data as PasteCommentMarker[]
  emit('markersUpdated', markers)
  return markers
}, [])

const {
  state: lineComments,
  isLoading: lineCommentsLoading,
  error: lineCommentsError,
  execute: loadLineComments,
} = useAsyncState(
  async () => {
    if (!selectedLine.value) return []

    return (
      await client.get(`/api/v2/paste/${props.pasteId}/comments`, {
        params: { line: selectedLine.value, page_limit: 30 },
      })
    ).data as PasteComment[]
  },
  [],
  { immediate: false },
)

const reloadComments = async () => {
  await Promise.all([
    loadComments(),
    loadMarkers(),
    ...(selectedLine.value ? [loadLineComments()] : []),
  ])
}

const {
  isLoading: isSubmitting,
  error: submitError,
  execute: createComment,
} = useAsyncState(
  async (request: CreatePasteComment) => {
    await client.post(`/api/v2/paste/${props.pasteId}/comments`, request)
    formKey.value++
    await reloadComments()
  },
  undefined,
  { immediate: false },
)

const { execute: deleteComment } = useAsyncState(
  async (commentId: number) => {
    await client.delete(`/api/v2/paste/${props.pasteId}/comments/${commentId}`)
    await reloadComments()
  },
  undefined,
  { immediate: false },
)

const submit = (request: CreatePasteComment) => createComment(0, request)

const submitRoot = (content: string) => {
  selectedLine.value = undefined
  page.value = 1
  return submit({ content })
}

