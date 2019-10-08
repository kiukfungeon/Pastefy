<script setup lang="ts">
import { useTranslation } from 'i18next-vue'
import { computed, watch } from 'vue'
import { useDebounceFn, useAsyncState, useTitle } from '@vueuse/core'
import { useRouteQuery } from '@vueuse/router'
import Button from 'primevue/button'
import InputText from 'primevue/inputtext'
import Select from 'primevue/select'
import Tag from 'primevue/tag'
import { useConfirm } from 'primevue/useconfirm'
import { client } from '@/main.ts'
import LoadingContainer from '@/components/LoadingContainer.vue'
import ErrorContainer from '@/components/ErrorContainer.vue'
import Pagination from '@/components/Pagination.vue'
import type { UserType } from '@/types/user.ts'

useTitle('Users - Admin | pastefy')

const { t } = useTranslation()
type UserAsAdmin = {
  auth_id: string
  auth_provider: string
  avatar: string
  created_at: string
  e_mail: string
  id: string
  name: string
  type: UserType
  unique_name: string
  updated_at: string
}

const page = useRouteQuery('page', 1, { transform: Number })
const pageLimit = useRouteQuery('pageLimit', 10, { transform: Number })
const search = useRouteQuery('search', '')
const userType = useRouteQuery<UserType | ''>('type', '')

const hasNext = computed(() => (users.value?.length || 0) >= pageLimit.value)

const {
  isLoading,
  state: users,
  error,
  execute: load,
} = useAsyncState(async () => {
  return (
    await client.get('/api/v2/admin/users', {
      params: {
        page: page.value,
        page_limit: pageLimit.value,
        search: search.value || undefined,
        ...(userType.value ? { 'filter[type]': userType.value } : {}),
      },
    })
  ).data as UserAsAdmin[]
}, undefined)

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
watch([pageLimit, userType], () => reloadFirstPage())
watch(search, () => debouncedReloadFirstPage())

const setUserType = async (id: string, type: UserType) => {
  confirm.require({
    message: t('admin.users.changeTypeConfirm'),
    header: t('admin.users.changeTypeTitle'),
    accept: async () => {
      await client.put(`/api/v2/admin/users/${id}`, {
        type,
      })
      load()
    },
  })
}

const deleteUser = async (id: string) => {
  confirm.require({
    message: t('admin.users.deleteConfirm'),
    header: t('admin.users.deleteTitle'),
    accept: async () => {
      await client.delete(`/api/v2/admin/users/${id}`)
      load()
    },
  })
}

const typeOptions: Array<{ label: string; value: UserType | '' }> = [
  { get label() { return t('views.adminUsers.options.allRoles') }, value: '' },
  { get label() { return t('views.adminUsers.options.admins') }, value: 'ADMIN' },
  { get label() { return t('views.adminUsers.options.users') }, value: 'USER' },
  { get label() { return t('views.adminUsers.options.awaitingAccess') }, value: 'AWAITING_ACCESS' },
  { get label() { return t('views.adminUsers.options.blocked') }, value: 'BLOCKED' },
]

const pageSizeOptions = [10, 20, 50].map((value) => ({
