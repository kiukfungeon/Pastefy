import type { PublicUser } from '@/types/user.ts'

export type PasteType = 'PASTE' | 'MULTI_PASTE'
export type PasteVisibility = 'PUBLIC' | 'PRIVATE' | 'UNLISTED'

export type PasteAiInfo = {
  dangerous?: boolean
  suggested_filename: string
