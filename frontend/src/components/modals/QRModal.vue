<script lang="ts" setup>
import { computed, onMounted, ref, watch } from 'vue'
import { useToast } from 'primevue/usetoast'
import Dialog from 'primevue/dialog'
import QRCode from 'qrcode'
import Button from 'primevue/button'
import Checkbox from 'primevue/checkbox'
import FloatLabel from 'primevue/floatlabel'
import InputNumber from 'primevue/inputnumber'
import InputText from 'primevue/inputtext'
import Select from 'primevue/select'

const props = defineProps<{
  text: string
}>()

const visible = defineModel<boolean>('visible')
const toast = useToast()
const qrCodeDataUrl = ref<string | undefined>(undefined)

const exportFormats = [
  { label: 'PNG', value: 'image/png' },
  { label: 'JPEG', value: 'image/jpeg' },
  { label: 'SVG', value: 'image/svg+xml' },
]

const colorPresets = [
  { label: 'Classic', dark: '#000000', light: '#ffffff' },
  { label: 'Midnight', dark: '#0f172a', light: '#e2e8f0' },
  { label: 'Ocean', dark: '#0f766e', light: '#ecfeff' },
  { label: 'Amber', dark: '#9a3412', light: '#fff7ed' },
  { label: 'Rose', dark: '#9f1239', light: '#fff1f2' },
]

const exportFormat = ref<'image/png' | 'image/jpeg' | 'image/svg+xml'>('image/png')
const width = ref(720)
const margin = ref(2)
const darkColor = ref('#000000')
const lightColor = ref('#ffffff')
const showBackground = ref(true)
const includeQuietZone = ref(true)
const fileName = ref('pastefy-qr')
const logoDataUrl = ref<string | undefined>(undefined)
const logoSize = ref(22)
const logoPadding = ref(12)
const logoBackground = ref(true)

const canCopyImage = computed(
  () => typeof navigator !== 'undefined' && 'clipboard' in navigator && 'ClipboardItem' in window,
)

const downloadExtension = computed(() => {
  if (exportFormat.value === 'image/jpeg') return 'jpg'
  if (exportFormat.value === 'image/svg+xml') return 'svg'
  return 'png'
})

const previewCardStyle = computed(() => ({
  backgroundColor: showBackground.value ? lightColor.value : 'transparent',
}))

const colorPreviewStyle = computed(() => ({
  background: showBackground.value
    ? `linear-gradient(135deg, ${lightColor.value} 0%, ${lightColor.value} 50%, ${darkColor.value} 50%, ${darkColor.value} 100%)`
    : `linear-gradient(135deg, transparent 0%, transparent 50%, ${darkColor.value} 50%, ${darkColor.value} 100%)`,
}))

const previewWrapperStyle = computed(() => ({
  width: `${Math.max(180, width.value)}px`,
  height: `${Math.max(180, width.value)}px`,
}))

const logoWrapperStyle = computed(() => {
  const resolvedSize = Math.max(10, logoSize.value)
  const resolvedPadding = logoBackground.value ? Math.max(0, logoPadding.value) : 0

  return {
    width: `${resolvedSize}%`,
    height: `${resolvedSize}%`,
    padding: `${resolvedPadding}px`,
    backgroundColor: logoBackground.value
      ? showBackground.value
        ? lightColor.value
        : '#ffffff'
      : 'transparent',
  }
})

const applyPreset = (preset?: { dark: string; light: string }) => {
  if (!preset) return
  darkColor.value = preset.dark
  lightColor.value = preset.light
}

const updateLogo = (event: Event) => {
  const input = event.target as HTMLInputElement
  const file = input.files?.[0]

  if (!file) return
  if (!file.type.startsWith('image/')) {
    toast.add({
      severity: 'error',
      summary: 'Invalid file',
      detail: 'Please choose an image file for the logo.',
      life: 4000,
    })
    return
  }

  const reader = new FileReader()
  reader.onload = () => {
    if (typeof reader.result === 'string') {
      logoDataUrl.value = reader.result
    }
  }
  reader.readAsDataURL(file)
  input.value = ''
}

const clearLogo = () => {
  logoDataUrl.value = undefined
}

const buildOptions = () => ({
  type:
    exportFormat.value === 'image/svg+xml'
      ? 'image/png'
      : (exportFormat.value as 'image/png' | 'image/jpeg'),
  width: Math.max(180, width.value),
  margin: includeQuietZone.value ? Math.max(0, margin.value) : 0,
  color: {
    dark: darkColor.value,
    light: showBackground.value ? lightColor.value : '#0000',
  },
})

const renderQRCode = async () => {
  try {
    if (exportFormat.value === 'image/svg+xml') {
      qrCodeDataUrl.value = await QRCode.toString(props.text, {
        type: 'svg',
        width: Math.max(180, width.value),
        margin: includeQuietZone.value ? Math.max(0, margin.value) : 0,
        color: {
          dark: darkColor.value,
          light: showBackground.value ? lightColor.value : '#0000',
        },
      }).then((svg) => `data:image/svg+xml;charset=utf-8,${encodeURIComponent(svg)}`)
      return
    }

    qrCodeDataUrl.value = await QRCode.toDataURL(props.text, buildOptions())
  } catch (error) {
    console.error(error)
    toast.add({
      severity: 'error',
      summary: 'QR generation failed',
      detail: 'The QR code could not be generated.',
      life: 4000,
    })
  }
}

const fetchBlob = async () => {
  if (!qrCodeDataUrl.value) throw new Error('QR code is not ready yet.')
  const response = await fetch(qrCodeDataUrl.value)
  return await response.blob()
}

const download = async () => {
  try {
    const blob = await fetchBlob()
    const link = document.createElement('a')
    const objectUrl = URL.createObjectURL(blob)

    link.download = `${fileName.value || 'pastefy-qr'}.${downloadExtension.value}`
    link.href = objectUrl
    link.click()

    setTimeout(() => URL.revokeObjectURL(objectUrl), 1000)
  } catch (error) {
    console.error(error)
    toast.add({
      severity: 'error',
      summary: 'Download failed',
      detail: 'The QR code could not be downloaded.',
      life: 4000,
    })
  }
}

const copyImage = async () => {
  if (!canCopyImage.value || exportFormat.value === 'image/svg+xml') return

  try {
    const blob = await fetchBlob()
    await navigator.clipboard.write([
      new ClipboardItem({
        [blob.type]: blob,
      }),
    ])
    toast.add({
      severity: 'success',
      summary: 'Copied',
      detail: 'The QR code was copied to your clipboard.',
      life: 2500,
    })
  } catch (error) {
    console.error(error)
    toast.add({
      severity: 'error',
      summary: 'Copy failed',
      detail: 'Your browser did not allow copying the QR image.',
      life: 4000,
    })
  }
}

watch(
  [
    () => props.text,
    visible,
    exportFormat,
    width,
    margin,
    darkColor,
    lightColor,
    showBackground,
