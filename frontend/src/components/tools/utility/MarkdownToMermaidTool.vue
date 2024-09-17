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
  { get label() { return t('utility.markdownToMermaidTool.options.mindmap') }, value: 'mindmap' as Mode },
  { get label() { return t('utility.markdownToMermaidTool.options.flowchart') }, value: 'flowchart' as Mode },
]

const directionOptions = [
  { get label() { return t('utility.markdownToMermaidTool.options.topToBottom') }, value: 'TD' },
  { get label() { return t('utility.markdownToMermaidTool.options.leftToRight') }, value: 'LR' },
]

const diagram = computed(() => {
  const lines = source.value.split(/\r?\n/).filter(Boolean)
  const nodes = lines
    .map((line) => {
      const heading = line.match(/^(#+)\s+(.*)$/)
      if (heading) {
        return { depth: heading[1].length, label: heading[2].trim() }
      }
      const bullet = line.match(/^(\s*)[-*+]\s+(.*)$/)
      if (bullet) {
        return { depth: Math.floor(bullet[1].length / 2) + 1, label: bullet[2].trim() }
      }
      return null
    })
    .filter((entry): entry is { depth: number; label: string } => !!entry)
    .filter((entry) => entry.depth <= maxDepth.value)

  if (!nodes.length) return '%% No headings or bullet lists found.'

  if (mode.value === 'mindmap') {
    return [
      'mindmap',
      '  root((Document))',
      ...nodes.map((node, index) => {
        const id = toMermaidSafeIdentifier(`node_${index + 1}`, 'node')
        return `${'  '.repeat(node.depth + 1)}${id}[${toMermaidQuotedLabel(node.label)}]`
      }),
    ].join('\n')
  }

  const out = [`flowchart ${direction.value}`]
  const stack: { id: string; depth: number }[] = []

  nodes.forEach((node, index) => {
    const id = toMermaidSafeIdentifier(`n${index + 1}`, 'node')
    out.push(`  ${id}[${toMermaidQuotedLabel(node.label)}]`)
    while (stack.length && stack[stack.length - 1].depth >= node.depth) {
      stack.pop()
    }
    if (stack.length) {
      out.push(`  ${stack[stack.length - 1].id} --> ${id}`)
    }
    stack.push({ id, depth: node.depth })
  })

  return out.join('\n')
})
</script>

<template>
  <UtilityShell
