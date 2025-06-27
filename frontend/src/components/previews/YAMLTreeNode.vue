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
