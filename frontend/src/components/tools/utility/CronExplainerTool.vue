<script setup lang="ts">
import { useTranslation } from 'i18next-vue'
import InputText from 'primevue/inputtext'
import Message from 'primevue/message'
import Select from 'primevue/select'
import { computed } from 'vue'
import { useStorage } from '@vueuse/core'
import UtilityShell from '@/components/tools/utility/UtilityShell.vue'
import UtilityResultActions from '@/components/tools/utility/UtilityResultActions.vue'

const { t } = useTranslation()
const cronInput = useStorage('pastefy-utility-cron-input', '*/15 9-17 * * 1-5')
const selectedSample = useStorage('pastefy-utility-cron-sample', '')

const samples = [
  { get label() { return t('utility.cronExplainerTool.options.custom') }, value: '', cron: '' },
  { get label() { return t('utility.cronExplainerTool.options.everyHour') }, value: 'hourly', cron: '0 * * * *' },
  { get label() { return t('utility.cronExplainerTool.options.dailyAtMidnight') }, value: 'daily', cron: '0 0 * * *' },
  { get label() { return t('utility.cronExplainerTool.options.weeklyOnMonday') }, value: 'weekly', cron: '0 9 * * 1' },
  { get label() { return t('utility.cronExplainerTool.options.monthlyOnThe1st') }, value: 'monthly', cron: '0 8 1 * *' },
  { get label() { return t('utility.cronExplainerTool.options.every15Minutes') }, value: 'quarter-hour', cron: '*/15 * * * *' },
  { get label() { return t('utility.cronExplainerTool.options.weekdayMornings') }, value: 'weekday-morning', cron: '0 9 * * 1-5' },
  { get label() { return t('utility.cronExplainerTool.options.yearlyOnJan1st') }, value: 'yearly', cron: '0 0 1 1 *' },
]

const applySample = (value: string) => {
  selectedSample.value = value
  const sample = samples.find((entry) => entry.value === value)
  if (sample?.cron) cronInput.value = sample.cron
}

const cronFields = computed(() => cronInput.value.trim().split(/\s+/).filter(Boolean))
const cronLabels = ['minute', 'hour', 'day of month', 'month', 'day of week']

