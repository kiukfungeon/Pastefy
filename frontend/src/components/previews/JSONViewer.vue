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
