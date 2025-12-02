export type SpecialPreviewType =
  | 'hcl'
  | 'log'
  | 'http'
  | 'github-actions'
  | 'ansible'
  | 'monitoring-rules'

export function resolveSpecialPreviewType(
  fileName?: string,
  contents?: string,
  type?: string,
): SpecialPreviewType | undefined {
  const name = (fileName || '').toLowerCase()
  const source = contents || ''
  const trimmed = source.trim()

  if (
    type === 'hcl' ||
    name.endsWith('.tf') ||
    name.endsWith('.tfvars') ||
    name.endsWith('.hcl')
  ) {
    return 'hcl'
  }

  if (
    type === 'log' ||
    name.endsWith('.log')
  ) {
    return 'log'
  }

  if (
    type === 'http' ||
    name.endsWith('.http') ||
    name.endsWith('.rest') ||
    trimmed.startsWith('curl ') ||
    /^(GET|POST|PUT|PATCH|DELETE|HEAD|OPTIONS)\s+https?:\/\//m.test(source)
  ) {
    return 'http'
  }

  if (
    (type === 'yaml' || type === 'json') &&
    (name.includes('workflow') || name.includes('action')) &&
    /\bon\s*:/i.test(source) &&
    /\bjobs\s*:/i.test(source)
