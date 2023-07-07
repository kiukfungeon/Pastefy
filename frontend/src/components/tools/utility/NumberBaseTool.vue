<script setup lang="ts">
import { useTranslation } from 'i18next-vue'
import InputText from 'primevue/inputtext'
import Message from 'primevue/message'
import Select from 'primevue/select'
import { computed } from 'vue'
import { useStorage } from '@vueuse/core'
import UtilityShell from '@/components/tools/utility/UtilityShell.vue'
import UtilityResultActions from '@/components/tools/utility/UtilityResultActions.vue'

const { t } = useTranslation()
const input = useStorage('pastefy-utility-number-base-input', '255')
const base = useStorage<'2' | '8' | '10' | '16'>('pastefy-utility-number-base', '10')
const bases = [
  { get label() { return t('utility.numberBaseTool.options.binary') }, value: '2' },
  { get label() { return t('utility.numberBaseTool.options.octal') }, value: '8' },
  { get label() { return t('utility.numberBaseTool.options.decimal') }, value: '10' },
