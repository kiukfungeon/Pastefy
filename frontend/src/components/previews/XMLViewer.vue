<script setup lang="ts">
import { useTranslation } from 'i18next-vue'
import { computed, defineAsyncComponent, ref, watch } from 'vue'
import { useClipboard } from '@vueuse/core'
import Button from 'primevue/button'
import InputText from 'primevue/inputtext'
import Select from 'primevue/select'
import Tag from 'primevue/tag'
import XMLTreeNode, { type XMLNodeEntry } from '@/components/previews/XMLTreeNode.vue'
const { t } = useTranslation()
const Highlighted = defineAsyncComponent(() => import('@/components/Highlighted.vue'))

const props = defineProps<{
  xml: string
}>()

const clipboard = useClipboard()
const search = ref('')
const viewMode = ref<'tree' | 'pretty' | 'raw'>('tree')
const expandedPaths = ref(new Set<string>(['root']))

const parserResult = computed(() => {
  try {
    const parser = new DOMParser()
    const documentNode = parser.parseFromString(props.xml, 'application/xml')
    const errorNode = documentNode.querySelector('parsererror')
    if (errorNode) {
      return {
        root: undefined,
        error: errorNode.textContent || 'Invalid XML',
        pretty: props.xml,
      }
    }

    const convert = (element: Element, path: string): XMLNodeEntry => ({
      name: element.tagName,
      path,
      attributes: Array.from(element.attributes).map((attribute) => ({
        name: attribute.name,
        value: attribute.value,
      })),
      text:
        Array.from(element.childNodes)
          .filter((node) => node.nodeType === Node.TEXT_NODE)
          .map((node) => node.textContent?.trim() || '')
          .filter(Boolean)
          .join(' ') || undefined,
      children: Array.from(element.children).map((child, index) =>
        convert(child, `${path}.${child.tagName}[${index}]`),
      ),
    })

    const serializer = new XMLSerializer()
    return {
      root: documentNode.documentElement
        ? convert(documentNode.documentElement, `root.${documentNode.documentElement.tagName}`)
        : undefined,
      error: undefined,
      pretty: serializer.serializeToString(documentNode),
    }
  } catch (error) {
    return {
      root: undefined,
      error: error instanceof Error ? error.message : 'Invalid XML',
      pretty: props.xml,
    }
  }
})

const rootNode = computed(() => parserResult.value.root)
const prettyXml = computed(() => parserResult.value.pretty)

const viewOptions = [
  { get label() { return t('previews.xmlViewer.options.tree') }, value: 'tree' },
  { get label() { return t('previews.xmlViewer.options.pretty') }, value: 'pretty' },
  { get label() { return t('previews.xmlViewer.options.raw') }, value: 'raw' },
]

const summary = computed(() => {
  if (!rootNode.value) return parserResult.value.error || 'Invalid XML'
  return rootNode.value.name
})

const stats = computed(() => {
  let elements = 0
  let attributes = 0
  let textNodes = 0

  const walk = (node?: XMLNodeEntry) => {
    if (!node) return
    elements++
    attributes += node.attributes.length
    if (node.text) textNodes++
    node.children.forEach(walk)
  }

  walk(rootNode.value)

  return { elements, attributes, textNodes }
})

const setExpanded = (path: string, expanded: boolean) => {
  const next = new Set(expandedPaths.value)
  if (expanded) next.add(path)
  else next.delete(path)
  expandedPaths.value = next
}

const isExpanded = (path: string) => expandedPaths.value.has(path)

const expandAll = () => {
  const next = new Set<string>(['root'])

  const walk = (node?: XMLNodeEntry) => {
    if (!node) return
    next.add(node.path)
    node.children.forEach(walk)
  }

  walk(rootNode.value)
  expandedPaths.value = next
