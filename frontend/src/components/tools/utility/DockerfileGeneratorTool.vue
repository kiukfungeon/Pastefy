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
