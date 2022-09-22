<script setup lang="ts">
import { computed, defineAsyncComponent } from 'vue'
import SVGViewer from '@/components/previews/SVGViewer.vue'
import { resolveSpecialPreviewType } from '@/utils/special-preview.ts'

// const LaTeXViewer = defineAsyncComponent(() => import('@/components/previews/LaTeXViewer.vue'))
const MarkdownViewer = defineAsyncComponent(
  () => import('@/components/previews/MarkdownViewer.vue'),
)
const CSVViewer = defineAsyncComponent(() => import('@/components/previews/CSVViewer.vue'))
const MermaidViewer = defineAsyncComponent(() => import('@/components/previews/MermaidViewer.vue'))
const GeoJSONViewer = defineAsyncComponent(() => import('@/components/previews/GeoJSONViewer.vue'))
const DiffViewer = defineAsyncComponent(() => import('@/components/previews/DiffViewer.vue'))
const ICSViewer = defineAsyncComponent(() => import('@/components/previews/ICSViewer.vue'))
const RegexViewer = defineAsyncComponent(() => import('@/components/previews/RegexViewer.vue'))
const JSONViewer = defineAsyncComponent(() => import('@/components/previews/JSONViewer.vue'))
const HTMLViewer = defineAsyncComponent(() => import('@/components/previews/HTMLViewer.vue'))
const XMLViewer = defineAsyncComponent(() => import('@/components/previews/XMLViewer.vue'))
const ConfigViewer = defineAsyncComponent(() => import('@/components/previews/ConfigViewer.vue'))
const YAMLViewer = defineAsyncComponent(() => import('@/components/previews/YAMLViewer.vue'))
const HCLViewer = defineAsyncComponent(() => import('@/components/previews/HCLViewer.vue'))
const LogViewer = defineAsyncComponent(() => import('@/components/previews/LogViewer.vue'))
const HTTPViewer = defineAsyncComponent(() => import('@/components/previews/HTTPViewer.vue'))
const GitHubActionsViewer = defineAsyncComponent(
  () => import('@/components/previews/GitHubActionsViewer.vue'),
)
const AnsibleViewer = defineAsyncComponent(() => import('@/components/previews/AnsibleViewer.vue'))
const MonitoringRulesViewer = defineAsyncComponent(
  () => import('@/components/previews/MonitoringRulesViewer.vue'),
)
const AsciinemaViewer = defineAsyncComponent(
  () => import('@/components/previews/AsciinemaViewer.vue'),
)

const props = defineProps<{
  fileName?: string
  contents: string
  type: string
  inEditor?: boolean
}>()

const specialType = computed(() =>
  resolveSpecialPreviewType(props.fileName, props.contents, props.type),
)
</script>
<template>
  <CSVViewer v-if="type === 'csv'" :csv="contents" :inEditor />
  <MarkdownViewer v-else-if="type === 'markdown'" :markdown="contents" :inEditor />
  <HCLViewer v-else-if="specialType === 'hcl'" :hcl="contents" :file-name="fileName" />
  <LogViewer v-else-if="specialType === 'log'" :logs="contents" />
  <HTTPViewer v-else-if="specialType === 'http'" :request="contents" />
  <GitHubActionsViewer v-else-if="specialType === 'github-actions'" :workflow="contents" />
  <AnsibleViewer v-else-if="specialType === 'ansible'" :playbook="contents" />
  <MonitoringRulesViewer v-else-if="specialType === 'monitoring-rules'" :config="contents" />
  <MermaidViewer
    v-else-if="
