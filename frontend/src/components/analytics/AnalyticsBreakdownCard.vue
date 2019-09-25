<script setup lang="ts">
import { computed, reactive, watchEffect } from 'vue'
import type { AnalyticsBreakdownPoint } from '@/types/analytics.ts'
import ProgressSpinner from 'primevue/progressspinner'

const countryFlagModules = import.meta.glob('../../../node_modules/ia-flag-icons/states/*.svg', {
  import: 'default',
  query: '?url',
}) as Record<string, () => Promise<string>>

const countryFlagLoaders = Object.entries(countryFlagModules).reduce<
  Record<string, () => Promise<string>>
>((loaders, [path, loader]) => {
  const country = path.match(/\/([A-Z]{2})\.svg$/)?.[1]
  if (country) loaders[country] = loader
  return loaders
}, {})

const props = defineProps<{
  title: string
  icon: string
  accent: 'amber' | 'rose' | 'blue' | 'green'
  dimensions: { label: string; value: string }[]
  active: string
  rows: AnalyticsBreakdownPoint[]
  loading?: boolean
}>()

const countryFlags = reactive<Record<string, string>>({})
const loadingCountryFlags = new Set<string>()

const loadCountryFlag = (value: string) => {
  const code = value.toUpperCase()
  if (!/^[A-Z]{2}$/.test(code) || countryFlags[code] || loadingCountryFlags.has(code)) return

  const loader = countryFlagLoaders[code]
  if (!loader) return

  loadingCountryFlags.add(code)
  loader()
    .then((url) => {
      countryFlags[code] = url
    })
    .catch(() => {})
    .finally(() => loadingCountryFlags.delete(code))
}

watchEffect(() => {
  if (props.active !== 'country') return
  props.rows.forEach(({ value }) => loadCountryFlag(value))
})

defineEmits<{
  'update:active': [value: string]
}>()

const maxVisits = computed(() => Math.max(...props.rows.map((row) => row.visits), 1))
const rowColor = computed(
  () =>
    ({
      amber: 'rgba(251, 191, 36, 0.2)',
      rose: 'rgba(251, 113, 133, 0.18)',
      blue: 'rgba(96, 165, 250, 0.2)',
      green: 'rgba(74, 222, 128, 0.18)',
    })[props.accent],
)

const displayValue = (value: string) => {
  if (!value) return '(direct / unknown)'
  if (
    props.active === 'paste_key' ||
    props.active === 'referer_host'
  )
    return value
  if (props.active === 'country' || props.active === 'region') return value.toUpperCase()
  return value
    .replace(/_/g, ' ')
    .toLowerCase()
    .replace(/(^|\s)\S/g, (character: string) => character.toUpperCase())
}

const countryFlag = (value: string) =>
  props.active === 'country' ? countryFlags[value.toUpperCase()] : undefined
</script>

<template>
  <section
    class="min-h-[19rem] overflow-hidden rounded-2xl border border-neutral-200 bg-white dark:border-neutral-800 dark:bg-neutral-900"
  >
    <header
      class="flex min-h-[3.8rem] items-end justify-between border-b border-neutral-200 px-5 dark:border-neutral-800"
    >
      <nav class="flex h-full gap-6 overflow-auto">
        <button
          v-for="dimension of dimensions"
          :key="dimension.value"
          class="relative pb-4 text-sm whitespace-nowrap text-neutral-500 transition-colors hover:text-neutral-950 dark:hover:text-white"
          :class="
            active === dimension.value
              ? 'font-semibold text-neutral-950 after:absolute after:right-0 after:bottom-0 after:left-0 after:h-0.5 after:bg-neutral-950 dark:text-white dark:after:bg-white'
