<script setup lang="ts">
import 'leaflet/dist/leaflet.css'

import { computed, onMounted, ref, watch } from 'vue'
import { useDebounceFn } from '@vueuse/core'
import { LGeoJson, LMap, LTileLayer } from '@vue-leaflet/vue-leaflet'
import type { GeoJsonObject } from 'geojson'
import Button from 'primevue/button'

const props = defineProps<{
  geoJson: string
  inEditor?: boolean
}>()

const render = async () => {}
onMounted(() => {
  render()
})
watch(
  () => props.geoJson,
  useDebounceFn(() => {
    render()
  }, 500),
)

const parsedGeoJson = computed(() => JSON.parse(props.geoJson) as GeoJsonObject)

const zoom = ref(2)
</script>
<template>
  <div
    class="geo-json-group relative w-full overflow-hidden text-sm"
    :class="inEditor ? '' : 'h-[700px] rounded-xl'"
  >
