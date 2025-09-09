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
