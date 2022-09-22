<script setup lang="ts">
import Checkbox from 'primevue/checkbox'
import Textarea from 'primevue/textarea'
import { computed } from 'vue'
import { useStorage } from '@vueuse/core'
import UtilityShell from '@/components/tools/utility/UtilityShell.vue'
import UtilityResultActions from '@/components/tools/utility/UtilityResultActions.vue'

const input = useStorage(
  'pastefy-utility-sorter-input',
  'redis\npostgres\npastefy\nredis\nnginx\napi',
)
const dedupe = useStorage('pastefy-utility-sorter-dedupe', true)
const reverse = useStorage('pastefy-utility-sorter-reverse', false)
const caseSensitive = useStorage('pastefy-utility-sorter-case', false)

const output = computed(() => {
  let lines = input.value.split(/\r?\n/).filter((line) => line !== '')
  if (dedupe.value) lines = Array.from(new Set(lines))
