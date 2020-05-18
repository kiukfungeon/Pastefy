<script setup lang="ts">
import { useTranslation } from 'i18next-vue'
import Highlighted from '@/components/Highlighted.vue'
import GeneratorToggleField from '@/components/tools/utility/GeneratorToggleField.vue'
import KeyValueListField from '@/components/tools/utility/KeyValueListField.vue'
import StringListField from '@/components/tools/utility/StringListField.vue'
import UtilityResultActions from '@/components/tools/utility/UtilityResultActions.vue'
import UtilityShell from '@/components/tools/utility/UtilityShell.vue'
import InputText from 'primevue/inputtext'
import Select from 'primevue/select'
import { computed } from 'vue'
import { useStorage } from '@vueuse/core'

import { toKeyValueEntries, toStringEntries, type KeyValueEntry } from '@/utils/container-generators.ts'

const { t } = useTranslation()
const props = defineProps<{
  server: 'caddy' | 'nginx' | 'apache'
}>()

type SiteMode = 'reverse-proxy' | 'static-site'
type TlsMode = 'auto' | 'manual'

const mode = useStorage<SiteMode>(`pastefy-${props.server}-config-mode`, 'reverse-proxy')
const hostEntries = useStorage<string[]>(`pastefy-${props.server}-config-hosts`, ['example.com'])
const enableTls = useStorage(`pastefy-${props.server}-config-tls-enabled`, true)
const tlsMode = useStorage<TlsMode>(`pastefy-${props.server}-config-tls-mode`, 'auto')
const tlsEmail = useStorage(`pastefy-${props.server}-config-tls-email`, 'ops@example.com')
const certPath = useStorage(`pastefy-${props.server}-config-cert-path`, '/etc/letsencrypt/live/example.com/fullchain.pem')
const keyPath = useStorage(`pastefy-${props.server}-config-key-path`, '/etc/letsencrypt/live/example.com/privkey.pem')
const enableWwwRedirect = useStorage(`pastefy-${props.server}-config-www`, true)
const enableCompression = useStorage(`pastefy-${props.server}-config-compression`, true)
const enableSecurityHeaders = useStorage(`pastefy-${props.server}-config-security`, true)
const enableHsts = useStorage(`pastefy-${props.server}-config-hsts`, true)
const enableStaticCaching = useStorage(`pastefy-${props.server}-config-cache`, true)
const enableSpaFallback = useStorage(`pastefy-${props.server}-config-spa`, false)
const enableBasicAuth = useStorage(`pastefy-${props.server}-config-basic-auth`, false)
const basicAuthUser = useStorage(`pastefy-${props.server}-config-basic-auth-user`, 'deploy')
const basicAuthPasswordHash = useStorage(`pastefy-${props.server}-config-basic-auth-hash`, '$2a$14$replace-me')
const rootPath = useStorage(`pastefy-${props.server}-config-root`, '/var/www/example')
const upstreamHost = useStorage(`pastefy-${props.server}-config-upstream-host`, '127.0.0.1')
const upstreamPort = useStorage(`pastefy-${props.server}-config-upstream-port`, '3000')
const trustedProxyEntries = useStorage<string[]>(`pastefy-${props.server}-config-trusted-proxies`, [])
const forwardedIpHeader = useStorage(`pastefy-${props.server}-config-forwarded-ip-header`, 'X-Forwarded-For')
const forwardClientIp = useStorage(`pastefy-${props.server}-config-forwarded-ip-enabled`, true)
const extraHeaderEntries = useStorage<KeyValueEntry[]>(`pastefy-${props.server}-config-extra-headers`, [
  { key: 'X-Robots-Tag', value: 'noindex' },
])
const accessLogPath = useStorage(`pastefy-${props.server}-config-access-log`, `/var/log/${props.server}/access.log`)
const errorLogPath = useStorage(`pastefy-${props.server}-config-error-log`, `/var/log/${props.server}/error.log`)

const modeOptions = [
  { get label() { return t('utility.webServerConfigGeneratorBase.options.reverseProxy') }, value: 'reverse-proxy' as SiteMode },
  { get label() { return t('utility.webServerConfigGeneratorBase.options.staticSite') }, value: 'static-site' as SiteMode },
]

const tlsModeOptions = [
  { label: "Automatic / Let's Encrypt", value: 'auto' as TlsMode },
  { get label() { return t('utility.webServerConfigGeneratorBase.options.manualCertificatePaths') }, value: 'manual' as TlsMode },
]

const hosts = computed(() => {
  const entries = toStringEntries(hostEntries.value)
  return entries.length ? entries : ['example.com']
})

const primaryHost = computed(() => hosts.value[0] || 'example.com')
const trustedProxies = computed(() => toStringEntries(trustedProxyEntries.value))
const headerEntries = computed(() => toKeyValueEntries(extraHeaderEntries.value))

const caddyHeaderLines = computed(() => [
  ...(enableSecurityHeaders.value
    ? [
        '    X-Frame-Options SAMEORIGIN',
        '    X-Content-Type-Options nosniff',
        '    Referrer-Policy strict-origin-when-cross-origin',
        ...(enableHsts.value && enableTls.value
          ? ['    Strict-Transport-Security "max-age=31536000; includeSubDomains"']
          : []),
      ]
    : []),
  ...headerEntries.value.map((entry) => `    ${entry.key} ${entry.value}`),
])

const nginxHeaderLines = computed(() => [
  ...(enableSecurityHeaders.value
    ? [
        '  add_header X-Frame-Options "SAMEORIGIN" always;',
        '  add_header X-Content-Type-Options "nosniff" always;',
        '  add_header Referrer-Policy "strict-origin-when-cross-origin" always;',
        ...(enableHsts.value && enableTls.value
          ? ['  add_header Strict-Transport-Security "max-age=31536000; includeSubDomains" always;']
          : []),
      ]
    : []),
  ...headerEntries.value.map((entry) => `  add_header ${entry.key} "${entry.value}" always;`),
])

const apacheHeaderLines = computed(() => [
  ...(enableSecurityHeaders.value
    ? [
        '  Header always set X-Frame-Options "SAMEORIGIN"',
        '  Header always set X-Content-Type-Options "nosniff"',
        '  Header always set Referrer-Policy "strict-origin-when-cross-origin"',
        ...(enableHsts.value && enableTls.value
          ? ['  Header always set Strict-Transport-Security "max-age=31536000; includeSubDomains"']
          : []),
      ]
    : []),
  ...headerEntries.value.map((entry) => `  Header always set ${entry.key} "${entry.value}"`),
])

const buildCaddySite = (host: string) => {
  const target = `${upstreamHost.value}:${upstreamPort.value}`
  const reverseProxy = mode.value === 'reverse-proxy'
  const autoHttpsDirective = enableTls.value
