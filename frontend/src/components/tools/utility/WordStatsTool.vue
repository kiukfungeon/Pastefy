<script setup lang="ts">
import Textarea from 'primevue/textarea'
import { computed } from 'vue'
import { useStorage } from '@vueuse/core'
import UtilityShell from '@/components/tools/utility/UtilityShell.vue'
import UtilityResultActions from '@/components/tools/utility/UtilityResultActions.vue'

const input = useStorage(
  'pastefy-utility-word-stats-input',
  `Pastefy gives you a fast place to shape content before you share it.

This tool counts much more than just words. It also shows reading time, sentence and paragraph counts, average word length, and the most common words in your text.`,
)

const words = computed(() => input.value.match(/\b[\p{L}\p{N}'-]+\b/gu) || [])
const lines = computed(() => (input.value === '' ? 0 : input.value.split(/\r?\n/).length))
const paragraphs = computed(() =>
  input.value.trim() === '' ? 0 : input.value.trim().split(/\n\s*\n/).filter(Boolean).length,
)
const sentences = computed(() => input.value.split(/[.!?]+/).map((entry) => entry.trim()).filter(Boolean).length)
const characters = computed(() => input.value.length)
const charactersNoSpaces = computed(() => input.value.replace(/\s/g, '').length)
const uniqueWords = computed(() => new Set(words.value.map((word) => word.toLowerCase())).size)
const averageWordLength = computed(() =>
  words.value.length
