<script setup lang="ts">
import { useTranslation } from 'i18next-vue'
import Button from 'primevue/button'
import Message from 'primevue/message'
import Select from 'primevue/select'
import Textarea from 'primevue/textarea'
import { computed } from 'vue'
import { useClipboard, useStorage } from '@vueuse/core'
import UtilityShell from '@/components/tools/utility/UtilityShell.vue'
import UtilityResultActions from '@/components/tools/utility/UtilityResultActions.vue'

const { t } = useTranslation()
const clipboard = useClipboard()
const mode = useStorage<'encode' | 'decode'>('pastefy-utility-url-mode', 'encode')
const input = useStorage('pastefy-utility-url-input', 'https://pastefy.app/tools?tab=utilities&query=hello world')

const modeOptions = [
  { get label() { return t('utility.urlEncoderTool.options.encode') }, value: 'encode' },
  { get label() { return t('utility.urlEncoderTool.options.decode') }, value: 'decode' },
]

const state = computed(() => {
  try {
    return {
      value: mode.value === 'encode' ? encodeURIComponent(input.value) : decodeURIComponent(input.value),
      error: undefined,
    }
  } catch {
    return {
      value: '',
      error: 'Input could not be decoded as a valid encoded URL component.',
    }
