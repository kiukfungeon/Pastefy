<script setup lang="ts">
import { computed, onMounted, reactive, ref, useTemplateRef, watch } from 'vue'
import { client } from '@/main.ts'
import type { AnalyticsBreakdownPoint, AnalyticsResponse } from '@/types/analytics.ts'
import AnalyticsBreakdownCard from '@/components/analytics/AnalyticsBreakdownCard.vue'
import Button from 'primevue/button'
import DatePicker from 'primevue/datepicker'
import InputText from 'primevue/inputtext'
import Popover from 'primevue/popover'
import Select from 'primevue/select'
import type { PopoverMethods } from 'primevue'
import ErrorContainer from '@/components/ErrorContainer.vue'
import LoadingContainer from '@/components/LoadingContainer.vue'
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { LineChart } from 'echarts/charts'
import { GridComponent, TooltipComponent } from 'echarts/components'

use([CanvasRenderer, LineChart, GridComponent, TooltipComponent])

const props = defineProps<{
  endpoint: string
  title: string
  description?: string
}>()

type Filter = { field: string; value: string }
type Metric = 'visits' | 'unique_visitors'
type PanelKey = 'pastes' | 'referrers' | 'locations' | 'devices'

type Dimension = {
  label: string
  value: string
}

type Panel = {
  key: PanelKey
  title: string
  icon: string
  accent: 'amber' | 'rose' | 'blue' | 'green'
  active: string
  dimensions: Dimension[]
}

const summary = ref<AnalyticsResponse>()
const breakdowns = reactive<Record<PanelKey, AnalyticsBreakdownPoint[]>>({
  pastes: [],
  referrers: [],
  locations: [],
  devices: [],
})
const loading = ref(false)
const panelLoading = reactive<Record<PanelKey, boolean>>({
  pastes: false,
  referrers: false,
  locations: false,
  devices: false,
})
const error = ref<unknown>()
const dates = ref<[Date, Date | null]>([new Date(Date.now() - 24 * 60 * 60 * 1000), new Date()])
const interval = ref('hour')
const selectedMetric = ref<Metric>('visits')
const filters = reactive<Filter[]>([])
const rangeLabel = ref('Last 24 hours')
const filterPopover = useTemplateRef<PopoverMethods>('filterPopover')
const rangePopover = useTemplateRef<PopoverMethods>('rangePopover')

const panels = reactive<Panel[]>([
  {
    key: 'pastes',
    title: 'Pastes',
    icon: 'ti ti-file-text',
    accent: 'amber',
    active: 'paste_key',
    dimensions: [
      { label: 'Pastes', value: 'paste_key' },
      { label: 'Visibility', value: 'paste_visibility' },
    ],
  },
  {
    key: 'referrers',
    title: 'Referrers',
    icon: 'ti ti-link',
    accent: 'rose',
    active: 'referer_host',
    dimensions: [
      { label: 'Domains', value: 'referer_host' },
      { label: 'Acquisition', value: 'acquisition' },
      { label: 'Visit type', value: 'visit_type' },
    ],
  },
  {
    key: 'locations',
    title: 'Locations',
    icon: 'ti ti-map-pin',
    accent: 'blue',
    active: 'country',
    dimensions: [
      { label: 'Countries', value: 'country' },
      { label: 'Cities', value: 'city' },
      { label: 'Regions', value: 'region' },
    ],
  },
  {
    key: 'devices',
    title: 'Devices',
    icon: 'ti ti-device-desktop',
    accent: 'green',
    active: 'device_type',
    dimensions: [
      { label: 'Devices', value: 'device_type' },
      { label: 'Browsers', value: 'browser' },
      { label: 'OS', value: 'os' },
    ],
  },
])

const fields = [
  { label: 'Paste key', value: 'paste_key' },
  { label: 'Paste visibility', value: 'paste_visibility' },
  { label: 'Paste owner', value: 'paste_user_id' },
  { label: 'Visit type', value: 'visit_type' },
  { label: 'Country', value: 'country' },
  { label: 'Region', value: 'region' },
  { label: 'City', value: 'city' },
  { label: 'Visitor user', value: 'visitor_user_id' },
  { label: 'Browser', value: 'browser' },
  { label: 'Device type', value: 'device_type' },
  { label: 'OS', value: 'os' },
  { label: 'Referer host', value: 'referer_host' },
  { label: 'Acquisition', value: 'acquisition' },
  { label: 'Bot', value: 'is_bot' },
]

const botTrackingEnabled = computed(() => summary.value?.bot_tracking_enabled !== false)
const availableFields = computed(() =>
  fields.filter(({ value }) => botTrackingEnabled.value || value !== 'is_bot'),
)

const filterOptions: Record<string, { label: string; value: string }[]> = {
  paste_visibility: [
    { label: 'Public', value: 'PUBLIC' },
    { label: 'Unlisted', value: 'UNLISTED' },
    { label: 'Private', value: 'PRIVATE' },
  ],
  visit_type: [
    { label: 'Page', value: 'PAGE' },
    { label: 'RAW', value: 'RAW' },
  ],
  browser: [
    { label: 'Chrome', value: 'CHROME' },
    { label: 'Firefox', value: 'FIREFOX' },
    { label: 'Safari', value: 'SAFARI' },
    { label: 'Edge', value: 'EDGE' },
    { label: 'cURL', value: 'CURL' },
    { label: 'Bot', value: 'BOT' },
    { label: 'Unknown', value: 'UNKNOWN' },
  ],
  device_type: [
    { label: 'Desktop', value: 'DESKTOP' },
    { label: 'Mobile', value: 'MOBILE' },
    { label: 'Tablet', value: 'TABLET' },
    { label: 'Bot', value: 'BOT' },
  ],
  os: [
    { label: 'Windows', value: 'WINDOWS' },
    { label: 'macOS', value: 'MACOS' },
    { label: 'Linux', value: 'LINUX' },
    { label: 'Android', value: 'ANDROID' },
    { label: 'iOS', value: 'IOS' },
    { label: 'Unknown', value: 'UNKNOWN' },
  ],
  acquisition: [
    { label: 'Direct', value: 'DIRECT' },
    { label: 'Internal', value: 'INTERNAL' },
    { label: 'Organic search', value: 'ORGANIC_SEARCH' },
    { label: 'Developer referral', value: 'DEVELOPER_REFERRAL' },
    { label: 'Social', value: 'SOCIAL' },
    { label: 'Referral', value: 'REFERRAL' },
  ],
  is_bot: [
    { label: 'Bots only', value: 'true' },
    { label: 'Humans only', value: 'false' },
  ],
}

