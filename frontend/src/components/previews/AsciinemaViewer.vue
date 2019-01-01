<script setup lang="ts">
import { computed, onMounted } from 'vue'

import 'asciinema-player/dist/bundle/asciinema-player.css'

// @ts-ignore
import * as AsciinemaPlayer from 'asciinema-player'
const props = defineProps<{
