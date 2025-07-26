<script setup lang="ts">
import { useAsyncState, useClipboard, useTitle } from '@vueuse/core'
import { client } from '@/main.ts'
import Button from 'primevue/button'
import LoadingContainer from '@/components/LoadingContainer.vue'
import ErrorContainer from '@/components/ErrorContainer.vue'
import { useConfirm } from 'primevue/useconfirm'
import { AxiosError } from 'axios'
import { useToast } from 'primevue'
import router from '@/router'
import Tabs from 'primevue/tabs'
import TabList from 'primevue/tablist'
import TabPanels from 'primevue/tabpanels'
import TabPanel from 'primevue/tabpanel'
import Tab from 'primevue/tab'
import { useCurrentUserStore } from '@/stores/current-user.ts'
import { useTranslation } from 'i18next-vue'

useTitle('Api-Keys | pastefy')

const toast = useToast()
const { t } = useTranslation()

const currentUserStore = useCurrentUserStore()

const {
  isLoading,
  state: keys,
  error,
  execute: load,
} = useAsyncState(async () => {
  try {
    return (await client.get('/api/v2/user/keys')).data as string[]
  } catch (e) {
    if (e instanceof AxiosError) {
      if (e?.status === 401) {
        toast.add({
          severity: 'error',
          summary: t('apiKeys.loginRequiredSummary'),
          detail: t('apiKeys.loginRequiredDetail'),
          life: 10000,
        })
        router.replace({ name: 'home' })
      }
    }
    throw e
  }
}, undefined)

const confirm = useConfirm()
const deleteKey = async (key: string) => {
  confirm.require({
    message: t('apiKeys.deleteConfirm'),
