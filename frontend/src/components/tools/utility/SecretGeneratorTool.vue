<script setup lang="ts">
import { useTranslation } from 'i18next-vue'
import Button from 'primevue/button'
import InputNumber from 'primevue/inputnumber'
import Select from 'primevue/select'
import { computed, ref, watch } from 'vue'
import { useClipboard, useStorage } from '@vueuse/core'
import UtilityShell from '@/components/tools/utility/UtilityShell.vue'
import UtilityResultActions from '@/components/tools/utility/UtilityResultActions.vue'

const { t } = useTranslation()
const clipboard = useClipboard()
const mode = useStorage<'uuid' | 'hex' | 'base64url'>('pastefy-utility-secret-mode', 'uuid')
const count = useStorage('pastefy-utility-secret-count', 3)
const length = useStorage('pastefy-utility-secret-length', 32)
const generatedValues = ref<string[]>([])

const modeOptions = [
  { get label() { return t('utility.secretGeneratorTool.options.uuid') }, value: 'uuid' },
  { get label() { return t('utility.secretGeneratorTool.options.hexToken') }, value: 'hex' },
  { get label() { return t('utility.secretGeneratorTool.options.base64UrlToken') }, value: 'base64url' },
]

const randomChars = (charset: string, targetLength: number) => {
  const values = new Uint32Array(targetLength)
  window.crypto.getRandomValues(values)
  return Array.from(values, (value) => charset[value % charset.length]).join('')
}

