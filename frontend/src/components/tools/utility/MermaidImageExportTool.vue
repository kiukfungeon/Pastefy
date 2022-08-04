<script setup lang="ts">
import { useTranslation } from 'i18next-vue'
import domtoimage from 'dom-to-image'
import Button from 'primevue/button'
import InputNumber from 'primevue/inputnumber'
import InputText from 'primevue/inputtext'
import Select from 'primevue/select'
import Textarea from 'primevue/textarea'
import { computed, ref, useTemplateRef } from 'vue'
import { useStorage } from '@vueuse/core'

import MermaidViewer from '@/components/previews/MermaidViewer.vue'
import UtilityShell from '@/components/tools/utility/UtilityShell.vue'

const { t } = useTranslation()
const source = useStorage(
  'pastefy-utility-mermaid-export-source',
  `flowchart LR
  Client --> API
  API --> DB
  API --> Cache`,
)
const fileName = useStorage('pastefy-utility-mermaid-export-file-name', 'diagram')
const format = useStorage<'png' | 'svg'>('pastefy-utility-mermaid-export-format', 'png')
const scale = useStorage<number>('pastefy-utility-mermaid-export-scale', 2)
const padding = useStorage<number>('pastefy-utility-mermaid-export-padding', 24)
const background = useStorage('pastefy-utility-mermaid-export-background', '#ffffff')
const watermark = useStorage('pastefy-utility-mermaid-export-watermark', 'Created with Pastefy')
const exportAreaRef = useTemplateRef<HTMLDivElement>('exportAreaRef')
const isExporting = ref(false)

const formatOptions = [
  { get label() { return t('utility.mermaidImageExportTool.options.png') }, value: 'png' as const },
  { get label() { return t('utility.mermaidImageExportTool.options.svg') }, value: 'svg' as const },
]

const download = async () => {
  const node = exportAreaRef.value
  if (!node) return
  isExporting.value = true
  try {
    const safeName = (fileName.value || 'diagram').replace(/[^a-zA-Z0-9-_]+/g, '-')
    if (format.value === 'svg') {
      const dataUrl = await (domtoimage as any).toSvg(node, {
        bgcolor: background.value,
        style: {
          transform: `scale(${scale.value})`,
          transformOrigin: 'top left',
          width: `${node.offsetWidth}px`,
          height: `${node.offsetHeight}px`,
        },
        width: node.offsetWidth * scale.value,
        height: node.offsetHeight * scale.value,
      })
      const link = document.createElement('a')
      link.download = `${safeName}.svg`
      link.href = dataUrl
      link.click()
      return
    }

    const blob = await domtoimage.toBlob(node, {
      bgcolor: background.value,
      style: {
        transform: `scale(${scale.value})`,
        transformOrigin: 'top left',
        width: `${node.offsetWidth}px`,
        height: `${node.offsetHeight}px`,
      },
      width: node.offsetWidth * scale.value,
      height: node.offsetHeight * scale.value,
