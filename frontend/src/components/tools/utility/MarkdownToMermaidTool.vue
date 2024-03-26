<script setup lang="ts">
import { useTranslation } from 'i18next-vue'
import InputNumber from 'primevue/inputnumber'
import Select from 'primevue/select'
import Textarea from 'primevue/textarea'
import { computed } from 'vue'
import { useStorage } from '@vueuse/core'

import MermaidToolResult from '@/components/tools/utility/MermaidToolResult.vue'
import UtilityShell from '@/components/tools/utility/UtilityShell.vue'
import { toMermaidQuotedLabel, toMermaidSafeIdentifier } from '@/utils/mermaid.ts'

const { t } = useTranslation()
type Mode = 'mindmap' | 'flowchart'

const source = useStorage(
  'pastefy-utility-markdown-mermaid-source',
  `# Pastefy
## Tools
- Mermaid
- Search
## Admin
- Users
- Pastes`,
)
const mode = useStorage<Mode>('pastefy-utility-markdown-mermaid-mode', 'mindmap')
const maxDepth = useStorage('pastefy-utility-markdown-mermaid-depth', 4)
const direction = useStorage('pastefy-utility-markdown-mermaid-direction', 'TD')

const modeOptions = [
