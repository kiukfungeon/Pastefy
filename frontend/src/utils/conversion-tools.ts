import { parseYamlDocument } from '@/utils/yaml.ts'

export type ConversionToolDefinition = {
  slug: string
  title: string
  shortTitle: string
  description: string
  icon: string
  sourceFileName: string
  sourceType: string
  targetFileName: string
  targetType: string
  category: 'Convert'
  keywords: string[]
  example: string
}

type ConversionResult = {
  output: string
}

const needsYamlQuotes = (value: string) =>
  value === '' ||
  /[:#\-\n{}\[\],&*!?|<>=@`'"]/.test(value) ||
  /^(true|false|null|~)$/i.test(value) ||
  /^[-+]?\d+(\.\d+)?$/.test(value)

const yamlScalar = (value: unknown): string => {
  if (value === null || value === undefined) return 'null'
  if (typeof value === 'number' || typeof value === 'boolean') return String(value)
  if (typeof value === 'string') return needsYamlQuotes(value) ? JSON.stringify(value) : value
  return JSON.stringify(value)
}

const toYaml = (value: unknown, indent = 0): string => {
  const prefix = '  '.repeat(indent)

  if (Array.isArray(value)) {
    if (value.length === 0) return `${prefix}[]`
    return value
      .map((entry) => {
        if (entry && typeof entry === 'object') {
          const nested = toYaml(entry, indent + 1)
          return `${prefix}-\n${nested}`
        }
        return `${prefix}- ${yamlScalar(entry)}`
      })
      .join('\n')
  }

  if (value && typeof value === 'object') {
    const entries = Object.entries(value)
    if (entries.length === 0) return `${prefix}{}`
    return entries
      .map(([key, entry]) => {
        if (entry && typeof entry === 'object') {
          return `${prefix}${key}:\n${toYaml(entry, indent + 1)}`
        }
        return `${prefix}${key}: ${yamlScalar(entry)}`
      })
      .join('\n')
  }

  return `${prefix}${yamlScalar(value)}`
}

const flattenObject = (
  value: unknown,
  prefix = '',
  separator = '_',
  output: Record<string, string> = {},
) => {
  if (value === null || value === undefined) {
    output[prefix] = ''
    return output
  }

  if (typeof value !== 'object') {
    output[prefix] = String(value)
    return output
  }

  if (Array.isArray(value)) {
    output[prefix] = JSON.stringify(value)
    return output
  }

  Object.entries(value).forEach(([key, entry]) => {
    const nextKey = prefix ? `${prefix}${separator}${key}` : key
    if (entry && typeof entry === 'object' && !Array.isArray(entry)) {
      flattenObject(entry, nextKey, separator, output)
    } else {
      output[nextKey] = Array.isArray(entry) ? JSON.stringify(entry) : String(entry ?? '')
    }
  })

  return output
}

const parseEnv = (source: string) => {
  const output: Record<string, string> = {}
  source.split('\n').forEach((line) => {
    const trimmed = line.trim()
    if (!trimmed || trimmed.startsWith('#') || !trimmed.includes('=')) return
    const separator = trimmed.indexOf('=')
    const key = trimmed.slice(0, separator).trim()
    let value = trimmed.slice(separator + 1).trim()
    if (
      (value.startsWith('"') && value.endsWith('"')) ||
      (value.startsWith("'") && value.endsWith("'"))
    ) {
      value = value.slice(1, -1)
    }
    output[key] = value
  })
  return output
}

const toEnv = (value: unknown) => {
  const flattened = flattenObject(value, '', '_')
  return Object.entries(flattened)
    .map(([key, entry]) => `${key.toUpperCase()}=${/\s/.test(entry) ? JSON.stringify(entry) : entry}`)
    .join('\n')
}

const parseCsv = (source: string) => {
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

  for (let i = 0; i < source.length; i += 1) {
    const char = source[i]
    const next = source[i + 1]

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
  return rows
}

const escapeCsvCell = (value: unknown) => {
  const stringValue = value === null || value === undefined ? '' : String(value)
  if (/[",\n]/.test(stringValue)) {
    return `"${stringValue.replace(/"/g, '""')}"`
  }
  return stringValue
}

const toCsv = (value: unknown) => {
  if (!Array.isArray(value)) {
    throw new Error('JSON to CSV expects an array of objects or values at the top level.')
  }

  if (value.length === 0) return ''

  if (value.every((entry) => typeof entry !== 'object' || entry === null || Array.isArray(entry))) {
    return value.map((entry) => escapeCsvCell(entry)).join('\n')
  }

  const objectRows = value as Record<string, unknown>[]
  const headers = Array.from(
    new Set(objectRows.flatMap((entry) => Object.keys(entry || {}))),
  )

  const lines = [
    headers.map(escapeCsvCell).join(','),
    ...objectRows.map((entry) => headers.map((header) => escapeCsvCell(entry?.[header])).join(',')),
  ]

  return lines.join('\n')
}

const toMarkdownTable = (headers: string[], rows: string[][]) => {
  if (headers.length === 0) return ''
  const headerLine = `| ${headers.join(' | ')} |`
  const separatorLine = `| ${headers.map(() => '---').join(' | ')} |`
  const bodyLines = rows.map((row) => `| ${headers.map((_, index) => row[index] ?? '').join(' | ')} |`)
  return [headerLine, separatorLine, ...bodyLines].join('\n')
}

const shellQuote = (value: string) => `'${value.replace(/'/g, `'\"'\"'`)}'`

type ParsedHttpRequest = {
  method: string
  url: string
  headers: Record<string, string>
  body: string
}

const parseHttpRequest = (source: string): ParsedHttpRequest => {
  const normalized = source.trim().replace(/\r\n/g, '\n')
  const [head, ...bodyParts] = normalized.split('\n\n')
  const lines = head.split('\n').filter(Boolean)
  const requestLine = lines.shift()

  if (!requestLine) {
    throw new Error('HTTP input must start with a request line like GET https://example.com.')
  }

  const [method, url] = requestLine.trim().split(/\s+/, 2)
  if (!method || !url) {
    throw new Error('HTTP request line must include method and URL.')
  }

  const headers: Record<string, string> = {}
  lines.forEach((line) => {
    const separator = line.indexOf(':')
    if (separator === -1) return
    headers[line.slice(0, separator).trim()] = line.slice(separator + 1).trim()
  })

  return {
    method: method.toUpperCase(),
    url,
    headers,
    body: bodyParts.join('\n\n').trim(),
  }
}

const shellSplit = (command: string) => {
  const tokens: string[] = []
  let current = ''
  let quote: "'" | '"' | null = null
  let escaping = false

  for (const char of command.trim()) {
    if (escaping) {
      current += char
      escaping = false
      continue
    }

    if (char === '\\' && quote !== "'") {
      escaping = true
      continue
    }

    if ((char === '"' || char === "'") && !quote) {
      quote = char
      continue
    }

    if (quote && char === quote) {
      quote = null
      continue
    }

    if (!quote && /\s/.test(char)) {
      if (current) {
        tokens.push(current)
        current = ''
      }
      continue
    }

    current += char
  }

  if (current) tokens.push(current)
  return tokens
}

const parseCurl = (source: string): ParsedHttpRequest => {
  const tokens = shellSplit(source)
  if (tokens[0] !== 'curl') {
    throw new Error('cURL input must start with the curl command.')
  }

  let method = 'GET'
  let url = ''
  const headers: Record<string, string> = {}
  const bodyParts: string[] = []

  for (let i = 1; i < tokens.length; i += 1) {
    const token = tokens[i]
    const next = tokens[i + 1]

    if (['-X', '--request'].includes(token) && next) {
      method = next.toUpperCase()
      i += 1
      continue
    }

    if (['-H', '--header'].includes(token) && next) {
      const separator = next.indexOf(':')
      if (separator > -1) {
        headers[next.slice(0, separator).trim()] = next.slice(separator + 1).trim()
      }
      i += 1
      continue
    }

    if (['-d', '--data', '--data-raw', '--data-binary'].includes(token) && next) {
      bodyParts.push(next)
      if (method === 'GET') method = 'POST'
      i += 1
      continue
    }

    if (!token.startsWith('-') && /^https?:\/\//.test(token)) {
      url = token
    }
  }

  if (!url) {
    throw new Error('Could not find a URL in the cURL command.')
