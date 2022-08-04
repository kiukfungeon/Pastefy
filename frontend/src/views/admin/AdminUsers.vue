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
