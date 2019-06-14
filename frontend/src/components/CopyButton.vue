<script setup lang="ts">
import Button from 'primevue/button'
import { useClipboard } from '@vueuse/core'
import { ref } from 'vue'

const clipboard = useClipboard()
const copied = ref(false)

const props = defineProps<{
  contents: string
  getCopyContents?: () => Promise<string>
}>()

const loading = ref(false)
const copy = async () => {
  if (props?.getCopyContents) {
    loading.value = true
    try {
      clipboard.copy(await props.getCopyContents!())
    } catch {
      console.log('error copying')
