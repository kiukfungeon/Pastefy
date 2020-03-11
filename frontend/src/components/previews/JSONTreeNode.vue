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
