<script setup lang="ts">
import { computed, inject, type Ref } from 'vue'

const props = defineProps<{
  controlTitle?: string
  controlDescription?: string
  resultTitle?: string
  resultDescription?: string
  fillHeight?: boolean
}>()

const inheritedFocusMode = inject<Ref<boolean> | undefined>('tool-focus-mode', undefined)
const shouldFillHeight = computed(() => props.fillHeight || inheritedFocusMode?.value || false)
