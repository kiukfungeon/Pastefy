<script setup lang="ts">
import { useTranslation } from 'i18next-vue'
import Checkbox from 'primevue/checkbox'
import InputText from 'primevue/inputtext'
import Select from 'primevue/select'
import Textarea from 'primevue/textarea'
import { computed } from 'vue'
import { useStorage } from '@vueuse/core'

import MermaidToolResult from '@/components/tools/utility/MermaidToolResult.vue'
import UtilityShell from '@/components/tools/utility/UtilityShell.vue'
import { inferSchemaGraph, toMermaidClassDiagram } from '@/utils/schema-diagrams.ts'
import { parseStructuredData, type StructuredInputFormat } from '@/utils/structured-data.ts'

const { t } = useTranslation()
const source = useStorage(
  'pastefy-utility-class-source',
  `{
  "project": {
    "id": "p_1",
    "name": "Pastefy",
    "owner": {
      "id": "u_1",
      "displayName": "Ada"
    },
    "members": [
      { "id": "u_2", "displayName": "Linus", "admin": true }
    ]
  }
}`,
)
const inputFormat = useStorage<StructuredInputFormat>('pastefy-utility-class-format', 'auto')
const rootEntity = useStorage('pastefy-utility-class-root', 'Root')
const includeTypes = useStorage('pastefy-utility-class-types', true)
