<script setup lang="ts">
import { useTranslation } from 'i18next-vue'
import Highlighted from '@/components/Highlighted.vue'
import GeneratorToggleField from '@/components/tools/utility/GeneratorToggleField.vue'
import KeyValueListField from '@/components/tools/utility/KeyValueListField.vue'
import StringListField from '@/components/tools/utility/StringListField.vue'
import UtilityResultActions from '@/components/tools/utility/UtilityResultActions.vue'
import UtilityShell from '@/components/tools/utility/UtilityShell.vue'
import InputNumber from 'primevue/inputnumber'
import InputText from 'primevue/inputtext'
import Select from 'primevue/select'
import { computed, watch } from 'vue'
import { useStorage } from '@vueuse/core'

import {
  composePresetDefaults,
  restartPolicyOptions,
  toKeyValueEntries,
  toStringEntries,
  type KeyValueEntry,
  type ComposePreset,
  type RestartPolicy,
} from '@/utils/container-generators.ts'

const { t } = useTranslation()
const preset = useStorage<ComposePreset>('pastefy-utility-compose-preset', 'proxy-app-postgres')
const serviceName = useStorage('pastefy-utility-compose-service-name', 'app')
const image = useStorage('pastefy-utility-compose-image', 'ghcr.io/acme/app:latest')
const containerName = useStorage('pastefy-utility-compose-container-name', '')
const restartPolicy = useStorage<RestartPolicy>('pastefy-utility-compose-restart', 'unless-stopped')
const replicas = useStorage('pastefy-utility-compose-replicas', 1)
const workingDir = useStorage('pastefy-utility-compose-workdir', '/app')
const portMappings = useStorage<string[]>('pastefy-utility-compose-ports', ['3000:3000'])
const environmentEntries = useStorage<KeyValueEntry[]>('pastefy-utility-compose-env', [{ key: 'NODE_ENV', value: 'production' }])
const volumeEntries = useStorage<string[]>('pastefy-utility-compose-volumes', ['./data:/app/data'])
const networkEntries = useStorage<string[]>('pastefy-utility-compose-networks', ['app'])
const dependsOnEntries = useStorage<string[]>('pastefy-utility-compose-depends', [])
const command = useStorage('pastefy-utility-compose-command', '')
const entrypoint = useStorage('pastefy-utility-compose-entrypoint', '')
const includeProxy = useStorage('pastefy-utility-compose-proxy', true)
const includePostgres = useStorage('pastefy-utility-compose-postgres', true)
const includeRedis = useStorage('pastefy-utility-compose-redis', true)
const proxyImage = useStorage('pastefy-utility-compose-proxy-image', 'nginx:1.27-alpine')
const postgresImage = useStorage('pastefy-utility-compose-postgres-image', 'postgres:16-alpine')
const redisImage = useStorage('pastefy-utility-compose-redis-image', 'redis:7-alpine')
const proxyPorts = useStorage<string[]>('pastefy-utility-compose-proxy-ports', ['80:80', '443:443'])
const postgresEnv = useStorage<KeyValueEntry[]>('pastefy-utility-compose-postgres-env', [
  { key: 'POSTGRES_DB', value: 'app' },
  { key: 'POSTGRES_USER', value: 'app' },
  { key: 'POSTGRES_PASSWORD', value: 'change-me' },
])

const presetOptions = [
  { get label() { return t('utility.composeStackGeneratorTool.options.blankService') }, value: 'blank' as ComposePreset },
  { get label() { return t('utility.composeStackGeneratorTool.options.nodeWebApp') }, value: 'node-web' as ComposePreset },
  { get label() { return t('utility.composeStackGeneratorTool.options.pythonWebApp') }, value: 'python-web' as ComposePreset },
  { get label() { return t('utility.composeStackGeneratorTool.options.javaApp') }, value: 'java-app' as ComposePreset },
  { get label() { return t('utility.composeStackGeneratorTool.options.staticNginxSite') }, value: 'static-nginx' as ComposePreset },
  { get label() { return t('utility.composeStackGeneratorTool.options.proxyAppPostgres') }, value: 'proxy-app-postgres' as ComposePreset },
]

watch(
  preset,
  (value) => {
    const defaults = composePresetDefaults[value]
    serviceName.value = defaults.service
    image.value = defaults.image
    restartPolicy.value = defaults.restart
    portMappings.value = [`${defaults.port}:${defaults.port}`]
    includeProxy.value = defaults.includeProxy
