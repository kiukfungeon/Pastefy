<script setup lang="ts">
import Button from 'primevue/button'
import Textarea from 'primevue/textarea'
import { computed } from 'vue'
import { useClipboard, useStorage } from '@vueuse/core'
import Highlighted from '@/components/Highlighted.vue'
import UtilityShell from '@/components/tools/utility/UtilityShell.vue'
import UtilityResultActions from '@/components/tools/utility/UtilityResultActions.vue'

const clipboard = useClipboard()
const input = useStorage('pastefy-utility-query-input', 'page=1&sort=created_at&tag=markdown&tag=json')

const entries = computed(() => {
  const params = new URLSearchParams(input.value.startsWith('?') ? input.value.slice(1) : input.value)
  return Array.from(params.entries())
})

const grouped = computed(() => {
  const output: Record<string, string[]> = {}
  entries.value.forEach(([key, value]) => {
    output[key] ||= []
    output[key].push(value)
  })
  return output
})

const rebuilt = computed(() => new URLSearchParams(entries.value).toString())
const result = computed(() =>
  JSON.stringify(
    {
      entries: entries.value.map(([key, value]) => ({ key, value })),
      grouped: grouped.value,
      rebuilt: rebuilt.value,
