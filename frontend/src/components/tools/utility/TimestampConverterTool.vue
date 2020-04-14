<script setup lang="ts">
import Button from 'primevue/button'
import InputText from 'primevue/inputtext'
import Message from 'primevue/message'
import { computed } from 'vue'
import { useStorage } from '@vueuse/core'
import UtilityShell from '@/components/tools/utility/UtilityShell.vue'
import UtilityResultActions from '@/components/tools/utility/UtilityResultActions.vue'

const input = useStorage('pastefy-utility-timestamp-input', String(Math.floor(Date.now() / 1000)))

const state = computed(() => {
  const raw = input.value.trim()
  if (!raw) return { error: 'Enter a Unix timestamp or date string.' }

  let date: Date | undefined
  if (/^\d+$/.test(raw)) {
    const numeric = Number(raw)
    date = new Date(raw.length > 10 ? numeric : numeric * 1000)
  } else {
    const parsed = new Date(raw)
    if (!Number.isNaN(parsed.getTime())) date = parsed
  }

  if (!date || Number.isNaN(date.getTime())) {
    return { error: 'Could not parse this timestamp or date input.' }
  }

  return {
    unixSeconds: Math.floor(date.getTime() / 1000),
    unixMillis: date.getTime(),
    iso: date.toISOString(),
