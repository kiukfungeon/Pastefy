<script setup lang="ts">
import Button from 'primevue/button'
import Textarea from 'primevue/textarea'
import { computed } from 'vue'
import { useClipboard, useStorage } from '@vueuse/core'
import UtilityShell from '@/components/tools/utility/UtilityShell.vue'
import UtilityResultActions from '@/components/tools/utility/UtilityResultActions.vue'

const clipboard = useClipboard()
const input = useStorage('pastefy-utility-case-input', 'Pastefy makes developer tooling feel polished.')

const words = computed(() =>
  input.value
    .normalize('NFKD')
    .replace(/[\u0300-\u036f]/g, '')
    .replace(/[^a-zA-Z0-9]+/g, ' ')
    .trim()
    .split(/\s+/)
    .filter(Boolean),
)

const state = computed(() => {
  const lower = input.value.toLowerCase()
  const upper = input.value.toUpperCase()
  const sentence = input.value
    ? `${input.value.charAt(0).toUpperCase()}${input.value.slice(1).toLowerCase()}`
    : ''
  const title = words.value
    .map((word) => `${word.charAt(0).toUpperCase()}${word.slice(1).toLowerCase()}`)
    .join(' ')
  const camel = words.value
    .map((word, index) =>
      index === 0
        ? word.toLowerCase()
