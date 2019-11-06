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
