<script setup lang="ts">
import Textarea from 'primevue/textarea'

defineProps<{
  label: string
  placeholder?: string
  rows?: number
}>()

const model = defineModel<string>({ required: true })
</script>

