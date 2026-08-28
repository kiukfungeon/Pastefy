<script setup lang="ts">
import { useTranslation } from 'i18next-vue'
import InputText from 'primevue/inputtext'
import Select from 'primevue/select'
import Textarea from 'primevue/textarea'
import { computed } from 'vue'
import { useStorage } from '@vueuse/core'

import MermaidToolResult from '@/components/tools/utility/MermaidToolResult.vue'
import UtilityShell from '@/components/tools/utility/UtilityShell.vue'
import { parseYamlDocument, parseYamlDocuments } from '@/utils/yaml.ts'
import { isRecord } from '@/utils/structured-data.ts'

const { t } = useTranslation()
type InfraFormat = 'auto' | 'compose' | 'kubernetes' | 'terraform' | 'github-actions'
type MermaidNode = {
  id: string
  label: string
}
type KubernetesResource = {
  id: string
  kind: string
  name: string
  document: Record<string, unknown>
}

const source = useStorage(
  'pastefy-utility-infra-source',
  `services:
  web:
    image: pastefy/web:latest
    depends_on: [db, redis]
  db:
    image: postgres:16
  redis:
    image: redis:7`,
)
const format = useStorage<InfraFormat>('pastefy-utility-infra-format', 'auto')
const direction = useStorage('pastefy-utility-infra-direction', 'LR')
const graphTitle = useStorage('pastefy-utility-infra-title', 'Infrastructure Overview')

const formatOptions = [
  { get label() { return t('utility.infraToMermaidTool.options.autoDetect') }, value: 'auto' as InfraFormat },
  { get label() { return t('utility.infraToMermaidTool.options.dockerCompose') }, value: 'compose' as InfraFormat },
  { get label() { return t('utility.infraToMermaidTool.options.kubernetes') }, value: 'kubernetes' as InfraFormat },
  { get label() { return t('utility.infraToMermaidTool.options.terraformHcl') }, value: 'terraform' as InfraFormat },
  { get label() { return t('utility.infraToMermaidTool.options.gitHubActions') }, value: 'github-actions' as InfraFormat },
]

const directionOptions = [
  { get label() { return t('utility.infraToMermaidTool.options.leftToRight') }, value: 'LR' },
  { get label() { return t('utility.infraToMermaidTool.options.topToBottom') }, value: 'TD' },
]

const sanitizeNodeId = (...parts: string[]) => {
  const value = parts
    .join('_')
    .replace(/[^a-zA-Z0-9_]/g, '_')
    .replace(/_+/g, '_')
    .replace(/^_+|_+$/g, '')
  return value || 'node'
}

const toRecordArray = (value: unknown): Record<string, unknown>[] =>
  Array.isArray(value) ? value.filter(isRecord) : []

const getNestedRecord = (value: unknown, path: string[]): Record<string, unknown> | undefined => {
  let current: unknown = value
  for (const segment of path) {
    if (!isRecord(current)) return undefined
    current = current[segment]
  }
  return isRecord(current) ? current : undefined
}

const getNestedArray = (value: unknown, path: string[]): unknown[] => {
  const current = path.reduce<unknown>((entry, segment) => {
    if (!isRecord(entry)) return undefined
    return entry[segment]
  }, value)

  return Array.isArray(current) ? current : []
}

const getMetadataLabels = (resource: KubernetesResource) => {
  if (resource.kind === 'Pod') {
