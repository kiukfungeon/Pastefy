<script setup lang="ts">
import { computed, ref } from 'vue'
import InputText from 'primevue/inputtext'
import Select from 'primevue/select'
import Tag from 'primevue/tag'

const props = defineProps<{
  logs: string
}>()

const search = ref('')
const levelFilter = ref<'ALL' | 'ERROR' | 'WARN' | 'INFO' | 'DEBUG'>('ALL')

const entries = computed(() =>
  props.logs.split('\n').map((line, index) => {
    const levelMatch = line.match(/\b(ERROR|WARN|WARNING|INFO|DEBUG|TRACE|FATAL)\b/i)
    const level = (levelMatch?.[1] || 'RAW').toUpperCase().replace('WARNING', 'WARN')
    return { index, line, level }
  }),
)

const filteredEntries = computed(() => {
  return entries.value.filter((entry) => {
    if (levelFilter.value !== 'ALL' && entry.level !== levelFilter.value) return false
    if (search.value.trim() && !entry.line.toLowerCase().includes(search.value.trim().toLowerCase()))
      return false
    return true
  })
})
