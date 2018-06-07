<script setup lang="ts">
import { computed, defineAsyncComponent } from 'vue'
import Tag from 'primevue/tag'
const Highlighted = defineAsyncComponent(() => import('@/components/Highlighted.vue'))

const props = defineProps<{
  hcl: string
  fileName?: string
}>()

const blocks = computed(() =>
  Array.from(
    props.hcl.matchAll(
      /^\s*(resource|module|variable|output|provider|data|locals|terraform)\s+"?([^"\s{]+)?"?\s*"?(.*?)"?\s*\{/gm,
    ),
  ).map((match, index) => ({
    kind: match[1],
    name: [match[2], match[3]].filter(Boolean).join(' / ') || `block-${index + 1}`,
  })),
)

const stats = computed(() => {
  const count = (kind: string) => blocks.value.filter((block) => block.kind === kind).length
  return {
    resources: count('resource'),
    modules: count('module'),
    variables: count('variable'),
    providers: count('provider'),
  }
})
</script>

<template>
  <div class="overflow-hidden text-sm">
    <div
      class="flex flex-col gap-4 border-b border-neutral-200 bg-neutral-50 px-4 py-4 dark:border-neutral-700 dark:bg-neutral-900/70"
    >
      <div class="flex flex-wrap items-center justify-between gap-3">
        <div>
          <div class="text-xs font-semibold tracking-[0.2em] uppercase opacity-60">
            {{ $t('previews.hclViewer.terraformHcl') }}
          </div>
          <div class="mt-1 text-xs opacity-60">{{ $t('previews.hclViewer.description') }}</div>
        </div>
        <div class="flex flex-wrap gap-2">
          <Tag :value="`${stats.resources} resources`" severity="contrast" />
