<script setup lang="ts">
import { computed } from 'vue'

defineOptions({
  name: 'YAMLTreeNode',
})

export type YAMLNodeEntry = {
  label: string
  value?: string
  path: string
  lineNumber: number
  kind: 'mapping' | 'sequence' | 'comment' | 'scalar'
  children: YAMLNodeEntry[]
}

const props = defineProps<{
  node: YAMLNodeEntry
  depth?: number
  search?: string
  isExpanded: (path: string) => boolean
  setExpanded: (path: string, expanded: boolean) => void
}>()

const depth = computed(() => props.depth || 0)
const needle = computed(() => props.search?.trim().toLowerCase() || '')
const hasChildren = computed(() => props.node.children.length > 0)

const selfText = computed(() =>
  [props.node.label, props.node.value || '', props.node.path].join(' ').toLowerCase(),
)

const hasMatchingDescendant = computed(() => {
  if (!needle.value) return false

  const walk = (node: YAMLNodeEntry): boolean => {
    const combined = [node.label, node.value || '', node.path].join(' ').toLowerCase()
    if (combined.includes(needle.value)) return true
    return node.children.some(walk)
  }

  return props.node.children.some(walk)
})

const isVisible = computed(() => {
  if (!needle.value) return true
  return selfText.value.includes(needle.value) || hasMatchingDescendant.value
})

const expanded = computed(() => {
  if (!hasChildren.value) return false
  if (needle.value && hasMatchingDescendant.value) return true
  return props.isExpanded(props.node.path)
})

const toggle = () => {
  if (!hasChildren.value) return
  props.setExpanded(props.node.path, !expanded.value)
}

const labelClass = computed(() => {
