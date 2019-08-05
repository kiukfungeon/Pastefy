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
const repliesExpanded = ref(false)
const showDelete = computed(
  () =>
    props.canDelete || (!!props.currentUserId && props.comment.user?.id === props.currentUserId),
)

const reply = (content: string) => {
  emit('reply', props.comment.id, content)
  replying.value = false
}

const formatDate = (value: string) => new Date(value).toLocaleString()
</script>

<template>
  <article class="flex gap-2">
    <span
      class="flex h-8 w-8 shrink-0 items-center justify-center overflow-hidden rounded-full bg-neutral-200 text-xs font-bold text-neutral-600 dark:bg-neutral-700 dark:text-neutral-200"
    >
      <img
        v-if="comment.user?.avatar"
        :src="comment.user.avatar"
        alt=""
        class="h-full w-full object-cover"
      />
      <span v-else>{{
