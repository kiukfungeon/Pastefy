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
