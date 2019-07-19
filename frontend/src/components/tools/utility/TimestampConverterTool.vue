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
