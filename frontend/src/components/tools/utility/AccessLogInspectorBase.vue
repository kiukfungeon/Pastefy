<script setup lang="ts">
import { useTranslation } from 'i18next-vue'
import Highlighted from '@/components/Highlighted.vue'
import UtilityResultActions from '@/components/tools/utility/UtilityResultActions.vue'
import UtilityShell from '@/components/tools/utility/UtilityShell.vue'
import InputText from 'primevue/inputtext'
import Select from 'primevue/select'
import Textarea from 'primevue/textarea'
import { computed } from 'vue'
import { useStorage } from '@vueuse/core'

const { t } = useTranslation()
const props = defineProps<{
  server: 'nginx' | 'apache'
}>()

type LogRow = {
  ip: string
  time: string
  request: string
  method: string
  path: string
  protocol: string
  status: number
  size: number | null
  referrer: string
  userAgent: string
  raw: string
}

const sampleByServer = {
  nginx: `203.0.113.10 - - [18/Mar/2026:12:14:20 +0100] "GET / HTTP/1.1" 200 1240 "-" "Mozilla/5.0"
203.0.113.10 - - [18/Mar/2026:12:14:23 +0100] "GET /assets/app.js HTTP/1.1" 200 82431 "https://example.com/" "Mozilla/5.0"
203.0.113.11 - - [18/Mar/2026:12:14:27 +0100] "POST /api/login HTTP/1.1" 401 231 "https://example.com/login" "Mozilla/5.0"`,
  apache: `198.51.100.20 - - [18/Mar/2026:12:16:10 +0100] "GET / HTTP/1.1" 200 5120 "-" "Mozilla/5.0"
198.51.100.21 - - [18/Mar/2026:12:16:14 +0100] "GET /health HTTP/1.1" 200 16 "-" "curl/8.5.0"
198.51.100.22 - - [18/Mar/2026:12:16:19 +0100] "GET /admin HTTP/1.1" 403 296 "-" "Mozilla/5.0"`,
}

const source = useStorage(`pastefy-utility-${props.server}-logs-source`, sampleByServer[props.server])
const search = useStorage(`pastefy-utility-${props.server}-logs-search`, '')
const statusFilter = useStorage<'all' | '2xx' | '3xx' | '4xx' | '5xx'>(
  `pastefy-utility-${props.server}-logs-status`,
  'all',
)

const statusOptions = [
  { get label() { return t('utility.accessLogInspectorBase.options.allStatuses') }, value: 'all' as const },
  { get label() { return t('utility.accessLogInspectorBase.options.2xx') }, value: '2xx' as const },
  { get label() { return t('utility.accessLogInspectorBase.options.3xx') }, value: '3xx' as const },
  { get label() { return t('utility.accessLogInspectorBase.options.4xx') }, value: '4xx' as const },
  { get label() { return t('utility.accessLogInspectorBase.options.5xx') }, value: '5xx' as const },
]

const parseLine = (line: string) => {
  const match = line.match(
    /^(\S+)\s+\S+\s+\S+\s+\[([^\]]+)\]\s+"([^"]*)"\s+(\d{3})\s+(\S+)(?:\s+"([^"]*)"\s+"([^"]*)")?/,
  )
  if (!match) return undefined

  const requestParts = match[3].split(' ')
  return {
    ip: match[1],
    time: match[2],
