<script setup lang="ts">
import { useTranslation } from 'i18next-vue'
import { computed, ref, useTemplateRef, watch } from 'vue'
import { useToast } from 'primevue/usetoast'
import domtoimage from 'dom-to-image'
import Dialog from 'primevue/dialog'
import Button from 'primevue/button'
import Checkbox from 'primevue/checkbox'
import FloatLabel from 'primevue/floatlabel'
import InputGroup from 'primevue/inputgroup'
import InputGroupAddon from 'primevue/inputgroupaddon'
import InputNumber from 'primevue/inputnumber'
import Select from 'primevue/select'
import Highlighted from '@/components/Highlighted.vue'

const { t } = useTranslation()
const visible = defineModel<boolean>('visible')

const screenshotAreaRef = useTemplateRef<HTMLDivElement>('screenshotAreaRef')
const toast = useToast()

const props = defineProps<{
  content: string
  title?: string
  id?: string
  fileName?: string
}>()

const backgrounds = [
  { name: 'Ocean', value: 'bg-gradient-to-r from-sky-700 via-cyan-700 to-blue-900' },
  { name: 'Forest', value: 'bg-gradient-to-r from-emerald-700 via-teal-700 to-cyan-900' },
  { name: 'Sunset', value: 'bg-gradient-to-r from-orange-600 via-rose-600 to-fuchsia-800' },
  { name: 'Amber', value: 'bg-gradient-to-r from-amber-500 via-orange-600 to-red-800' },
  { name: 'Graphite', value: 'bg-gradient-to-r from-neutral-800 to-neutral-950' },
  { name: 'Paper', value: 'bg-white' },
  { name: 'Black', value: 'bg-black' },
  { name: 'Transparent', value: 'bg-transparent' },
]

const themes = [
  { get label() { return t('modals.screenshotPaste.options.dark') }, value: 'dark' },
  { get label() { return t('modals.screenshotPaste.options.light') }, value: 'light' },
]

const exportFormats = [
  { get label() { return t('modals.screenshotPaste.options.png') }, value: 'png' },
  { get label() { return t('modals.screenshotPaste.options.jpeg') }, value: 'jpeg' },
]

const scaleOptions = [
  { get label() { return t('modals.screenshotPaste.options.1x') }, value: 1 },
  { get label() { return t('modals.screenshotPaste.options.2x') }, value: 2 },
  { get label() { return t('modals.screenshotPaste.options.3x') }, value: 3 },
]

const background = ref(backgrounds[0].value)
const theme = ref<'dark' | 'light'>('dark')
const exportFormat = ref<'png' | 'jpeg'>('png')
const exportScale = ref(2)
const width = ref(880)
const padding = ref(32)
const showTitleBar = ref(true)
const showLineNumbers = ref(true)

const totalLines = computed(() => Math.max(props.content?.split('\n').length || 1, 1))
const fromLine = ref(1)
const toLine = ref(totalLines.value)

watch(
  totalLines,
  (lineCount) => {
    fromLine.value = Math.min(Math.max(1, fromLine.value), lineCount)
    toLine.value = Math.min(Math.max(fromLine.value, toLine.value), lineCount)
  },
  { immediate: true },
)

const selectedLines = computed(() => {
  const lines = props.content?.split('\n') || []
  const start = Math.max(fromLine.value - 1, 0)
  const end = Math.max(toLine.value, fromLine.value)
  return lines.slice(start, end).join('\n')
})

const fileLabel = computed(() => props.fileName || props.title || 'paste')
const screenshotThemeClass = computed(() => (theme.value === 'dark' ? 'dark text-white' : ''))
const codeShellClass = computed(() =>
  theme.value === 'dark'
    ? 'border-neutral-600 bg-neutral-800/85 text-white'
    : 'border-neutral-200 bg-white/92 text-neutral-900 shadow-sm',
)

const canCopyImage = computed(
  () => typeof navigator !== 'undefined' && 'clipboard' in navigator && 'ClipboardItem' in window,
)

const getImageOptions = () => {
  const node = screenshotAreaRef.value
  if (!node) return undefined

  const scaledWidth = node.offsetWidth * exportScale.value
  const scaledHeight = node.offsetHeight * exportScale.value

  return {
    width: scaledWidth,
    height: scaledHeight,
    bgcolor:
      exportFormat.value === 'jpeg' && background.value === 'bg-transparent'
        ? theme.value === 'dark'
          ? '#0a0a0a'
          : '#ffffff'
        : undefined,
    style: {
      transform: `scale(${exportScale.value})`,
      transformOrigin: 'top left',
      width: `${node.offsetWidth}px`,
      height: `${node.offsetHeight}px`,
    },
  }
}

const createBlob = async () => {
  const node = screenshotAreaRef.value
  if (!node) throw new Error('Screenshot area is not ready yet.')

  const options = getImageOptions()

  if (exportFormat.value === 'jpeg') {
    const dataUrl = await domtoimage.toJpeg(node, {
      ...options,
      quality: 0.96,
    })
    const response = await fetch(dataUrl)
    return {
      blob: await response.blob(),
      extension: 'jpg',
      mime: 'image/jpeg',
    }
  }

  return {
    blob: await domtoimage.toBlob(node, options),
    extension: 'png',
    mime: 'image/png',
  }
}

const download = async () => {
  try {
    const { blob, extension } = await createBlob()
    const link = document.createElement('a')
    const objectUrl = URL.createObjectURL(blob)

    link.download = `${props.fileName?.replace(/\./g, '-') || 'paste-screenshot'}.${extension}`
    link.href = objectUrl
    link.click()

    setTimeout(() => URL.revokeObjectURL(objectUrl), 1000)
  } catch (error) {
    console.error(error)
    toast.add({
      severity: 'error',
      summary: 'Export failed',
      detail: 'The screenshot could not be generated.',
      life: 4000,
    })
  }
}

const copyImage = async () => {
  if (!canCopyImage.value) return

  try {
    const { blob, mime } = await createBlob()
    await navigator.clipboard.write([
      new ClipboardItem({
        [mime]: blob,
      }),
    ])
    toast.add({
      severity: 'success',
      summary: 'Copied',
      detail: 'The screenshot was copied to your clipboard.',
      life: 2500,
    })
  } catch (error) {
    console.error(error)
    toast.add({
      severity: 'error',
      summary: 'Copy failed',
      detail: 'Your browser did not allow copying the image.',
      life: 4000,
    })
