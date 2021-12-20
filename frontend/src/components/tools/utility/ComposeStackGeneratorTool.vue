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
    includePostgres.value = defaults.includePostgres
    includeRedis.value = defaults.includeRedis
  },
  { immediate: true },
)

const serviceYaml = (
  name: string,
  options: {
    image: string
    containerName?: string
    restart?: RestartPolicy
    ports?: string[]
    env?: { key: string; value: string }[]
    volumes?: string[]
    networks?: string[]
    dependsOn?: string[]
    command?: string
    entrypoint?: string
    workingDir?: string
    replicas?: number
  },
) => {
  const lines = [`  ${name}:`, `    image: ${options.image}`]
  if (options.containerName) lines.push(`    container_name: ${options.containerName}`)
  if (options.restart && options.restart !== 'no') lines.push(`    restart: ${options.restart}`)
  if (options.workingDir) lines.push(`    working_dir: ${options.workingDir}`)
  if (options.ports?.length) {
    lines.push('    ports:')
    lines.push(...options.ports.map((port) => `      - "${port}"`))
  }
  if (options.env?.length) {
    lines.push('    environment:')
    lines.push(...options.env.map((entry) => `      ${entry.key}: ${entry.value}`))
  }
  if (options.volumes?.length) {
    lines.push('    volumes:')
    lines.push(...options.volumes.map((volume) => `      - ${volume}`))
  }
  if (options.dependsOn?.length) {
    lines.push('    depends_on:')
    lines.push(...options.dependsOn.map((service) => `      - ${service}`))
  }
  if (options.networks?.length) {
    lines.push('    networks:')
    lines.push(...options.networks.map((network) => `      - ${network}`))
  }
  if (options.command?.trim()) lines.push(`    command: ${options.command.trim()}`)
  if (options.entrypoint?.trim()) lines.push(`    entrypoint: ${options.entrypoint.trim()}`)
  if ((options.replicas || 1) > 1) {
    lines.push('    deploy:')
    lines.push(`      replicas: ${options.replicas}`)
  }
  return lines.join('\n')
}

const composeFile = computed(() => {
  const appNetworks = toStringEntries(networkEntries.value)
  const output = [
    'services:',
    serviceYaml(serviceName.value, {
      image: image.value,
      containerName: containerName.value.trim() || undefined,
      restart: restartPolicy.value,
      ports: toStringEntries(portMappings.value),
      env: toKeyValueEntries(environmentEntries.value),
      volumes: toStringEntries(volumeEntries.value),
      networks: appNetworks,
      dependsOn: toStringEntries(dependsOnEntries.value),
      command: command.value,
      entrypoint: entrypoint.value,
      workingDir: workingDir.value,
      replicas: replicas.value,
    }),
  ]

  if (includeProxy.value) {
    output.push(
      '',
      serviceYaml('proxy', {
        image: proxyImage.value,
        restart: 'unless-stopped',
        ports: toStringEntries(proxyPorts.value),
        volumes: ['./deploy/nginx.conf:/etc/nginx/conf.d/default.conf:ro'],
        networks: appNetworks.length ? appNetworks : ['app'],
        dependsOn: [serviceName.value],
      }),
    )
  }

  if (includePostgres.value) {
    output.push(
      '',
      serviceYaml('postgres', {
        image: postgresImage.value,
        restart: 'unless-stopped',
        env: toKeyValueEntries(postgresEnv.value),
        volumes: ['postgres_data:/var/lib/postgresql/data'],
        networks: appNetworks.length ? appNetworks : ['app'],
      }),
    )
  }

  if (includeRedis.value) {
    output.push(
      '',
      serviceYaml('redis', {
        image: redisImage.value,
        restart: 'unless-stopped',
        networks: appNetworks.length ? appNetworks : ['app'],
      }),
    )
  }

  const networkSet = new Set([
    ...appNetworks,
    ...(includeProxy.value && !appNetworks.length ? ['app'] : []),
  ])

  if (includePostgres.value) output.push('', 'volumes:', '  postgres_data:')
  if (networkSet.size) {
    output.push('', 'networks:')
    Array.from(networkSet).forEach((network) => output.push(`  ${network}:`))
  }

  return output.join('\n')
})
</script>

<template>
  <UtilityShell
    control-title="Compose Stack"
    control-description="Choose a preset or blank service and visually define ports, envs, volumes, networks, services, and deploy behavior."
    result-title="docker-compose.yml"
    result-description="A more flexible compose baseline for servers, local setups, or self-hosted apps."
  >
    <template #controls>
      <div class="grid gap-3 md:grid-cols-2">
        <div>
          <label class="mb-1 block text-sm text-neutral-500 dark:text-neutral-400">{{ $t('utility.composeStackGeneratorTool.preset') }}</label>
          <Select v-model="preset" :options="presetOptions" option-label="label" option-value="value" fluid />
        </div>
