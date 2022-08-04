import type { TFunction } from 'i18next'

type ToolCatalogKind = 'preview' | 'conversion' | 'utility'

type TranslatableTool = {
  slug: string
  title: string
  shortTitle: string
  description: string
  category: string
}

type TranslatableCategory = {
  slug: string
  title: string
  eyebrow: string
  description: string
  seoTitle: string
  seoDescription: string
}

const toKey = (value: string) =>
  value
    .replace(/[^a-zA-Z0-9]+(.)/g, (_, character: string) => character.toUpperCase())
    .replace(/^[A-Z]/, (character) => character.toLowerCase())
