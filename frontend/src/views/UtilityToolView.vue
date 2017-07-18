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
