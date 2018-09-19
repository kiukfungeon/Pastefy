import {
  isRecord,
  normalizePrimitive,
  singularize,
  toPascalCase,
} from '@/utils/structured-data.ts'
import {
  toMermaidBareLabel,
  toMermaidQuotedLabel,
  toMermaidSafeIdentifier,
} from '@/utils/mermaid.ts'

export type SchemaField = {
  name: string
  type: string
}

export type SchemaRelation = {
  from: string
  to: string
  label: string
  kind: 'one' | 'many'
}

export type SchemaEntity = {
  name: string
  fields: SchemaField[]
}

export type SchemaGraph = {
  entities: SchemaEntity[]
  relations: SchemaRelation[]
}

const mergeType = (existing: string | undefined, next: string) => {
  if (!existing || existing === next) return next
