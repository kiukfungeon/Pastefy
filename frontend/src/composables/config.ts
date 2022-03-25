import { useStorage } from '@vueuse/core'
import type { SelectedTheme } from '@/utils/theme-logic.ts'
import type { PasteVisibility } from '@/types/paste.ts'
import type { LanguagePreference } from '@/i18n.ts'
const prefersReducedMotion = window.matchMedia('(prefers-reduced-motion: reduce)')

export function useConfig() {
