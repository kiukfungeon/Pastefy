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
