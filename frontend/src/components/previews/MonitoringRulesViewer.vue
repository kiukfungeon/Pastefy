<script setup lang="ts">
import { computed, defineAsyncComponent } from 'vue'
import Tag from 'primevue/tag'
const Highlighted = defineAsyncComponent(() => import('@/components/Highlighted.vue'))

const props = defineProps<{
  config: string
}>()

const alerts = computed(() =>
  Array.from(props.config.matchAll(/\b(alert|title)\s*[:=]\s*["']?([^"'\n]+)["']?/gim)).map(
