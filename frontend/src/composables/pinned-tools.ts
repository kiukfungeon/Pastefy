import { computed } from 'vue'
import { useStorage } from '@vueuse/core'

import { allToolCatalogEntries, type ToolCatalogEntry } from '@/utils/tool-categories.ts'

export type PinnedToolKind = ToolCatalogEntry['kind']

export const getPinnedToolKey = (kind: PinnedToolKind, slug: string) => `${kind}:${slug}`

export function usePinnedTools() {
  const pinnedToolKeys = useStorage<string[]>('pastefy-pinned-tools', [])

  const isPinned = (kind: PinnedToolKind, slug: string) =>
    pinnedToolKeys.value.includes(getPinnedToolKey(kind, slug))

  const togglePinned = (kind: PinnedToolKind, slug: string) => {
    const key = getPinnedToolKey(kind, slug)
    if (pinnedToolKeys.value.includes(key)) {
      pinnedToolKeys.value = pinnedToolKeys.value.filter((entry) => entry !== key)
      return
