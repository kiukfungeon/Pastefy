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
import { computed, watch } from 'vue'
import { useStorage } from '@vueuse/core'

import {
  dockerfilePresetDefaults,
  restartPolicyOptions,
  toCommandArray,
  toKeyValueEntries,
  toStringEntries,
  type KeyValueEntry,
  type DockerfilePreset,
  type RestartPolicy,
} from '@/utils/container-generators.ts'

const { t } = useTranslation()
const preset = useStorage<DockerfilePreset>('pastefy-utility-dockerfile-preset', 'node')
const baseImage = useStorage('pastefy-utility-dockerfile-base-image', 'node:20-alpine')
const runtimeImage = useStorage('pastefy-utility-dockerfile-runtime-image', '')
const buildWorkdir = useStorage('pastefy-utility-dockerfile-build-workdir', '/workspace')
const runtimeWorkdir = useStorage('pastefy-utility-dockerfile-runtime-workdir', '/app')
const copySource = useStorage('pastefy-utility-dockerfile-copy-source', '.')
const copyTarget = useStorage('pastefy-utility-dockerfile-copy-target', '/workspace')
const runCommands = useStorage<string[]>('pastefy-utility-dockerfile-run-commands', ['npm ci', 'npm run build'])
const environmentEntries = useStorage<KeyValueEntry[]>('pastefy-utility-dockerfile-env', [{ key: 'NODE_ENV', value: 'production' }])
const volumeEntries = useStorage<string[]>('pastefy-utility-dockerfile-volumes', ['/app/data'])
const portEntries = useStorage<string[]>('pastefy-utility-dockerfile-ports', ['3000'])
const labelEntries = useStorage<KeyValueEntry[]>('pastefy-utility-dockerfile-labels', [{ key: 'org.opencontainers.image.source', value: 'https://example.com/repo' }])
const networkName = useStorage('pastefy-utility-dockerfile-network', 'app-network')
const restartPolicy = useStorage<RestartPolicy>('pastefy-utility-dockerfile-restart', 'unless-stopped')
const entrypoint = useStorage('pastefy-utility-dockerfile-entrypoint', '')
const command = useStorage('pastefy-utility-dockerfile-command', 'npm run start')
const includeHealthcheck = useStorage('pastefy-utility-dockerfile-healthcheck', false)
const healthcheckCommand = useStorage('pastefy-utility-dockerfile-healthcheck-command', 'wget -qO- http://127.0.0.1:3000/health || exit 1')
const multiStage = useStorage('pastefy-utility-dockerfile-multi-stage', true)

const presetOptions = [
  { get label() { return t('utility.dockerfileGeneratorTool.options.nodeJsApp') }, value: 'node' as DockerfilePreset },
  { get label() { return t('utility.dockerfileGeneratorTool.options.bunApp') }, value: 'bun' as DockerfilePreset },
  { get label() { return t('utility.dockerfileGeneratorTool.options.javaJar') }, value: 'java-jar' as DockerfilePreset },
  { get label() { return t('utility.dockerfileGeneratorTool.options.pythonApp') }, value: 'python' as DockerfilePreset },
  { get label() { return t('utility.dockerfileGeneratorTool.options.staticSiteOnNginx') }, value: 'static-nginx' as DockerfilePreset },
  { get label() { return t('utility.dockerfileGeneratorTool.options.goApp') }, value: 'go' as DockerfilePreset },
  { get label() { return t('utility.dockerfileGeneratorTool.options.rustApp') }, value: 'rust' as DockerfilePreset },
  { get label() { return t('utility.dockerfileGeneratorTool.options.phpApache') }, value: 'php-apache' as DockerfilePreset },
  { get label() { return t('utility.dockerfileGeneratorTool.options.phpFpm') }, value: 'php-fpm' as DockerfilePreset },
  { get label() { return t('utility.dockerfileGeneratorTool.options.dotNetApp') }, value: 'dotnet' as DockerfilePreset },
  { get label() { return t('utility.dockerfileGeneratorTool.options.customImage') }, value: 'custom' as DockerfilePreset },
]

watch(
  preset,
  (value) => {
    const defaults = dockerfilePresetDefaults[value]
    baseImage.value = defaults.base
    runtimeImage.value = defaults.runtimeBase || defaults.base
    runCommands.value = [...defaults.install, ...defaults.build]
    command.value = defaults.start
    portEntries.value = [String(defaults.port)]
  },
  { immediate: true },
)

const dockerfile = computed(() => {
  const envs = toKeyValueEntries(environmentEntries.value)
  const labels = toKeyValueEntries(labelEntries.value)
  const volumes = toStringEntries(volumeEntries.value)
  const ports = toStringEntries(portEntries.value)
  const runs = toStringEntries(runCommands.value)
  const entrypointArray = toCommandArray(entrypoint.value)
  const commandArray = toCommandArray(command.value)
  const defaults = dockerfilePresetDefaults[preset.value]

  const buildStage = [
    `FROM ${baseImage.value.trim()}${multiStage.value ? ' AS build' : ''}`,
    `WORKDIR ${buildWorkdir.value.trim() || '/workspace'}`,
    `COPY ${copySource.value.trim() || '.'} ${copyTarget.value.trim() || '/workspace'}`,
    ...runs.map((line) => `RUN ${line}`),
  ]

  const runtimeLines = [
    `FROM ${multiStage.value ? runtimeImage.value.trim() || baseImage.value.trim() : baseImage.value.trim()}`,
    `WORKDIR ${runtimeWorkdir.value.trim() || '/app'}`,
    multiStage.value
      ? `COPY --from=build ${defaults.copyOutput || `${copyTarget.value.trim() || '/workspace'} ${runtimeWorkdir.value.trim() || '/app'}`}`
      : `COPY ${copySource.value.trim() || '.'} ${runtimeWorkdir.value.trim() || '/app'}`,
    ...envs.map((entry) => `ENV ${entry.key}=${entry.value}`),
    ...labels.map((entry) => `LABEL ${entry.key}=${JSON.stringify(entry.value)}`),
