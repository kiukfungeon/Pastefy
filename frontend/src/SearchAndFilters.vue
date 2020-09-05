<script setup lang="ts">
import InputText from 'primevue/inputtext'

const page = defineModel<number>('page', {
  required: true,
})

const search = defineModel<string>('search')
</script>
<template>
