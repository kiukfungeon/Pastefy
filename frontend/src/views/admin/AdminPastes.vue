<script setup lang="ts">
import { useTranslation } from 'i18next-vue'
import { computed, watch } from 'vue'
import { useAsyncState, useDebounceFn, useTitle } from '@vueuse/core'
import { useRouteQuery } from '@vueuse/router'
import Button from 'primevue/button'
import InputText from 'primevue/inputtext'
import Select from 'primevue/select'
import Tag from 'primevue/tag'
import { useConfirm } from 'primevue/useconfirm'
import { client } from '@/main.ts'
import type { Paste, PasteVisibility } from '@/types/paste.ts'
import LoadingContainer from '@/components/LoadingContainer.vue'
import ErrorContainer from '@/components/ErrorContainer.vue'
import Pagination from '@/components/Pagination.vue'
import PasteCard from '@/components/lists/PasteCard.vue'

useTitle('Pastes - Admin | pastefy')

const { t } = useTranslation()
const page = useRouteQuery('page', 1, { transform: Number })
const pageLimit = useRouteQuery('pageLimit', 10, { transform: Number })
const search = useRouteQuery('search', '')
const visibility = useRouteQuery<PasteVisibility | ''>('visibility', '')
const sort = useRouteQuery('sort', 'createdAt')
const userId = useRouteQuery('userId', '')
const userName = useRouteQuery('userName', '')

const visibilityOptions: Array<{ label: string; value: PasteVisibility | '' }> = [
  { get label() { return t('views.adminPastes.options.allVisibilities') }, value: '' },
  { get label() { return t('views.adminPastes.options.public') }, value: 'PUBLIC' },
  { get label() { return t('views.adminPastes.options.unlisted') }, value: 'UNLISTED' },
  { get label() { return t('views.adminPastes.options.private') }, value: 'PRIVATE' },
]

const sortOptions = [
  { get label() { return t('views.adminPastes.options.newestFirst') }, value: 'createdAt' },
  { get label() { return t('views.adminPastes.options.oldestFirst') }, value: '+createdAt' },
]

const pageSizeOptions = [10, 20, 50].map((value) => ({
  label: `${value} / page`,
  value,
}))

const {
  isLoading,
  state: pastes,
  error,
  execute: load,
} = useAsyncState(async () => {
  return (
    await client.get('/api/v2/paste', {
      params: {
        page: page.value,
        page_limit: pageLimit.value,
        search: search.value || undefined,
        sort: sort.value || undefined,
        shorten_content: 'true',
        ...(visibility.value ? { 'filter[visibility]': visibility.value } : {}),
        ...(userId.value ? { 'filter[userId]': userId.value } : {}),
      },
    })
  ).data as Paste[]
}, undefined)

const hasNext = computed(() => (pastes.value?.length || 0) >= pageLimit.value)
const confirm = useConfirm()

const reloadFirstPage = () => {
  if (page.value !== 1) {
    page.value = 1
    return
  }
  load()
}

const debouncedReloadFirstPage = useDebounceFn(reloadFirstPage, 250)

watch(page, () => load())
watch([pageLimit, visibility, sort, userId], () => reloadFirstPage())
watch(search, () => debouncedReloadFirstPage())

const clearUserFilter = () => {
  userId.value = ''
  userName.value = ''
}

const deletePaste = async (id?: string) => {
  if (!id) return

  confirm.require({
    message: t('paste.deleteConfirm'),
    header: t('paste.deleteTitle'),
    accept: async () => {
      await client.delete(`/api/v2/paste/${id}`)
      load()
    },
  })
}

const formatDate = (value?: string) => {
  if (!value) return t('common.unknownDate')
  return new Date(value.replace(' ', 'T')).toLocaleString()
}
</script>
<template>
  <div class="flex flex-col gap-5">
    <section
      class="grid gap-3 rounded-2xl border border-neutral-200 bg-white/80 p-4 xl:grid-cols-[minmax(0,1fr)_220px_220px_180px] dark:border-neutral-800 dark:bg-neutral-900/70"
    >
      <InputText v-model="search" fluid :placeholder="$t('views.adminPastes.searchByTitleContentOrCreator')" />
      <Select
        v-model="visibility"
        :options="visibilityOptions"
        option-label="label"
        option-value="value"
        fluid
      />
      <Select
        v-model="sort"
        :options="sortOptions"
        option-label="label"
