<script setup lang="ts">
import { computed } from 'vue'

defineOptions({
  name: 'JSONTreeNode',
})

const props = defineProps<{
  label?: string
  value: unknown
  path: string
  depth?: number
  search?: string
  isExpanded: (path: string) => boolean
  setExpanded: (path: string, expanded: boolean) => void
}>()

const depth = computed(() => props.depth || 0)

const isObjectLike = computed(
  () => props.value !== null && typeof props.value === 'object' && !Array.isArray(props.value),
)
const isArrayLike = computed(() => Array.isArray(props.value))
const isCollapsible = computed(() => isObjectLike.value || isArrayLike.value)

const entries = computed(() => {
  if (isArrayLike.value) {
    return (props.value as unknown[]).map((entry, index) => ({
      key: `[${index}]`,
      value: entry,
      childPath: `${props.path}.${index}`,
    }))
  }
  if (isObjectLike.value) {
    return Object.entries(props.value as Record<string, unknown>).map(([key, value]) => ({
      key,
      value,
      childPath: `${props.path}.${key}`,
    }))
  }
  return []
})

const searchNeedle = computed(() => props.search?.trim().toLowerCase() || '')

const primitiveText = computed(() => {
  if (typeof props.value === 'string') return `"${props.value}"`
  if (props.value === null) return 'null'
  return String(props.value)
})

const summary = computed(() => {
  if (isArrayLike.value) return `Array(${entries.value.length})`
  if (isObjectLike.value) return `Object(${entries.value.length})`
  return primitiveText.value
})

const selfSearchText = computed(() => {
  const fragments = [props.label || '', props.path]
  if (!isCollapsible.value) fragments.push(primitiveText.value)
  return fragments.join(' ').toLowerCase()
})

const hasMatchingDescendant = computed(() => {
  if (!searchNeedle.value || !isCollapsible.value) return false

  return entries.value.some((entry) => {
    const label = entry.key.toLowerCase()
    const path = entry.childPath.toLowerCase()

    if (label.includes(searchNeedle.value) || path.includes(searchNeedle.value)) return true

    if (entry.value === null) return 'null'.includes(searchNeedle.value)
    if (typeof entry.value !== 'object') {
      return String(entry.value).toLowerCase().includes(searchNeedle.value)
    }

    try {
      return JSON.stringify(entry.value).toLowerCase().includes(searchNeedle.value)
    } catch {
      return false
    }
  })
})

const isVisible = computed(() => {
  if (!searchNeedle.value) return true
  return selfSearchText.value.includes(searchNeedle.value) || hasMatchingDescendant.value
})

const expanded = computed(() => {
  if (!isCollapsible.value) return false
  if (searchNeedle.value && hasMatchingDescendant.value) return true
  return props.isExpanded(props.path)
})

const toggle = () => {
  if (!isCollapsible.value) return
  props.setExpanded(props.path, !expanded.value)
}

const valueClass = computed(() => {
  if (props.value === null) return 'text-fuchsia-600 dark:text-fuchsia-300'
  if (typeof props.value === 'string') return 'text-emerald-700 dark:text-emerald-300'
  if (typeof props.value === 'number') return 'text-sky-700 dark:text-sky-300'
  if (typeof props.value === 'boolean') return 'text-orange-700 dark:text-orange-300'
  return 'text-neutral-600 dark:text-neutral-300'
})
</script>

<template>
  <div v-if="isVisible" class="font-[JetBrains_Mono_Variable] text-[0.92rem]">
    <div
      class="flex items-start gap-2 rounded-lg px-2 py-1 transition-colors hover:bg-neutral-100/80 dark:hover:bg-neutral-800/70"
