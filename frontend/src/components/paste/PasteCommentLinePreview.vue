<script setup lang="ts">
import Button from 'primevue/button'
import { computed, ref } from 'vue'
import Highlighted from '@/components/Highlighted.vue'

const props = defineProps<{
  contents: string
  fileName?: string
  lineFrom: number
  lineTo?: number
}>()

const expanded = ref(false)
