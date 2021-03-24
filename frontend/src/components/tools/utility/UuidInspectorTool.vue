<script setup lang="ts">
import InputText from 'primevue/inputtext'
import Message from 'primevue/message'
import { computed } from 'vue'
import { useStorage } from '@vueuse/core'
import UtilityShell from '@/components/tools/utility/UtilityShell.vue'
import UtilityResultActions from '@/components/tools/utility/UtilityResultActions.vue'

const input = useStorage('pastefy-utility-uuid-input', crypto.randomUUID())

const state = computed(() => {
  const value = input.value.trim().toLowerCase()
  const match = value.match(
    /^([0-9a-f]{8})-([0-9a-f]{4})-([1-8][0-9a-f]{3})-([89ab][0-9a-f]{3})-([0-9a-f]{12})$/,
  )

  if (!match) {
    return { error: 'Input is not a valid canonical UUID.' }
  }

  const version = Number.parseInt(match[3][0], 16)
  const variantNibble = Number.parseInt(match[4][0], 16)
  const variant =
    (variantNibble & 0b1000) === 0
      ? 'NCS'
      : (variantNibble & 0b1100) === 0b1000
        ? 'RFC 4122 / RFC 9562'
        : (variantNibble & 0b1110) === 0b1100
          ? 'Microsoft'
          : 'Future'

  return {
    value,
    version,
    variant,
    nil: value === '00000000-0000-0000-0000-000000000000',
    error: undefined,
