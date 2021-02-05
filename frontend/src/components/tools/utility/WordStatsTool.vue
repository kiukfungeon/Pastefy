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
    ? (words.value.reduce((sum, word) => sum + word.length, 0) / words.value.length).toFixed(1)
    : '0.0',
)
const readingTimeMinutes = computed(() =>
  words.value.length ? Math.max(1, Math.ceil(words.value.length / 200)) : 0,
)
const longestWord = computed(() =>
  words.value.reduce((longest, word) => (word.length > longest.length ? word : longest), ''),
)

const topWords = computed(() => {
  const frequencies = new Map<string, number>()
  words.value
    .map((word) => word.toLowerCase())
    .filter((word) => word.length > 2)
    .forEach((word) => {
      frequencies.set(word, (frequencies.get(word) || 0) + 1)
    })

  return Array.from(frequencies.entries())
    .sort((a, b) => b[1] - a[1] || a[0].localeCompare(b[0]))
    .slice(0, 8)
})

const result = computed(() =>
