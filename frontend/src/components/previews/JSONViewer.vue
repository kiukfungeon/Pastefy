<script setup lang="ts">
import { useTranslation } from 'i18next-vue'
import { computed, defineAsyncComponent, ref, watch } from 'vue'
import { useClipboard } from '@vueuse/core'
import Button from 'primevue/button'
import InputText from 'primevue/inputtext'
import Select from 'primevue/select'
import Tag from 'primevue/tag'
import JSONTreeNode from '@/components/previews/JSONTreeNode.vue'
const { t } = useTranslation()
const Highlighted = defineAsyncComponent(() => import('@/components/Highlighted.vue'))

const props = defineProps<{
  json: string
}>()

const clipboard = useClipboard()
const search = ref('')
const viewMode = ref<'tree' | 'pretty' | 'raw'>('tree')
const expandedPaths = ref(new Set<string>(['root']))

const parsed = computed(() => {
  try {
    return JSON.parse(props.json)
  } catch {
    return undefined
  }
})

const prettyJson = computed(() => {
  if (parsed.value === undefined) return props.json
  return JSON.stringify(parsed.value, null, 2)
})

const minifiedJson = computed(() => {
  if (parsed.value === undefined) return props.json
  return JSON.stringify(parsed.value)
})

const rootSummary = computed(() => {
  if (Array.isArray(parsed.value)) return `${parsed.value.length} items`
  if (parsed.value && typeof parsed.value === 'object')
    return `${Object.keys(parsed.value).length} keys`
  if (parsed.value === undefined) return t('common.invalidJson')
  return typeof parsed.value
})

const stats = computed(() => {
  const source = parsed.value

  let keys = 0
  let arrays = 0
  let objects = 0
  let primitives = 0

  const walk = (value: unknown) => {
    if (Array.isArray(value)) {
      arrays++
      value.forEach(walk)
      return
    }
    if (value !== null && typeof value === 'object') {
      objects++
      const entries = Object.entries(value as Record<string, unknown>)
      keys += entries.length
      entries.forEach(([, child]) => walk(child))
      return
    }
    primitives++
  }

  if (source !== undefined) walk(source)

  return { keys, arrays, objects, primitives }
})

const viewOptions = [
  { get label() { return t('previews.jsonViewer.options.tree') }, value: 'tree' },
  { get label() { return t('previews.jsonViewer.options.pretty') }, value: 'pretty' },
  { get label() { return t('previews.jsonViewer.options.raw') }, value: 'raw' },
]

const setExpanded = (path: string, expanded: boolean) => {
  const next = new Set(expandedPaths.value)
  if (expanded) next.add(path)
  else next.delete(path)
  expandedPaths.value = next
}

const isExpanded = (path: string) => expandedPaths.value.has(path)

const expandAll = () => {
  const next = new Set<string>()

  const walk = (value: unknown, path: string) => {
    if (value !== null && typeof value === 'object') {
