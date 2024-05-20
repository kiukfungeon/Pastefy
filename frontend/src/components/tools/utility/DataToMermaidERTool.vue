<script setup lang="ts">
import { useTranslation } from 'i18next-vue'
import InputText from 'primevue/inputtext'
import Select from 'primevue/select'
import Textarea from 'primevue/textarea'
import Checkbox from 'primevue/checkbox'
import { computed } from 'vue'
import { useStorage } from '@vueuse/core'

import MermaidToolResult from '@/components/tools/utility/MermaidToolResult.vue'
import UtilityShell from '@/components/tools/utility/UtilityShell.vue'
import { inferSchemaGraph, toMermaidERDiagram } from '@/utils/schema-diagrams.ts'
import { parseStructuredData, type StructuredInputFormat } from '@/utils/structured-data.ts'

const { t } = useTranslation()
const source = useStorage(
  'pastefy-utility-data-er-source',
  `{
  "users": [
    {
      "id": 1,
      "name": "Ada",
      "profile": { "avatar": "ada.png", "bio": "Engineer" },
      "posts": [
        { "id": 10, "title": "Hello", "published": true }
      ]
    }
  ]
}`,
)
const inputFormat = useStorage<StructuredInputFormat>('pastefy-utility-data-er-format', 'auto')
const rootEntity = useStorage('pastefy-utility-data-er-root', 'Dataset')
const includeTypes = useStorage('pastefy-utility-data-er-types', true)

const formatOptions = [
  { get label() { return t('utility.dataToMermaidErTool.options.autoDetect') }, value: 'auto' as StructuredInputFormat },
  { get label() { return t('utility.dataToMermaidErTool.options.json') }, value: 'json' as StructuredInputFormat },
  { get label() { return t('utility.dataToMermaidErTool.options.yaml') }, value: 'yaml' as StructuredInputFormat },
  { get label() { return t('utility.dataToMermaidErTool.options.csv') }, value: 'csv' as StructuredInputFormat },
]

const diagram = computed(() => {
