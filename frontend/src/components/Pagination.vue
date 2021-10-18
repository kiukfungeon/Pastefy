<script setup lang="ts">
import Button from 'primevue/button'

defineProps<{
  loading?: boolean
  nextDisabled?: boolean
}>()

const page = defineModel<number>('page', {
  required: true,
})
