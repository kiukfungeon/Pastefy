<script setup lang="ts">
import Button from 'primevue/button'
import { computed } from 'vue'

import { usePinnedTools, type PinnedToolKind } from '@/composables/pinned-tools.ts'

const props = withDefaults(
  defineProps<{
    kind: PinnedToolKind
    slug: string
    label?: boolean
    text?: boolean
    outlined?: boolean
    size?: 'small' | 'large'
