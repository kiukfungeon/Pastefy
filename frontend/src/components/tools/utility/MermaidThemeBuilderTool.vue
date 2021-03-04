<script setup lang="ts">
import { useTranslation } from 'i18next-vue'
import InputNumber from 'primevue/inputnumber'
import InputText from 'primevue/inputtext'
import Select from 'primevue/select'
import Textarea from 'primevue/textarea'
import { computed } from 'vue'
import { useStorage } from '@vueuse/core'

import MermaidToolResult from '@/components/tools/utility/MermaidToolResult.vue'
import UtilityShell from '@/components/tools/utility/UtilityShell.vue'

const { t } = useTranslation()
const source = useStorage(
  'pastefy-utility-mermaid-theme-source',
  `flowchart LR
  User([User]) --> API[API]
  API --> DB[(Database)]
  API --> Cache[(Cache)]`,
)

const theme = useStorage('pastefy-utility-mermaid-theme-name', 'base')
const fontFamily = useStorage('pastefy-utility-mermaid-theme-font', "'Plus Jakarta Sans Variable', sans-serif")
const primaryColor = useStorage('pastefy-utility-mermaid-primary-color', '#dbeafe')
const primaryTextColor = useStorage('pastefy-utility-mermaid-primary-text', '#111827')
const primaryBorderColor = useStorage('pastefy-utility-mermaid-primary-border', '#2563eb')
const lineColor = useStorage('pastefy-utility-mermaid-line-color', '#475569')
const tertiaryColor = useStorage('pastefy-utility-mermaid-tertiary-color', '#f5f3ff')
const background = useStorage('pastefy-utility-mermaid-background', '#ffffff')
const curve = useStorage('pastefy-utility-mermaid-curve', 'basis')
const nodeSpacing = useStorage('pastefy-utility-mermaid-node-spacing', 40)
const rankSpacing = useStorage('pastefy-utility-mermaid-rank-spacing', 50)
const diagramPadding = useStorage('pastefy-utility-mermaid-diagram-padding', 8)

const themeOptions = [
  { get label() { return t('utility.mermaidThemeBuilderTool.options.base') }, value: 'base' },
  { get label() { return t('utility.mermaidThemeBuilderTool.options.default') }, value: 'default' },
  { get label() { return t('utility.mermaidThemeBuilderTool.options.dark') }, value: 'dark' },
  { get label() { return t('utility.mermaidThemeBuilderTool.options.forest') }, value: 'forest' },
  { get label() { return t('utility.mermaidThemeBuilderTool.options.neutral') }, value: 'neutral' },
]

const curveOptions = [
  { get label() { return t('utility.mermaidThemeBuilderTool.options.basis') }, value: 'basis' },
  { get label() { return t('utility.mermaidThemeBuilderTool.options.linear') }, value: 'linear' },
  { get label() { return t('utility.mermaidThemeBuilderTool.options.step') }, value: 'stepBefore' },
  { get label() { return t('utility.mermaidThemeBuilderTool.options.monotone') }, value: 'monotoneX' },
]

