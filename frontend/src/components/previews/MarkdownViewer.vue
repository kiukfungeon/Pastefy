<script setup lang="ts">
import markdownit from 'markdown-it'
import { defineAsyncComponent, defineCustomElement, onMounted, ref, watch } from 'vue'
import { useDebounceFn } from '@vueuse/core'

const props = defineProps<{
  markdown: string
}>()

const useMermaidViewer = () => {
  if (customElements.get('pastefy-mermaid-viewer')) return
  customElements.define(
    'pastefy-mermaid-viewer',
    defineCustomElement(
