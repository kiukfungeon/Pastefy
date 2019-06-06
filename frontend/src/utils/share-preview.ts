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
