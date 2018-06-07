<script setup lang="ts">
import { useTranslation } from 'i18next-vue'
import Button from 'primevue/button'
import InputText from 'primevue/inputtext'
import { computed, ref } from 'vue'

import ToolCard from '@/components/tools/ToolCard.vue'
import { useSEO } from '@/composables/seo.ts'
import { usePinnedTools } from '@/composables/pinned-tools.ts'
import {
  conversionCatalogEntries,
  getCategoryCount,
  previewCatalogEntries,
  toolCategoryDefinitions,
  utilityCatalogEntries,
} from '@/utils/tool-categories.ts'
import { previewTools } from '@/utils/preview-tools.ts'
import { localizeCategory, localizeTool } from '@/utils/tool-catalog-i18n.ts'

const { t } = useTranslation()

useSEO({
  title: 'Pastefy Tools • Preview, Convert, and Share',
  get description() { return t('views.toolIndexView.descriptions.usePastefyToolsToPreviewStructuredFilesConvertFormatsLikeJsonAndYamlAndCreateAPasteOnlyWhenYouAreReadyToShare') },
})

const search = ref('')
const pinnedToolsStore = usePinnedTools()
const localizedPreviewTools = computed(() =>
  previewTools.map((tool) => localizeTool('preview', tool, t)),
)
const localizedPreviewCatalogEntries = computed(() =>
  previewCatalogEntries.map((tool) => localizeTool('preview', tool, t)),
)
const localizedConversionCatalogEntries = computed(() =>
  conversionCatalogEntries.map((tool) => localizeTool('conversion', tool, t)),
)
const localizedUtilityCatalogEntries = computed(() =>
  utilityCatalogEntries.map((tool) => localizeTool('utility', tool, t)),
)

const normalizedSearch = computed(() => search.value.trim().toLowerCase())

const matchesSearch = (
  entry: { title?: string; shortTitle?: string; description: string; keywords: string[] },
) => {
  if (!normalizedSearch.value) return true

  return [
    entry.title || '',
    entry.shortTitle || '',
    entry.description,
    ...entry.keywords,
  ]
    .join(' ')
    .toLowerCase()
    .includes(normalizedSearch.value)
}

const groupedTools = computed(() => {
  return Object.entries(
    localizedPreviewTools.value.reduce<Record<string, typeof previewTools>>((groups, tool) => {
      if (!matchesSearch(tool)) return groups
      groups[tool.category] ||= []
      groups[tool.category].push(tool)
      return groups
