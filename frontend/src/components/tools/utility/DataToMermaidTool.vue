<script setup lang="ts">
import { useTranslation } from 'i18next-vue'
import Button from 'primevue/button'
import Checkbox from 'primevue/checkbox'
import InputNumber from 'primevue/inputnumber'
import InputText from 'primevue/inputtext'
import MultiSelect from 'primevue/multiselect'
import Select from 'primevue/select'
import Tag from 'primevue/tag'
import Textarea from 'primevue/textarea'
import { computed, ref, watch } from 'vue'
import { useStorage } from '@vueuse/core'

import Highlighted from '@/components/Highlighted.vue'
import MermaidViewer from '@/components/previews/MermaidViewer.vue'
import UtilityResultActions from '@/components/tools/utility/UtilityResultActions.vue'
import UtilityShell from '@/components/tools/utility/UtilityShell.vue'
import { parseYamlDocument } from '@/utils/yaml.ts'

const { t } = useTranslation()
type InputFormat = 'auto' | 'json' | 'yaml' | 'csv'
type ChartType = 'pie' | 'bar' | 'line' | 'grouped-bar' | 'multi-line' | 'combo'
type Aggregation = 'sum' | 'avg' | 'min' | 'max' | 'count'
type SortMode = 'input' | 'label-asc' | 'label-desc' | 'value-desc' | 'value-asc'

type FlatRow = Record<string, string | number | boolean | null>

type ParsedState = {
  format: Exclude<InputFormat, 'auto'>
  rows: FlatRow[]
  error?: string
}

type FieldOption = {
  label: string
  value: string
  numeric: boolean
  sample: string
}

type ExamplePreset = {
  id: string
  label: string
  description: string
  format: Exclude<InputFormat, 'auto'>
  source: string
  chartType: ChartType
  title: string
  yAxisLabel?: string
  categoryFields: string[]
  splitFields?: string[]
  valueFields?: string[]
  aggregation?: Aggregation
}

const jsonExample = `[
  { "month": "Jan", "region": "North", "revenue": 120, "profit": 32, "orders": 12 },
  { "month": "Jan", "region": "South", "revenue": 95, "profit": 20, "orders": 10 },
  { "month": "Feb", "region": "North", "revenue": 140, "profit": 44, "orders": 14 },
  { "month": "Feb", "region": "South", "revenue": 110, "profit": 28, "orders": 11 },
  { "month": "Mar", "region": "North", "revenue": 168, "profit": 51, "orders": 17 },
  { "month": "Mar", "region": "South", "revenue": 126, "profit": 35, "orders": 13 }
]`

const csvExample = `month,region,revenue,profit,orders
Jan,North,120,32,12
Jan,South,95,20,10
Feb,North,140,44,14
Feb,South,110,28,11
Mar,North,168,51,17
Mar,South,126,35,13`

const yamlExample = `sales:
  - month: Jan
    region: North
    revenue: 120
    profit: 32
    orders: 12
  - month: Jan
    region: South
    revenue: 95
    profit: 20
    orders: 10
  - month: Feb
    region: North
    revenue: 140
    profit: 44
    orders: 14
  - month: Feb
    region: South
    revenue: 110
    profit: 28
    orders: 11`

const source = useStorage('pastefy-utility-data-mermaid-source', jsonExample)
const inputFormat = useStorage<InputFormat>('pastefy-utility-data-mermaid-format', 'auto')
const chartType = useStorage<ChartType>('pastefy-utility-data-mermaid-chart-type', 'grouped-bar')
const chartTitle = useStorage('pastefy-utility-data-mermaid-title', 'Revenue by Month')
const yAxisLabel = useStorage('pastefy-utility-data-mermaid-y-axis', 'Value')
const categoryFields = useStorage<string[]>('pastefy-utility-data-mermaid-category-fields', [])
const splitFields = useStorage<string[]>('pastefy-utility-data-mermaid-split-fields', [])
const valueFields = useStorage<string[]>('pastefy-utility-data-mermaid-value-fields', [])
const aggregation = useStorage<Aggregation>('pastefy-utility-data-mermaid-aggregation', 'sum')
const sortMode = useStorage<SortMode>('pastefy-utility-data-mermaid-sort', 'input')
const limit = useStorage<number>('pastefy-utility-data-mermaid-limit', 12)
const includeEverythingElse = useStorage('pastefy-utility-data-mermaid-everything-else', false)
const everythingElseLabel = useStorage(
  'pastefy-utility-data-mermaid-everything-else-label',
  'Everything else',
)
const maxSeries = useStorage<number>('pastefy-utility-data-mermaid-max-series', 8)
const decimalPlaces = useStorage<number>('pastefy-utility-data-mermaid-decimals', 2)
const yAxisMode = useStorage<'auto' | 'manual'>('pastefy-utility-data-mermaid-y-mode', 'auto')
const yAxisMin = useStorage<number>('pastefy-utility-data-mermaid-y-min', 0)
const yAxisMax = useStorage<number>('pastefy-utility-data-mermaid-y-max', 200)
const emptyLabelText = useStorage('pastefy-utility-data-mermaid-empty-label', '(empty)')
const labelSeparator = useStorage('pastefy-utility-data-mermaid-label-separator', ' / ')
const includeSeriesComments = useStorage('pastefy-utility-data-mermaid-comments', true)
const hideZeroSeries = useStorage('pastefy-utility-data-mermaid-hide-zero-series', false)
const hideZeroCategories = useStorage('pastefy-utility-data-mermaid-hide-zero-categories', false)
const pieShowData = useStorage('pastefy-utility-data-mermaid-pie-show-data', true)
const selectedPresetId = ref<string | null>(null)

const formatOptions = [
  { get label() { return t('utility.dataToMermaidTool.options.autoDetect') }, value: 'auto' as InputFormat },
  { get label() { return t('utility.dataToMermaidTool.options.json') }, value: 'json' as InputFormat },
  { get label() { return t('utility.dataToMermaidTool.options.yaml') }, value: 'yaml' as InputFormat },
  { get label() { return t('utility.dataToMermaidTool.options.csv') }, value: 'csv' as InputFormat },
]

const chartTypeOptions = [
  { get label() { return t('utility.dataToMermaidTool.options.pieChart') }, value: 'pie' as ChartType },
  { get label() { return t('utility.dataToMermaidTool.options.barChart') }, value: 'bar' as ChartType },
  { get label() { return t('utility.dataToMermaidTool.options.lineChart') }, value: 'line' as ChartType },
  { get label() { return t('utility.dataToMermaidTool.options.groupedBars') }, value: 'grouped-bar' as ChartType },
  { get label() { return t('utility.dataToMermaidTool.options.multiLine') }, value: 'multi-line' as ChartType },
  { get label() { return t('utility.dataToMermaidTool.options.comboChart') }, value: 'combo' as ChartType },
]

const aggregationOptions = [
  { get label() { return t('utility.dataToMermaidTool.options.sum') }, value: 'sum' as Aggregation },
  { get label() { return t('utility.dataToMermaidTool.options.average') }, value: 'avg' as Aggregation },
  { get label() { return t('utility.dataToMermaidTool.options.min') }, value: 'min' as Aggregation },
  { get label() { return t('utility.dataToMermaidTool.options.max') }, value: 'max' as Aggregation },
  { get label() { return t('utility.dataToMermaidTool.options.count') }, value: 'count' as Aggregation },
]

const sortOptions = [
  { get label() { return t('utility.dataToMermaidTool.options.keepInputOrder') }, value: 'input' as SortMode },
  { get label() { return t('utility.dataToMermaidTool.options.labelAZ') }, value: 'label-asc' as SortMode },
  { get label() { return t('utility.dataToMermaidTool.options.labelZA') }, value: 'label-desc' as SortMode },
  { get label() { return t('utility.dataToMermaidTool.options.valueHighLow') }, value: 'value-desc' as SortMode },
  { get label() { return t('utility.dataToMermaidTool.options.valueLowHigh') }, value: 'value-asc' as SortMode },
]

const yAxisModeOptions = [
  { get label() { return t('utility.dataToMermaidTool.options.autoRange') }, value: 'auto' as const },
  { get label() { return t('utility.dataToMermaidTool.options.manualRange') }, value: 'manual' as const },
]

const examplePresets: ExamplePreset[] = [
  {
    id: 'grouped-bar-sales',
    get label() { return t('utility.dataToMermaidTool.options.groupedBars') },
    get description() { return t('utility.dataToMermaidTool.descriptions.revenueByMonthAndRegion') },
    format: 'json',
    source: jsonExample,
    chartType: 'grouped-bar',
    title: 'Revenue by Month',
    yAxisLabel: 'Revenue',
    categoryFields: ['month'],
    splitFields: ['region'],
    valueFields: ['revenue'],
    aggregation: 'sum',
  },
  {
    id: 'multi-line-profit',
    get label() { return t('utility.dataToMermaidTool.options.multiLineTrends') },
    get description() { return t('utility.dataToMermaidTool.descriptions.profitTrendPerRegionOverTime') },
    format: 'json',
    source: jsonExample,
    chartType: 'multi-line',
    title: 'Profit Trend',
    yAxisLabel: 'Profit',
    categoryFields: ['month'],
    splitFields: ['region'],
    valueFields: ['profit'],
    aggregation: 'sum',
  },
  {
    id: 'pie-orders',
    get label() { return t('utility.dataToMermaidTool.options.pieShare') },
    get description() { return t('utility.dataToMermaidTool.descriptions.orderShareByRegion') },
    format: 'csv',
    source: csvExample,
    chartType: 'pie',
    title: 'Orders by Region',
    categoryFields: ['region'],
    valueFields: ['orders'],
    aggregation: 'sum',
  },
  {
    id: 'combo-revenue-profit',
    get label() { return t('utility.dataToMermaidTool.options.comboChart') },
    get description() { return t('utility.dataToMermaidTool.descriptions.revenueBarsWithProfitLine') },
    format: 'json',
    source: jsonExample,
    chartType: 'combo',
    title: 'Revenue vs Profit',
    yAxisLabel: 'Amount',
    categoryFields: ['month'],
    valueFields: ['revenue', 'profit'],
    aggregation: 'sum',
  },
  {
    id: 'yaml-bars',
    get label() { return t('utility.dataToMermaidTool.options.yamlBarChart') },
    get description() { return t('utility.dataToMermaidTool.descriptions.revenueValuesFromYamlGroupedByMonthAndRegion') },
    format: 'yaml',
    source: yamlExample,
    chartType: 'bar',
    title: 'Revenue Entries',
    yAxisLabel: 'Revenue',
    categoryFields: ['month', 'region'],
    valueFields: ['revenue'],
    aggregation: 'sum',
  },
]

const detectFormat = (value: string): Exclude<InputFormat, 'auto'> => {
  const trimmed = value.trim()
  if (!trimmed) return 'json'
  if (trimmed.startsWith('{') || trimmed.startsWith('[')) return 'json'
  const firstLine = trimmed.split(/\r?\n/, 1)[0] || ''
  if (firstLine.includes(',') && !firstLine.includes(':')) return 'csv'
  return 'yaml'
}

const parseCsv = (raw: string) => {
  const rows: string[][] = []
  let current = ''
  let row: string[] = []
  let inQuotes = false

  const pushCell = () => {
    row.push(current)
    current = ''
  }

  const pushRow = () => {
    if (row.length > 0 || current !== '') {
      pushCell()
      rows.push(row)
      row = []
    }
  }

  for (let i = 0; i < raw.length; i += 1) {
    const char = raw[i]
    const next = raw[i + 1]

    if (char === '"') {
      if (inQuotes && next === '"') {
        current += '"'
        i += 1
      } else {
        inQuotes = !inQuotes
      }
      continue
    }

    if (char === ',' && !inQuotes) {
      pushCell()
      continue
