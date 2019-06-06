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
