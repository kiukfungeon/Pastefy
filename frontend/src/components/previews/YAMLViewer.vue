<script setup lang="ts">
import { useTranslation } from 'i18next-vue'
import { computed, defineAsyncComponent, ref, watch } from 'vue'
import { useClipboard } from '@vueuse/core'
import Button from 'primevue/button'
import InputText from 'primevue/inputtext'
import Select from 'primevue/select'
import Tag from 'primevue/tag'
import YAMLTreeNode, { type YAMLNodeEntry } from '@/components/previews/YAMLTreeNode.vue'
const { t } = useTranslation()
const Highlighted = defineAsyncComponent(() => import('@/components/Highlighted.vue'))

const props = defineProps<{
  yaml: string
}>()

const clipboard = useClipboard()
const search = ref('')
const viewMode = ref<'tree' | 'pretty' | 'raw'>('tree')
const expandedPaths = ref(new Set<string>(['root']))

const parsedTree = computed(() => {
  const root: YAMLNodeEntry = {
    get label() { return t('previews.yamlViewer.options.root') },
    path: 'root',
    lineNumber: 0,
    kind: 'mapping',
    children: [],
  }

  const stack: Array<{ indent: number; node: YAMLNodeEntry }> = [{ indent: -1, node: root }]
  const sequenceCounters = new Map<string, number>()

  props.yaml.split('\n').forEach((line, index) => {
    const trimmed = line.trim()
    if (!trimmed) return

    const indent = line.match(/^\s*/)?.[0].length || 0
    while (stack.length > 1 && indent <= stack[stack.length - 1].indent) {
      stack.pop()
    }

    const parent = stack[stack.length - 1].node
    const basePath = parent.path

    let node: YAMLNodeEntry

    if (trimmed.startsWith('#')) {
      node = {
        label: trimmed,
        path: `${basePath}.comment-${index}`,
        lineNumber: index + 1,
        kind: 'comment',
        children: [],
      }
      parent.children.push(node)
      return
    }

    if (trimmed.startsWith('- ')) {
      const counter = sequenceCounters.get(basePath) || 0
      sequenceCounters.set(basePath, counter + 1)
      const remainder = trimmed.slice(2).trim()
      const separatorIndex = remainder.indexOf(':')

      if (separatorIndex > -1) {
        const label = remainder.slice(0, separatorIndex).trim()
        const value = remainder.slice(separatorIndex + 1).trim() || undefined
        node = {
          label,
          value,
          path: `${basePath}[${counter}]`,
          lineNumber: index + 1,
          kind: 'sequence',
          children: [],
        }
      } else {
        node = {
          label: `[item ${counter + 1}]`,
          value: remainder || undefined,
          path: `${basePath}[${counter}]`,
          lineNumber: index + 1,
          kind: 'sequence',
          children: [],
        }
      }

      parent.children.push(node)
      stack.push({ indent, node })
      return
    }

    const separatorIndex = trimmed.indexOf(':')
    if (separatorIndex > -1) {
      const label = trimmed.slice(0, separatorIndex).trim()
      const value = trimmed.slice(separatorIndex + 1).trim() || undefined
      node = {
        label,
        value,
        path: `${basePath}.${label}-${index}`,
        lineNumber: index + 1,
        kind: 'mapping',
        children: [],
      }
      parent.children.push(node)
      stack.push({ indent, node })
      return
    }

    node = {
      label: trimmed,
      path: `${basePath}.scalar-${index}`,
      lineNumber: index + 1,
      kind: 'scalar',
      children: [],
    }
    parent.children.push(node)
  })

  return root
})

const stats = computed(() => {
  const allNodes: YAMLNodeEntry[] = []

  const walk = (node: YAMLNodeEntry) => {
    allNodes.push(node)
    node.children.forEach(walk)
  }
  parsedTree.value.children.forEach(walk)

  return {
    keys: allNodes.filter((node) => node.kind === 'mapping').length,
    listItems: allNodes.filter((node) => node.kind === 'sequence').length,
    comments: allNodes.filter((node) => node.kind === 'comment').length,
  }
})

