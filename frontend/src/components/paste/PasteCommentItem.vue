<script setup lang="ts">
import Button from 'primevue/button'
import { computed, ref } from 'vue'
import type { PasteComment } from '@/types/paste-comment.ts'
import PasteCommentForm from '@/components/paste/PasteCommentForm.vue'
import PasteCommentLinePreview from '@/components/paste/PasteCommentLinePreview.vue'

const props = withDefaults(
  defineProps<{
    comment: PasteComment
    canDelete?: boolean
    canReply?: boolean
    submitting?: boolean
    currentUserId?: string
    pasteContents?: string
    pasteFileName?: string
  }>(),
  { canReply: true },
)

const emit = defineEmits<{
  reply: [commentId: number, content: string]
  delete: [commentId: number]
}>()

const replying = ref(false)
