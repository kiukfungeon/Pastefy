<script setup lang="ts">
import Message from 'primevue/message'
import Textarea from 'primevue/textarea'
import { computed } from 'vue'
import { useNow, useStorage } from '@vueuse/core'
import Highlighted from '@/components/Highlighted.vue'
import UtilityShell from '@/components/tools/utility/UtilityShell.vue'
import UtilityResultActions from '@/components/tools/utility/UtilityResultActions.vue'

const jwtInput = useStorage(
  'pastefy-utility-jwt-input',
  'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IlBhc3RlZnkiLCJhZG1pbiI6dHJ1ZSwiaWF0IjoxNzEwNjYyNDAwLCJleHAiOjE4OTM0NTYwMDB9.signature',
)

const now = useNow({ interval: 1000 })

const base64UrlDecode = (value: string) => {
  const normalized = value.replace(/-/g, '+').replace(/_/g, '/')
  const padded = normalized.padEnd(Math.ceil(normalized.length / 4) * 4, '=')
  return atob(padded)
}

const jwtState = computed(() => {
  const token = jwtInput.value.trim()
  if (!token) return { error: 'Enter a JWT to inspect it.' }

  const parts = token.split('.')
  if (parts.length < 2) return { error: 'JWTs need at least header and payload sections.' }

  try {
    return {
      header: JSON.parse(base64UrlDecode(parts[0])),
      payload: JSON.parse(base64UrlDecode(parts[1])),
      signature: parts[2] || '',
      error: undefined,
    }
  } catch {
    return { error: 'Could not decode this JWT. Check that the token is base64url encoded.' }
  }
})

const formatTimestamp = (value: unknown) =>
