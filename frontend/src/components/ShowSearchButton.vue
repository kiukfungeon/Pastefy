<script setup lang="ts">
import { useAppStore } from '@/stores/app.ts'

const appStore = useAppStore()

const props = defineProps<{
  publicPastes?: boolean
  myPastes?: boolean
}>()

const showSearch = () => {
  appStore.searchShown = true
  appStore.searchShownEndpoints.publicPastes = props.publicPastes || false
  appStore.searchShownEndpoints.myPastes = props.myPastes || false
}
</script>

