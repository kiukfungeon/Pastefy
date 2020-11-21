<script setup lang="ts">
import Checkbox from 'primevue/checkbox'
import Textarea from 'primevue/textarea'
import { computed } from 'vue'
import { useStorage } from '@vueuse/core'

import MermaidToolResult from '@/components/tools/utility/MermaidToolResult.vue'
import UtilityShell from '@/components/tools/utility/UtilityShell.vue'

type TableColumn = {
  name: string
  type: string
  primaryKey: boolean
}

type Relation = {
  from: string
  to: string
  fromField: string
  toField: string
}

const sanitizeEntityName = (value: string) => {
  const sanitized = value
    .replace(/["`]/g, '')
    .replace(/[^a-zA-Z0-9_]/g, '_')
    .replace(/_+/g, '_')
    .replace(/^_+|_+$/g, '')
  return sanitized || 'entity'
}

const sanitizeTypeForMermaid = (value: string) => {
  const sanitized = value
    .trim()
    .toLowerCase()
    .replace(/["`]/g, '')
    .replace(/[^a-zA-Z0-9]+/g, '_')
    .replace(/^_+|_+$/g, '')
  return sanitized || 'field'
}

const source = useStorage(
  'pastefy-utility-sql-er-source',
  `CREATE TABLE users (
  id UUID PRIMARY KEY,
  name VARCHAR(255) NOT NULL
);

CREATE TABLE posts (
  id UUID PRIMARY KEY,
  user_id UUID NOT NULL REFERENCES users(id),
  title TEXT NOT NULL
);`,
)
const includeTypes = useStorage('pastefy-utility-sql-er-types', true)

const diagram = computed(() => {
  try {
    const tableMatches = source.value.matchAll(
      /CREATE\s+TABLE\s+(?:IF\s+NOT\s+EXISTS\s+)?([^\s(]+)\s*\(([\s\S]*?)\);/gi,
    )
    const tables = new Map<string, TableColumn[]>()
    const relations: Relation[] = []

    for (const match of tableMatches) {
      const tableName = sanitizeEntityName(match[1])
      const block = match[2]
      const columns: TableColumn[] = []

      block
        .split(/,\n|,\r\n|,\s*(?=[a-zA-Z_"`])/)
        .map((line) => line.trim())
        .filter(Boolean)
        .forEach((line) => {
          const fkInline = line.match(/REFERENCES\s+([^\s(]+)\s*\(([^)]+)\)/i)
          if (fkInline) {
            const columnName = line.split(/\s+/)[0].replace(/["`]/g, '')
            relations.push({
              from: tableName,
              to: sanitizeEntityName(fkInline[1]),
              fromField: columnName,
