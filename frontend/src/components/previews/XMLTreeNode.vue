<script setup lang="ts">
import { computed } from 'vue'

defineOptions({
  name: 'XMLTreeNode',
})

export type XMLNodeEntry = {
  name: string
  path: string
  attributes: Array<{ name: string; value: string }>
  text?: string
  children: XMLNodeEntry[]
}

const props = defineProps<{
  node: XMLNodeEntry
  depth?: number
  search?: string
  isExpanded: (path: string) => boolean
  setExpanded: (path: string, expanded: boolean) => void
}>()

const depth = computed(() => props.depth || 0)
const needle = computed(() => props.search?.trim().toLowerCase() || '')
const hasChildren = computed(() => props.node.children.length > 0)

const selfText = computed(() =>
  [
    props.node.name,
    props.node.path,
    props.node.text || '',
    props.node.attributes.map((attr) => `${attr.name}=${attr.value}`).join(' '),
  ]
    .join(' ')
    .toLowerCase(),
)

const hasMatchingDescendant = computed(() => {
  if (!needle.value) return false

  const walk = (node: XMLNodeEntry): boolean => {
    const combined = [
      node.name,
      node.path,
      node.text || '',
      node.attributes.map((attr) => `${attr.name}=${attr.value}`).join(' '),
    ]
      .join(' ')
      .toLowerCase()

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

