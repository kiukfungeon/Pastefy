<script setup lang="ts">
import DOMPurify from 'dompurify'
import { computed } from 'vue'

const props = defineProps<{
  html: string
}>()

const sanitizedHtml = computed(() =>
  DOMPurify.sanitize(props.html, {
    USE_PROFILES: { html: true },
  }),
