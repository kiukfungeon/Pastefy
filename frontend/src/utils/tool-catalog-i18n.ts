import type { TFunction } from 'i18next'

type ToolCatalogKind = 'preview' | 'conversion' | 'utility'

type TranslatableTool = {
  slug: string
  title: string
  shortTitle: string
  description: string
  category: string
}

