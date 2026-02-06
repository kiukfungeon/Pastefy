import { parseYamlDocument } from '@/utils/yaml.ts'

export type StructuredInputFormat = 'auto' | 'json' | 'yaml' | 'csv'
export type FlatRow = Record<string, string | number | boolean | null>

export const detectStructuredFormat = (value: string): Exclude<StructuredInputFormat, 'auto'> => {
  const trimmed = value.trim()
  if (!trimmed) return 'json'
  if (trimmed.startsWith('{') || trimmed.startsWith('[')) return 'json'
  const firstLine = trimmed.split(/\r?\n/, 1)[0] || ''
  if (firstLine.includes(',') && !firstLine.includes(':')) return 'csv'
  return 'yaml'
}

export const parseCsvRows = (raw: string) => {
  const rows: string[][] = []
  let current = ''
  let row: string[] = []
  let inQuotes = false

  const pushCell = () => {
    row.push(current)
    current = ''
  }

  const pushRow = () => {
    if (row.length > 0 || current !== '') {
      pushCell()
      rows.push(row)
      row = []
    }
  }

  for (let i = 0; i < raw.length; i += 1) {
    const char = raw[i]
    const next = raw[i + 1]

    if (char === '"') {
      if (inQuotes && next === '"') {
        current += '"'
        i += 1
      } else {
        inQuotes = !inQuotes
      }
      continue
    }

    if (char === ',' && !inQuotes) {
      pushCell()
      continue
    }

    if ((char === '\n' || char === '\r') && !inQuotes) {
      if (char === '\r' && next === '\n') i += 1
      pushRow()
      continue
    }

    current += char
  }

  if (current !== '' || row.length > 0) pushRow()
