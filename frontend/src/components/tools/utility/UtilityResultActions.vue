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
