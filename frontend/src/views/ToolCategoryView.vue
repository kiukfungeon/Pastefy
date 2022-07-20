<script setup lang="ts">
import Button from 'primevue/button'
import { computed } from 'vue'
import { useRoute } from 'vue-router'

import ToolCard from '@/components/tools/ToolCard.vue'
import { useSEO } from '@/composables/seo.ts'
import {
  findToolCategory,
  getCategoryCount,
  getCategoryEntries,
  toolCategoryDefinitions,
  type ToolCategorySlug,
} from '@/utils/tool-categories.ts'
import { useTranslation } from 'i18next-vue'
import { localizeCategory, localizeTool } from '@/utils/tool-catalog-i18n.ts'

const { t } = useTranslation()
const route = useRoute()

const categorySlug = computed(() => route.params.category as ToolCategorySlug | undefined)
const category = computed(() => {
  const definition = findToolCategory(categorySlug.value)
  return definition ? localizeCategory(definition, t) : undefined
})
const tools = computed(() =>
  category.value
    ? getCategoryEntries(category.value.slug).map((tool) => localizeTool(tool.kind, tool, t))
    : [],
)
const groupedTools = computed(() => ({
  preview: tools.value.filter((tool) => tool.kind === 'preview'),
  conversion: tools.value.filter((tool) => tool.kind === 'conversion'),
  utility: tools.value.filter((tool) => tool.kind === 'utility'),
}))
const relatedCategories = computed(() =>
  toolCategoryDefinitions
    .filter((entry) => entry.slug !== category.value?.slug && getCategoryCount(entry.slug) > 0)
    .filter((entry) => entry.scope === category.value?.scope || category.value?.scope === 'mixed')
