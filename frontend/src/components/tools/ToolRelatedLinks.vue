<script setup lang="ts">
defineProps<{
  items: Array<{
    slug: string
    icon: string
    title: string
    meta: string
    to: {
      name: string
      params?: Record<string, string>
      hash?: string
    }
  }>
}>()
</script>
