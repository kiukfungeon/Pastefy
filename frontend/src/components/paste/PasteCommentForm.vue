<script setup lang="ts">
import Button from 'primevue/button'
import InputNumber from 'primevue/inputnumber'
import Textarea from 'primevue/textarea'
import { ref } from 'vue'
import { useCurrentUserStore } from '@/stores/current-user.ts'

const props = defineProps<{
  lineFrom?: number
  loading?: boolean
  compact?: boolean
}>()

const currentUser = useCurrentUserStore()

const emit = defineEmits<{
  submit: [content: string, lineTo?: number]
  cancel: []
}>()

const content = ref('')
const lineTo = ref<number>()

const submit = () => {
  const trimmedContent = content.value.trim()
  if (!trimmedContent) return

  emit(
    'submit',
    trimmedContent,
