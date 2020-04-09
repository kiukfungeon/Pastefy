<script setup lang="ts">
import { computed, defineAsyncComponent } from 'vue'
import Tag from 'primevue/tag'
const Highlighted = defineAsyncComponent(() => import('@/components/Highlighted.vue'))

const props = defineProps<{
  workflow: string
}>()

const triggers = computed(() =>
  Array.from(props.workflow.matchAll(/^\s{0,2}([a-z_]+)\s*:/gim))
    .map((match) => match[1])
    .filter((name) => ['push', 'pull_request', 'workflow_dispatch', 'schedule'].includes(name)),
)

const jobs = computed(() =>
  Array.from(props.workflow.matchAll(/^\s{2}([a-zA-Z0-9_-]+)\s*:\s*$/gm)).map((match) => ({
