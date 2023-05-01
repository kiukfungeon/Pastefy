<script setup lang="ts">
import Button from 'primevue/button'
import { useCurrentUserStore } from '@/stores/current-user.ts'
import { useAppStore } from '@/stores/app.ts'
import { useAppInfoStore } from '@/stores/app-info.ts'

const currentUserStore = useCurrentUserStore()
const appStore = useAppStore()
const appInfo = useAppInfoStore()
import { useConfirm } from 'primevue/useconfirm'
import { useTranslation } from 'i18next-vue'
const confirm = useConfirm()
const { t } = useTranslation()

const emit = defineEmits(['elementClicked'])
</script>
<template>
  <Button
    as="router-link"
    :to="{ name: 'home-page' }"
    text
    icon="ti ti-home text-lg"
    :label="$t('paste.myPastes')"
    severity="contrast"
    size="small"
    fluid
    class="flex justify-start md:hidden"
    @click="emit('elementClicked')"
  />
  <Button
    as="router-link"
    :to="{ name: 'explore' }"
    text
    icon="ti ti-world text-lg"
    :label="$t('nav.explore')"
    severity="contrast"
    size="small"
    fluid
    class="flex justify-start md:hidden"
    @click="emit('elementClicked')"
  />
  <Button
    as="router-link"
    :to="{ name: 'stars' }"
    v-if="currentUserStore.user?.logged_in"
    text
    icon="ti ti-star text-lg"
    :label="$t('nav.stars')"
