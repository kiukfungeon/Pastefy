<script setup lang="ts">
import Button from 'primevue/button'
import { ref, toValue, type MaybeRefOrGetter } from 'vue'
import { useClipboard } from '@vueuse/core'
import { useCurrentPasteStore } from '@/stores/current-paste.ts'
import { client } from '@/main.ts'
import router from '@/router'
import type { Paste } from '@/types/paste.ts'

const props = defineProps<{
  result: MaybeRefOrGetter<string>
  fileName: MaybeRefOrGetter<string>
}>()

const clipboard = useClipboard()
const currentPaste = useCurrentPasteStore()
const isCreatingPaste = ref(false)

const resolvedResult = () => toValue(props.result)
const resolvedFileName = () => toValue(props.fileName)

const copyResult = async () => {
  if (!resolvedResult()) return
  await clipboard.copy(resolvedResult())
}

const openInPasteEditor = async () => {
  if (!resolvedResult()) return
  currentPaste.clear()
  currentPaste.type = 'PASTE'
  currentPaste.title = resolvedFileName()
  currentPaste.contents = resolvedResult()
  await router.push({ name: 'home-page' })
}

const createPaste = async () => {
  if (!resolvedResult()) return
  isCreatingPaste.value = true
  try {
    const res = await client.post('/api/v2/paste', {
      title: resolvedFileName(),
      content: resolvedResult(),
      encrypted: false,
      visibility: 'UNLISTED',
      type: 'PASTE',
      ai: true,
