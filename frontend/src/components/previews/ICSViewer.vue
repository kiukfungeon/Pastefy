<script setup lang="ts">
import ICAL from 'ical.js'
import { ref } from 'vue'
import Button from 'primevue/button'

const props = defineProps<{ ics: string }>()

const events = ref<
  {
    uid: string
    summary?: string
    location?: string
    description?: string
    organizer?: string
    attendees?: string[]
    start: Date
    end?: Date
  }[]
>([])
