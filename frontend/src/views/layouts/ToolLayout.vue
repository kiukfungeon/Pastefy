<script setup lang="ts">
import Button from 'primevue/button'
import InputText from 'primevue/inputtext'
import Popover from 'primevue/popover'
import LoginModal from '@/components/modals/LoginModal.vue'
import UserMenu from '@/components/popovers/UserMenu.vue'
import { computed, defineAsyncComponent, ref, useTemplateRef } from 'vue'
import { useCurrentUserStore } from '@/stores/current-user.ts'
import { useAppInfoStore } from '@/stores/app-info.ts'
import type { PopoverMethods } from 'primevue'
import { previewTools } from '@/utils/preview-tools.ts'
import { conversionTools } from '@/utils/conversion-tools.ts'
import { utilityTools } from '@/utils/utility-tools.ts'
import {
  allToolCatalogEntries,
  getCategoryCount,
  toolCategoryDefinitions,
} from '@/utils/tool-categories.ts'
import LanguageSwitcher from '@/components/LanguageSwitcher.vue'
import { useTranslation } from 'i18next-vue'
import { localizeCategory, localizeTool } from '@/utils/tool-catalog-i18n.ts'

const Logo = defineAsyncComponent(() => import('@/components/Logo.vue'))

const loginModalVisible = ref(false)
const toolSearch = ref('')
const currentUserStore = useCurrentUserStore()
const appInfo = useAppInfoStore()
const userMenu = useTemplateRef<PopoverMethods>('userMenu')
const { t } = useTranslation()
const localizedToolCatalogEntries = computed(() =>
  allToolCatalogEntries.map((entry) => localizeTool(entry.kind, entry, t)),
)

const toolSearchResults = computed(() => {
  const query = toolSearch.value.trim().toLowerCase()
  if (!query) return []

  return localizedToolCatalogEntries.value
    .filter((entry) =>
      [entry.title, entry.shortTitle, entry.description, entry.category, ...entry.keywords]
        .join(' ')
        .toLowerCase()
        .includes(query),
    )
    .slice(0, 8)
})

const topLinks = computed(() => [
  {
    label: t('tools.categories'),
    icon: 'ti ti-category',
    to: { name: 'tool-home' },
    panelClass: 'w-[56rem]',
    cards: toolCategoryDefinitions
      .filter((category) => getCategoryCount(category.slug) > 0)
      .map((category) => localizeCategory(category, t))
      .map((category) => ({
        title: category.title,
        description: `${category.description} ${getCategoryCount(category.slug)} tools.`,
        icon: category.icon,
        to: { name: 'tool-category', params: { category: category.slug } },
      })),
  },
  {
    label: t('tools.previewInspect'),
    icon: 'ti ti-sparkles',
    to: { name: 'tool-home', hash: '#preview-tools' },
    cards: previewTools.slice(0, 6).map((tool) => localizeTool('preview', tool, t)).map((tool) => ({
      title: tool.shortTitle,
      description: tool.description,
      icon: tool.icon,
      to: { name: 'tool-preview', params: { tool: tool.slug } },
    })),
  },
  {
    label: t('tools.convert'),
    icon: 'ti ti-arrows-exchange',
    to: { name: 'tool-home', hash: '#conversions' },
    cards: conversionTools.slice(0, 6).map((tool) => localizeTool('conversion', tool, t)).map((tool) => ({
      title: tool.shortTitle,
      description: tool.description,
      icon: tool.icon,
      to: { name: 'tool-conversion', params: { tool: tool.slug } },
    })),
  },
  {
    label: t('tools.utilities'),
    icon: 'ti ti-tool',
    to: { name: 'tool-home', hash: '#utilities' },
    cards: utilityTools.slice(0, 6).map((tool) => localizeTool('utility', tool, t)).map((tool) => ({
      title: tool.shortTitle,
      description: tool.description,
      icon: tool.icon,
      to: { name: 'tool-utility', params: { tool: tool.slug } },
    })),
  },
  {
    label: t('nav.explore'),
    icon: 'ti ti-world',
    to: { name: 'explore' },
    hidden: !appInfo.appInfo?.public_pastes_enabled,
  },
])
</script>

<template>
  <div class="min-h-screen bg-white dark:bg-[#121212]">
    <header class="sticky top-0 z-50 bg-white/80 backdrop-blur-xl dark:bg-[#121212]/80">
      <div class="flex w-full items-center justify-between gap-3 px-4 py-3 md:px-6">
        <div class="flex items-center gap-2 md:gap-3">
          <router-link
            :to="{ name: 'home' }"
            class="flex items-center gap-2 py-2 transition-opacity hover:opacity-80"
            :aria-label="$t('tools.title')"
          >
            <img
              v-if="appInfo.appInfo?.custom_logo"
              :src="appInfo.appInfo.custom_logo"
              alt="logo"
              class="h-[1.7rem]"
            />
            <Logo v-else />
          </router-link>

          <nav class="hidden items-center gap-1 md:flex">
            <template v-for="link of topLinks.filter((entry) => !entry.hidden)" :key="link.label">
              <div v-if="'cards' in link" class="group relative">
                <Button
                  as="router-link"
