import { findFromFileName } from '@/utils/lang-replacements.ts'

export type SharePreviewType =
  | 'csv'
  | 'mermaid'
  | 'geojson'
  | 'svg'
  | 'diff'
  | 'cast'
  | 'html'
  | 'json'
  | 'xml'
  | 'yaml'
  | 'toml'
  | 'config'

export function resolveSharePreviewType(fileName?: string): SharePreviewType | undefined {
  if (!fileName) return undefined

  if (fileName.endsWith('.svg')) return 'svg'
  if (fileName.endsWith('.geojson')) return 'geojson'
  if (fileName.endsWith('.mmd') || fileName.endsWith('.mermaid')) return 'mermaid'
  if (fileName.endsWith('.cast')) return 'cast'
  if (fileName.endsWith('.html') || fileName.endsWith('.htm')) return 'html'
  if (fileName.endsWith('.xml') || fileName.endsWith('.xhtml') || fileName.endsWith('.iml'))
    return 'xml'
  if (fileName.endsWith('.yml') || fileName.endsWith('.yaml')) return 'yaml'
  if (fileName.endsWith('.toml')) return 'toml'
  if (fileName.endsWith('.ini') || fileName.endsWith('.properties') || fileName === '.env')
    return 'config'

  const lang = findFromFileName(fileName)

  if (lang === 'csv') return 'csv'
  if (lang === 'diff') return 'diff'
  if (lang === 'cast') return 'cast'
  if (lang === 'geojson') return 'geojson'
  if (lang === 'mermaid' || lang === 'mmd') return 'mermaid'
  if (lang === 'json') return 'json'
  if (lang === 'xml') return 'xml'
  if (lang === 'yaml') return 'yaml'
  if (lang === 'toml') return 'toml'
  if (lang === 'properties' || lang === 'ini') return 'config'

  return undefined
}

export function getSharePreviewMeta(type: SharePreviewType) {
  const map = {
    csv: {
      label: 'CSV Table',
      description: 'A clean tabular view for sharing datasets and quick exports.',
      icon: 'table',
    },
    mermaid: {
      label: 'Diagram View',
      description: 'A presentation-friendly view for Mermaid charts and diagrams.',
      icon: 'chart-arcs',
    },
    geojson: {
