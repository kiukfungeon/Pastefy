<script setup lang="ts">
import Highlighted from '@/components/Highlighted.vue'
import UtilityResultActions from '@/components/tools/utility/UtilityResultActions.vue'
import UtilityShell from '@/components/tools/utility/UtilityShell.vue'
import InputText from 'primevue/inputtext'
import Textarea from 'primevue/textarea'
import { computed } from 'vue'
import { useStorage } from '@vueuse/core'

const serviceName = useStorage('pastefy-utility-systemd-name', 'my-app')
const description = useStorage('pastefy-utility-systemd-description', 'My application service')
const user = useStorage('pastefy-utility-systemd-user', 'www-data')
const group = useStorage('pastefy-utility-systemd-group', 'www-data')
const workingDir = useStorage('pastefy-utility-systemd-workdir', '/opt/my-app')
const execStart = useStorage('pastefy-utility-systemd-exec-start', '/usr/bin/java -jar /opt/my-app/app.jar')
const restartPolicy = useStorage('pastefy-utility-systemd-restart', 'on-failure')
const environment = useStorage('pastefy-utility-systemd-env', 'SPRING_PROFILES_ACTIVE=prod\nSERVER_PORT=8080')

const serviceFile = computed(() => `[Unit]
Description=${description.value}
After=network.target

[Service]
Type=simple
User=${user.value}
Group=${group.value}
WorkingDirectory=${workingDir.value}
${environment.value
  .split(/\r?\n/)
  .map((line) => line.trim())
  .filter(Boolean)
  .map((line) => `Environment="${line}"`)
  .join('\n')}
ExecStart=${execStart.value}
Restart=${restartPolicy.value}
RestartSec=5

[Install]
