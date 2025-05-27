<script setup lang="ts">
import Button from 'primevue/button'
import InputText from 'primevue/inputtext'

const props = withDefaults(
  defineProps<{
    label: string
    placeholder?: string
    addLabel?: string
  }>(),
  {
    addLabel: 'add entry',
