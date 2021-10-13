<script setup lang="ts">
import { useTranslation } from 'i18next-vue'
import Highlighted from '@/components/Highlighted.vue'
import GeneratorToggleField from '@/components/tools/utility/GeneratorToggleField.vue'
import UtilityResultActions from '@/components/tools/utility/UtilityResultActions.vue'
import UtilityShell from '@/components/tools/utility/UtilityShell.vue'
import InputText from 'primevue/inputtext'
import Select from 'primevue/select'
import { computed } from 'vue'
import { useStorage } from '@vueuse/core'

const { t } = useTranslation()
type ScriptType = 'sh' | 'bat'

const scriptType = useStorage<ScriptType>('pastefy-utility-jar-script-type', 'sh')
const appName = useStorage('pastefy-utility-jar-script-name', 'my-app')
const jarPath = useStorage('pastefy-utility-jar-script-jar', 'app.jar')
const workingDir = useStorage('pastefy-utility-jar-script-workdir', '/opt/my-app')
const xms = useStorage('pastefy-utility-jar-script-xms', '256m')
const xmx = useStorage('pastefy-utility-jar-script-xmx', '512m')
const profile = useStorage('pastefy-utility-jar-script-profile', 'prod')
const port = useStorage('pastefy-utility-jar-script-port', '8080')
const extraJvmArgs = useStorage('pastefy-utility-jar-script-jvm', '-Dfile.encoding=UTF-8')
const extraAppArgs = useStorage('pastefy-utility-jar-script-app-args', '--server.shutdown=graceful')
const logFile = useStorage('pastefy-utility-jar-script-log', 'app.log')
const useNohup = useStorage('pastefy-utility-jar-script-nohup', true)

const scriptTypeOptions = [
  { get label() { return t('utility.jarStartScriptGeneratorTool.options.startSh') }, value: 'sh' as ScriptType },
  { get label() { return t('utility.jarStartScriptGeneratorTool.options.startBat') }, value: 'bat' as ScriptType },
]

const script = computed(() => {
  if (scriptType.value === 'bat') {
    return `@echo off
set APP_NAME=${appName.value}
set JAR_FILE=${jarPath.value}
set WORKDIR=${workingDir.value}
set JAVA_OPTS=-Xms${xms.value} -Xmx${xmx.value} ${extraJvmArgs.value}
set APP_ARGS=--spring.profiles.active=${profile.value} --server.port=${port.value} ${extraAppArgs.value}

cd /d %WORKDIR%
start /b java %JAVA_OPTS% -jar %JAR_FILE% %APP_ARGS% >> ${logFile.value} 2>&1`
  }

  return `#!/usr/bin/env bash
set -euo pipefail

APP_NAME="${appName.value}"
JAR_FILE="${jarPath.value}"
WORKDIR="${workingDir.value}"
JAVA_OPTS="-Xms${xms.value} -Xmx${xmx.value} ${extraJvmArgs.value}"
APP_ARGS="--spring.profiles.active=${profile.value} --server.port=${port.value} ${extraAppArgs.value}"

cd "$WORKDIR"
${useNohup.value ? `nohup java $JAVA_OPTS -jar "$JAR_FILE" $APP_ARGS >> "${logFile.value}" 2>&1 &` : `java $JAVA_OPTS -jar "$JAR_FILE" $APP_ARGS`}`
})

const fileName = computed(() => (scriptType.value === 'bat' ? 'start.bat' : 'start.sh'))
</script>

<template>
  <UtilityShell
    control-title="JAR Startup Script"
    control-description="Generate a start script for Java JAR deployments with JVM options, profiles, logs, and app args."
    result-title="Generated Script"
    result-description="Use this as a starting point for manual or scripted JAR launches."
