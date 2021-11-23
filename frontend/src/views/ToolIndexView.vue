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
    }, {}),
  ).filter(([, tools]) => tools.length > 0)
})

const filteredPreviewTools = computed(() =>
  localizedPreviewCatalogEntries.value.filter((tool) => matchesSearch(tool)),
)
const filteredConversionTools = computed(() =>
  localizedConversionCatalogEntries.value.filter((tool) => matchesSearch(tool)),
)
const filteredUtilityTools = computed(() =>
  localizedUtilityCatalogEntries.value.filter((tool) => matchesSearch(tool)),
)
const categoryLinks = computed(() =>
  toolCategoryDefinitions
    .filter((category) => getCategoryCount(category.slug) > 0)
    .map((category) => localizeCategory(category, t))
    .filter((category) => {
      if (!normalizedSearch.value) return true
      return `${category.title} ${category.description} ${category.eyebrow}`
        .toLowerCase()
        .includes(normalizedSearch.value)
    }),
)
const pinnedTools = computed(() =>
  pinnedToolsStore.pinnedTools.value.map((tool) => localizeTool(tool.kind, tool, t)),
)
</script>

<template>
  <section class="mx-auto flex max-w-[1200px] flex-col gap-8">
    <div
      class="overflow-hidden rounded-xl border border-neutral-200 bg-neutral-100 p-6 dark:border-neutral-700 dark:bg-neutral-800 md:p-8"
    >
      <div class="grid gap-6 md:grid-cols-[1.5fr_1fr] md:items-end">
        <div class="flex flex-col gap-4">
          <span class="text-sm font-semibold uppercase tracking-[0.25em] text-neutral-500 dark:text-neutral-400">
            {{ $t('views.toolIndexView.pastefyTools') }}
          </span>
          <div class="flex flex-col gap-3">
            <h1 class="max-w-[18ch] text-4xl leading-none font-bold md:text-5xl">
              Preview, convert, and shape code before you share it.
            </h1>
            <p class="max-w-[62ch] text-base text-neutral-600 dark:text-neutral-300">
              Work with Markdown, JSON, YAML, HTML, DevOps configs, API requests, and more in
              focused tools built on top of Pastefy's preview system. Stay in a lightweight
              split-view workflow and create a paste only when the result is ready.
            </p>
          </div>
        </div>

        <div class="grid gap-3 rounded-xl border border-neutral-200 bg-white p-4 dark:border-neutral-700 dark:bg-neutral-900">
          <div class="flex items-center justify-between">
            <span class="text-sm font-medium">{{ $t('views.toolIndexView.whatYouCanDoHere') }}</span>
            <i class="ti ti-sparkles text-xl opacity-60" />
          </div>
          <div class="grid gap-2 text-sm text-neutral-600 dark:text-neutral-300">
            <p>{{ $t('views.toolIndexView.previewStructuredFormatsWithDedicatedViewers') }}</p>
            <p>{{ $t('views.toolIndexView.convertDataBetweenCommonDeveloperFormats') }}</p>
            <p>{{ $t('views.toolIndexView.createWhenReady') }}</p>
          </div>
          <Button
            as="router-link"
            :to="{ name: 'tool-preview', params: { tool: previewTools[0]?.slug } }"
            :label="$t('views.toolIndexView.openPreviewTool')"
            icon="ti ti-arrow-right"
            severity="contrast"
