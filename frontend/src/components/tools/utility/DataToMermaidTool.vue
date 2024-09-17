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
    }

    if ((char === '\n' || char === '\r') && !inQuotes) {
      if (char === '\r' && next === '\n') i += 1
      pushRow()
      continue
    }

    current += char
  }

  if (current !== '' || row.length > 0) pushRow()
  return rows
}

const isRecord = (value: unknown): value is Record<string, unknown> =>
  value !== null && typeof value === 'object' && !Array.isArray(value)

const normalizePrimitive = (value: unknown) => {
  if (value === null || value === undefined) return null
  if (typeof value === 'number' || typeof value === 'boolean' || typeof value === 'string') {
    return value
  }
  return JSON.stringify(value)
}

const flattenRecord = (
  value: unknown,
  prefix = '',
  output: FlatRow = {},
): FlatRow => {
  if (Array.isArray(value)) {
    output[prefix || 'value'] = value.every((entry) => !isRecord(entry))
      ? value.map((entry) => normalizePrimitive(entry)).join(', ')
      : JSON.stringify(value)
    return output
  }

  if (!isRecord(value)) {
    output[prefix || 'value'] = normalizePrimitive(value)
    return output
  }

  Object.entries(value).forEach(([key, entry]) => {
    const nextKey = prefix ? `${prefix}.${key}` : key
    if (isRecord(entry)) {
      flattenRecord(entry, nextKey, output)
      return
    }

    if (Array.isArray(entry)) {
      output[nextKey] = entry.every((item) => !isRecord(item))
        ? entry.map((item) => normalizePrimitive(item)).join(', ')
        : JSON.stringify(entry)
      return
    }

    output[nextKey] = normalizePrimitive(entry)
  })

  return output
}

const normalizeToRows = (value: unknown): FlatRow[] => {
  if (Array.isArray(value)) {
    if (!value.length) return []
    return value.map((entry, index) =>
      isRecord(entry)
        ? flattenRecord(entry)
        : {
            index: index + 1,
            value: normalizePrimitive(entry),
          },
    )
  }

  if (isRecord(value)) {
    const entries = Object.entries(value)
    const arrayEntries = entries.filter(([, entry]) => Array.isArray(entry))

    if (arrayEntries.length === 1) {
      return normalizeToRows(arrayEntries[0][1])
    }

    if (entries.length && entries.every(([, entry]) => isRecord(entry))) {
      return entries.map(([key, entry]) => ({
        key,
        ...flattenRecord(entry),
      }))
    }

    return [flattenRecord(value)]
  }

  return [{ value: normalizePrimitive(value) }]
}

const parseInput = (raw: string, format: InputFormat): ParsedState => {
  const resolvedFormat = format === 'auto' ? detectFormat(raw) : format

  try {
    if (resolvedFormat === 'json') {
      return {
        format: 'json',
        rows: normalizeToRows(JSON.parse(raw)),
      }
    }

    if (resolvedFormat === 'yaml') {
      return {
        format: 'yaml',
        rows: normalizeToRows(parseYamlDocument(raw)),
      }
    }

    const csvRows = parseCsv(raw)
    if (!csvRows.length) {
      return {
        format: 'csv',
        rows: [],
      }
    }

    const [headers, ...body] = csvRows
    return {
      format: 'csv',
      rows: body.map((row) =>
        headers.reduce<FlatRow>((acc, header, index) => {
          acc[header] = row[index] ?? ''
          return acc
        }, {}),
      ),
    }
  } catch (error) {
    return {
      format: resolvedFormat,
      rows: [],
      error: error instanceof Error ? error.message : 'Could not parse this input.',
    }
  }
}

const toNumber = (value: unknown) => {
  if (typeof value === 'number' && Number.isFinite(value)) return value
  if (typeof value === 'string' && value.trim() !== '' && Number.isFinite(Number(value))) {
    return Number(value)
  }
  return undefined
}

const normalizeMermaidText = (value: unknown, fallback = '(empty)') => {
  const normalized = String(value ?? '')
    .replace(/\r?\n|\r/g, ' ')
    .replace(/\s+/g, ' ')
    .trim()

  return normalized || fallback
}

const escapeMermaidLabel = (value: unknown, fallback = '(empty)') =>
  JSON.stringify(normalizeMermaidText(value, fallback))

const escapeMermaidComment = (value: unknown, fallback = 'Series') =>
  normalizeMermaidText(value, fallback).replace(/[%[\]{}]/g, '')

const parsedState = computed(() => parseInput(source.value, inputFormat.value))
const selectedPreset = computed(
  () => examplePresets.find((preset) => preset.id === selectedPresetId.value) || null,
)

const fieldOptions = computed<FieldOption[]>(() => {
  const fields = new Map<string, { numericCount: number; sample: string; total: number }>()

  parsedState.value.rows.forEach((row) => {
    Object.entries(row).forEach(([key, value]) => {
      const entry = fields.get(key) || { numericCount: 0, sample: '', total: 0 }
      entry.total += 1
      if (!entry.sample && value !== null && value !== undefined && String(value) !== '') {
        entry.sample = String(value)
      }
      if (toNumber(value) !== undefined) entry.numericCount += 1
      fields.set(key, entry)
    })
  })

  return Array.from(fields.entries())
    .map(([key, value]) => ({
      label: key,
      value: key,
      numeric: value.numericCount > 0,
      sample: value.sample,
    }))
    .sort((a, b) => a.label.localeCompare(b.label))
})

const numericFieldOptions = computed(() => fieldOptions.value.filter((field) => field.numeric))
const dimensionFieldOptions = computed(() => fieldOptions.value)

watch(
  fieldOptions,
  (fields) => {
    const available = new Set(fields.map((field) => field.value))
    const firstDimension =
      fields.find((field) => !field.numeric)?.value || fields[0]?.value || ''
    const firstNumeric = numericFieldOptions.value[0]?.value || ''

    const nextCategoryFields = categoryFields.value.filter((field) => available.has(field))
    if (!nextCategoryFields.length && firstDimension) {
      categoryFields.value = [firstDimension]
    } else if (nextCategoryFields.length !== categoryFields.value.length) {
      categoryFields.value = nextCategoryFields
    }

    const nextSplitFields = splitFields.value.filter((field) => available.has(field))
    if (nextSplitFields.length !== splitFields.value.length) {
      splitFields.value = nextSplitFields
    }

    const filteredValues = valueFields.value.filter((value) => available.has(value))
    if (!filteredValues.length && firstNumeric) {
      valueFields.value = [firstNumeric]
    } else if (filteredValues.length !== valueFields.value.length) {
      valueFields.value = filteredValues
    }
  },
  { immediate: true },
)

const buildLabelFromFields = (
  row: FlatRow,
  fields: string[],
  fallback: string,
) => {
  if (!fields.length) return fallback
  return normalizeMermaidText(
    fields
      .map((field) => normalizeMermaidText(row[field], emptyLabelText.value || '(empty)'))
      .join(labelSeparator.value || ' / '),
    fallback,
  )
}

const seriesConfig = computed(() => {
  const warnings: string[] = []

  if (!categoryFields.value.length) {
    return {
      warnings: ['Choose a category field to build a chart.'],
      categories: [] as string[],
      series: [] as { name: string; values: number[] }[],
    }
  }

  const chartNeedsSingleSeries =
    chartType.value === 'pie' || chartType.value === 'bar' || chartType.value === 'line'
  const effectiveValueFields =
    aggregation.value === 'count'
      ? ['__count__']
      : splitFields.value.length
        ? valueFields.value.slice(0, 1)
        : valueFields.value

  if (splitFields.value.length && valueFields.value.length > 1) {
    warnings.push('Split-by uses the first selected numeric field to keep Mermaid output readable.')
  }

  if (maxSeries.value < 1) {
    warnings.push('Set the max series value to at least 1.')
  }

  if (!effectiveValueFields.length) {
    return {
      warnings: ['Choose at least one numeric field or switch the aggregation to count.'],
      categories: [] as string[],
