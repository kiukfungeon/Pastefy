<script setup lang="ts">
import { useTranslation } from 'i18next-vue'
import Button from 'primevue/button'
import InputText from 'primevue/inputtext'
import Select from 'primevue/select'
import Textarea from 'primevue/textarea'
import { computed } from 'vue'
import { useStorage } from '@vueuse/core'
import { createTwoFilesPatch, diffChars, diffLines, diffWords } from 'diff'

import DiffViewer from '@/components/previews/DiffViewer.vue'
import UtilityResultActions from '@/components/tools/utility/UtilityResultActions.vue'
import UtilityShell from '@/components/tools/utility/UtilityShell.vue'

const { t } = useTranslation()
type DiffMode = 'lines' | 'words' | 'chars'

const left = useStorage('pastefy-utility-diff-left', 'hello\nworld\nold line')
const right = useStorage('pastefy-utility-diff-right', 'hello\nPastefy\nnew line')
const leftName = useStorage('pastefy-utility-diff-left-name', 'before.txt')
const rightName = useStorage('pastefy-utility-diff-right-name', 'after.txt')
const mode = useStorage<DiffMode>('pastefy-utility-diff-mode', 'lines')

const modeOptions = [
  { get label() { return t('utility.diffLabTool.options.lineDiff') }, value: 'lines' as DiffMode },
  { get label() { return t('utility.diffLabTool.options.wordDiff') }, value: 'words' as DiffMode },
  { get label() { return t('utility.diffLabTool.options.characterDiff') }, value: 'chars' as DiffMode },
]

const patch = computed(() =>
  createTwoFilesPatch(leftName.value, rightName.value, left.value, right.value),
)

const stats = computed(() => {
  const changes =
    mode.value === 'words'
      ? diffWords(left.value, right.value)
      : mode.value === 'chars'
        ? diffChars(left.value, right.value)
        : diffLines(left.value, right.value)
