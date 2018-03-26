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
    () => import('@/components/tools/utility/NginxLogInspectorTool.vue'),
  ),
  'apache-log-inspector': defineAsyncComponent(
    () => import('@/components/tools/utility/ApacheLogInspectorTool.vue'),
  ),
  'caddy-config-generator': defineAsyncComponent(
    () => import('@/components/tools/utility/CaddyConfigGeneratorTool.vue'),
  ),
  'nginx-config-generator': defineAsyncComponent(
    () => import('@/components/tools/utility/NginxConfigGeneratorTool.vue'),
  ),
  'apache-config-generator': defineAsyncComponent(
    () => import('@/components/tools/utility/ApacheConfigGeneratorTool.vue'),
  ),
  'dockerfile-generator': defineAsyncComponent(
    () => import('@/components/tools/utility/DockerfileGeneratorTool.vue'),
  ),
  'jar-start-script-generator': defineAsyncComponent(
    () => import('@/components/tools/utility/JarStartScriptGeneratorTool.vue'),
  ),
  'systemd-service-generator': defineAsyncComponent(
    () => import('@/components/tools/utility/SystemdServiceGeneratorTool.vue'),
  ),
  'compose-stack-generator': defineAsyncComponent(
    () => import('@/components/tools/utility/ComposeStackGeneratorTool.vue'),
  ),
  'jwt-inspector': defineAsyncComponent(
    () => import('@/components/tools/utility/JwtInspectorTool.vue'),
  ),
  'hash-generator': defineAsyncComponent(
    () => import('@/components/tools/utility/HashGeneratorTool.vue'),
  ),
  'secret-generator': defineAsyncComponent(
    () => import('@/components/tools/utility/SecretGeneratorTool.vue'),
  ),
  'password-generator': defineAsyncComponent(
    () => import('@/components/tools/utility/PasswordGeneratorTool.vue'),
  ),
  'cron-explainer': defineAsyncComponent(
    () => import('@/components/tools/utility/CronExplainerTool.vue'),
  ),
  'base64-lab': defineAsyncComponent(() => import('@/components/tools/utility/Base64Tool.vue')),
  'url-encoder': defineAsyncComponent(
    () => import('@/components/tools/utility/UrlEncoderTool.vue'),
  ),
  'timestamp-converter': defineAsyncComponent(
    () => import('@/components/tools/utility/TimestampConverterTool.vue'),
  ),
  'slug-generator': defineAsyncComponent(
    () => import('@/components/tools/utility/SlugGeneratorTool.vue'),
  ),
  'case-converter': defineAsyncComponent(
    () => import('@/components/tools/utility/CaseConverterTool.vue'),
  ),
  'query-string-parser': defineAsyncComponent(
    () => import('@/components/tools/utility/QueryStringTool.vue'),
  ),
  'html-entity-tool': defineAsyncComponent(
    () => import('@/components/tools/utility/HtmlEntityTool.vue'),
  ),
  'json-string-tool': defineAsyncComponent(
    () => import('@/components/tools/utility/JsonStringTool.vue'),
  ),
  'regex-escape-tool': defineAsyncComponent(
    () => import('@/components/tools/utility/RegexEscapeTool.vue'),
  ),
  'color-converter': defineAsyncComponent(
    () => import('@/components/tools/utility/ColorConverterTool.vue'),
  ),
  'number-base-converter': defineAsyncComponent(
    () => import('@/components/tools/utility/NumberBaseTool.vue'),
  ),
  'text-sorter': defineAsyncComponent(
    () => import('@/components/tools/utility/TextSorterTool.vue'),
  ),
  'lorem-ipsum-generator': defineAsyncComponent(
    () => import('@/components/tools/utility/LoremIpsumTool.vue'),
  ),
  'uuid-inspector': defineAsyncComponent(
    () => import('@/components/tools/utility/UuidInspectorTool.vue'),
  ),
  'word-stats': defineAsyncComponent(
    () => import('@/components/tools/utility/WordStatsTool.vue'),
  ),
} as const

const currentComponent = computed(() =>
  tool.value ? toolComponents[tool.value.slug as keyof typeof toolComponents] : undefined,
)

const relatedTools = computed(() =>
  utilityTools
    .map((entry) => localizeTool('utility', entry, t))
    .filter((entry) => entry.slug !== tool.value?.slug)
    .slice(0, 3),
)

const relatedToolLinks = computed(() =>
  relatedTools.value.map((entry) => ({
    slug: entry.slug,
    icon: entry.icon,
    title: entry.shortTitle,
    meta: entry.category,
    to: {
      name: 'tool-utility',
      params: {
        tool: entry.slug,
      },
    },
  })),
)
</script>

<template>
  <section
    v-if="tool"
    class="mx-auto flex max-w-[1400px] flex-col gap-6 bg-white dark:bg-[#121212]"
    :class="focusMode ? 'fixed inset-0 z-[120] max-w-none h-[100dvh] overflow-hidden p-4 md:p-6' : ''"
  >
    <ToolFocusExitButton :visible="focusMode" @close="focusMode = false" />

    <div
