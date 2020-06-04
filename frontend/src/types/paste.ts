import type { PublicUser } from '@/types/user.ts'

export type PasteType = 'PASTE' | 'MULTI_PASTE'
export type PasteVisibility = 'PUBLIC' | 'PRIVATE' | 'UNLISTED'

export type PasteAiInfo = {
  dangerous?: boolean
  suggested_filename: string
  warnings: {
    description: string
    severity: number
  }[]
  tags: string[]
  description: string
}
export type Paste = {
  id?: string
  content: string
  title: string
  encrypted: boolean
  visibility: PasteVisibility
  raw_url?: string
  type: PasteType
  created_at?: string
