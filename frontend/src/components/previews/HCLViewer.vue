<script setup lang="ts">
import { computed, defineAsyncComponent } from 'vue'
import Tag from 'primevue/tag'
const Highlighted = defineAsyncComponent(() => import('@/components/Highlighted.vue'))

const props = defineProps<{
  hcl: string
  fileName?: string
}>()

const blocks = computed(() =>
  Array.from(
    props.hcl.matchAll(
      /^\s*(resource|module|variable|output|provider|data|locals|terraform)\s+"?([^"\s{]+)?"?\s*"?(.*?)"?\s*\{/gm,
    ),
  ).map((match, index) => ({
    kind: match[1],
    name: [match[2], match[3]].filter(Boolean).join(' / ') || `block-${index + 1}`,
  })),
)

const stats = computed(() => {
  const count = (kind: string) => blocks.value.filter((block) => block.kind === kind).length
