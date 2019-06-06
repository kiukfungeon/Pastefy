<script setup lang="ts">
import Button from 'primevue/button'
import { computed, defineAsyncComponent, provide, ref } from 'vue'
import { useRoute } from 'vue-router'

import ToolFocusExitButton from '@/components/tools/ToolFocusExitButton.vue'
import ToolPinButton from '@/components/tools/ToolPinButton.vue'
import ToolRelatedLinks from '@/components/tools/ToolRelatedLinks.vue'
import { useSEO } from '@/composables/seo.ts'
import { findUtilityTool, utilityTools } from '@/utils/utility-tools.ts'
import { useTranslation } from 'i18next-vue'
import { localizeTool } from '@/utils/tool-catalog-i18n.ts'

const route = useRoute()
const focusMode = ref(false)
const { t } = useTranslation()
provide('tool-focus-mode', focusMode)

const tool = computed(() => {
  const definition = findUtilityTool(String(route.params.tool || ''))
  return definition ? localizeTool('utility', definition, t) : undefined
})

useSEO({
  title: computed(() =>
    tool.value ? `${tool.value.title} • Pastefy Tools` : 'Pastefy Tools • Utilities',
  ),
  description: computed(() =>
    tool.value
      ? `${tool.value.description} Use this Pastefy utility locally in the browser and create a paste from the result when needed.`
      : 'Use Pastefy utility tools for inspection, generation, and automation workflows.',
  ),
})

const toolComponents = {
  'data-to-mermaid': defineAsyncComponent(
    () => import('@/components/tools/utility/DataToMermaidTool.vue'),
  ),
  'mermaid-theme-builder': defineAsyncComponent(
    () => import('@/components/tools/utility/MermaidThemeBuilderTool.vue'),
  ),
  'data-to-mermaid-er': defineAsyncComponent(
    () => import('@/components/tools/utility/DataToMermaidERTool.vue'),
  ),
  'sql-to-mermaid-er': defineAsyncComponent(
    () => import('@/components/tools/utility/SQLToMermaidERTool.vue'),
  ),
  'infra-to-mermaid': defineAsyncComponent(
    () => import('@/components/tools/utility/InfraToMermaidTool.vue'),
  ),
  'markdown-to-mermaid': defineAsyncComponent(
    () => import('@/components/tools/utility/MarkdownToMermaidTool.vue'),
  ),
  'data-to-class-diagram': defineAsyncComponent(
    () => import('@/components/tools/utility/DataToClassDiagramTool.vue'),
  ),
  'mermaid-image-export': defineAsyncComponent(
    () => import('@/components/tools/utility/MermaidImageExportTool.vue'),
  ),
  'diff-lab': defineAsyncComponent(() => import('@/components/tools/utility/DiffLabTool.vue')),
  'nginx-log-inspector': defineAsyncComponent(
