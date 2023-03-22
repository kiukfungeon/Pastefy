<script setup lang="ts">
import { useTranslation } from 'i18next-vue'
import Button from 'primevue/button'
import Select from 'primevue/select'
import Textarea from 'primevue/textarea'
import { computed } from 'vue'
import { useClipboard, useStorage } from '@vueuse/core'
import UtilityShell from '@/components/tools/utility/UtilityShell.vue'
import UtilityResultActions from '@/components/tools/utility/UtilityResultActions.vue'

const { t } = useTranslation()
const clipboard = useClipboard()
const mode = useStorage<'escape' | 'unescape'>('pastefy-utility-regex-mode', 'escape')
const input = useStorage('pastefy-utility-regex-input', 'https://pastefy.app/tools?tag=markdown')
const modes = [
  { get label() { return t('utility.regexEscapeTool.options.escape') }, value: 'escape' },
  { get label() { return t('utility.regexEscapeTool.options.unescape') }, value: 'unescape' },
]

const output = computed(() =>
  mode.value === 'escape'
    ? input.value.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')
    : input.value.replace(/\\([.*+?^${}()|[\]\\])/g, '$1'),
)
</script>
