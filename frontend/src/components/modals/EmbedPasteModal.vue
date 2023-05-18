<script lang="ts" setup>
import Dialog from 'primevue/dialog'
import Checkbox from 'primevue/checkbox'
import Highlighted from '@/components/Highlighted.vue'
import { computed, type Ref, ref } from 'vue'

const props = defineProps<{
  pasteId: string
}>()

const visible = defineModel<boolean>('visible')

const origin = window.location.origin

const hideTitle = ref(false)
const hideActions = ref(false)
const addPadding = ref(false)
const settingsQuery = computed(() => {
  const query: Record<string, string> = {}
  if (hideTitle.value) query.hideTitle = 'true'
