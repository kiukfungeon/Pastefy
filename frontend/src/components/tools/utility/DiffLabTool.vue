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
