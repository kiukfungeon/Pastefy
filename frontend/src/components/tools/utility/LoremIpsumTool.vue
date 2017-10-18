<script setup lang="ts">
import { useTranslation } from 'i18next-vue'
import Button from 'primevue/button'
import InputNumber from 'primevue/inputnumber'
import Select from 'primevue/select'
import { computed, ref, watch } from 'vue'
import { useStorage } from '@vueuse/core'
import UtilityShell from '@/components/tools/utility/UtilityShell.vue'
import UtilityResultActions from '@/components/tools/utility/UtilityResultActions.vue'

const { t } = useTranslation()
const mode = useStorage<'paragraphs' | 'sentences' | 'words'>('pastefy-utility-lorem-mode', 'paragraphs')
const count = useStorage('pastefy-utility-lorem-count', 3)
const output = ref('')

const modeOptions = [
  { get label() { return t('utility.loremIpsumTool.options.paragraphs') }, value: 'paragraphs' },
  { get label() { return t('utility.loremIpsumTool.options.sentences') }, value: 'sentences' },
  { get label() { return t('utility.loremIpsumTool.options.words') }, value: 'words' },
]

const corpus = 'lorem ipsum dolor sit amet consectetur adipiscing elit sed do eiusmod tempor incididunt ut labore et dolore magna aliqua ut enim ad minim veniam quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat'.split(
  ' ',
)

const wordAt = (index: number) => corpus[index % corpus.length]

const generate = () => {
  const target = Math.max(1, Math.min(count.value || 1, 20))
  if (mode.value === 'words') {
    output.value = Array.from({ length: target * 25 }, (_, index) => wordAt(index)).join(' ')
    return
  }

  if (mode.value === 'sentences') {
    output.value = Array.from({ length: target * 4 }, (_, index) => {
      const start = index * 10
      const sentence = Array.from({ length: 10 }, (_, wordIndex) => wordAt(start + wordIndex)).join(' ')
      return `${sentence.charAt(0).toUpperCase()}${sentence.slice(1)}.`
    }).join(' ')
    return
