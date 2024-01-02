<script setup lang="ts">
import { computed, defineAsyncComponent } from 'vue'
import Tag from 'primevue/tag'
const Highlighted = defineAsyncComponent(() => import('@/components/Highlighted.vue'))

const props = defineProps<{
  playbook: string
}>()

const hosts = computed(() =>
  Array.from(props.playbook.matchAll(/^\s*hosts\s*:\s*(.+)$/gm)).map((match) => match[1].trim()),
)
const roles = computed(() =>
  Array.from(props.playbook.matchAll(/^\s*-\s*role\s*:\s*(.+)$/gm)).map((match) => match[1].trim()),
)
