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

const addToCalendar = () => {
  const blob = new Blob([props.ics], { type: 'text/calendar' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = 'calendar.ics'
  a.click()
  URL.revokeObjectURL(url)
}

try {
  const jcalData = ICAL.parse(props.ics)
  const comp = new ICAL.Component(jcalData)

  events.value = comp.getAllSubcomponents('vevent').map((e) => {
    const event = new ICAL.Event(e)

    return {
      uid: event.uid,
      summary: event.summary,
      description: event.description,
      location: event.location,
      organizer: event.organizer,
      color: event.color,
      attendees: event.attendees.map((a) => a?.jCal?.[3]),
      start: event.startDate.toJSDate(),
      end: event.endDate?.toJSDate(),
    }
  })
} catch (err) {
  console.error('Failed to parse ICS:', err)
}
</script>
<template>
  <div class="p-4">
    <ul class="space-y-4">
      <li
        v-for="event in events"
