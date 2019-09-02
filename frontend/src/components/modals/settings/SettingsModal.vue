<script lang="ts" setup>
import Dialog from 'primevue/dialog'
import Button from 'primevue/button'
import AppSettings from '@/components/modals/settings/AppSettings.vue'
import { ref } from 'vue'

const visible = defineModel<boolean>('visible')
const tab = ref('app-settings')
</script>
<template>
