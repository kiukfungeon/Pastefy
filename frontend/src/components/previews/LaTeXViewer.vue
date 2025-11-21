<script setup lang="ts">
import { onMounted, ref } from 'vue'

const props = defineProps<{
  latex: string
}>()

const outhtml = ref('')
onMounted(() => {})
</script>
