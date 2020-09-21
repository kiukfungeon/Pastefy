<script setup lang="ts">
import Button from 'primevue/button'
import Textarea from 'primevue/textarea'
import { computed, ref, watch } from 'vue'
import { useClipboard, useStorage } from '@vueuse/core'
import UtilityShell from '@/components/tools/utility/UtilityShell.vue'
import UtilityResultActions from '@/components/tools/utility/UtilityResultActions.vue'

const clipboard = useClipboard()
const hashInput = useStorage('pastefy-utility-hash-input', 'Pastefy makes sharing snippets feel fast and polished.')
const hashResults = ref<{ label: string; value: string }[]>([])

const toHex = (buffer: ArrayBuffer) =>
  Array.from(new Uint8Array(buffer))
    .map((byte) => byte.toString(16).padStart(2, '0'))
    .join('')

const calculateHashes = async () => {
  const input = new TextEncoder().encode(hashInput.value)
  if (!window.crypto?.subtle) {
    hashResults.value = []
    return
  }

  hashResults.value = await Promise.all(
    ['SHA-1', 'SHA-256', 'SHA-384', 'SHA-512'].map(async (algorithm) => ({
      label: algorithm,
      value: toHex(await window.crypto.subtle.digest(algorithm, input)),
    })),
  )
}

watch(hashInput, calculateHashes, { immediate: true })
