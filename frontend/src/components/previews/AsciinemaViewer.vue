<script setup lang="ts">
import { computed, onMounted } from 'vue'

import 'asciinema-player/dist/bundle/asciinema-player.css'

// @ts-ignore
import * as AsciinemaPlayer from 'asciinema-player'
const props = defineProps<{
  cast: string
}>()

onMounted(() => {
  const text = props.cast
  const base64 = btoa(
    encodeURIComponent(text).replace(/%([0-9A-F]{2})/g, (_, p1) =>
      String.fromCharCode(parseInt(p1, 16)),
