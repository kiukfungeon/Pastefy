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
