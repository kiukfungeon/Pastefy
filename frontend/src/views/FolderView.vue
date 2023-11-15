<script setup lang="ts">
import PasteList from '@/components/lists/PasteList.vue'
import Button from 'primevue/button'
import FolderList from '@/components/lists/FolderList.vue'
import { useAsyncState, useTitle } from '@vueuse/core'
import type { Folder } from '@/types/folder.ts'
import { client, eventBus } from '@/main.ts'
import { useRoute } from 'vue-router'
import ErrorContainer from '@/components/ErrorContainer.vue'
import LoadingContainer from '@/components/LoadingContainer.vue'
import { useConfirm } from 'primevue/useconfirm'
import router from '@/router'
import { useTranslation } from 'i18next-vue'

useTitle(`Folder | Pastefy`)

const route = useRoute()

const {
  isLoading,
  state: folder,
  error,
} = useAsyncState(async () => {
  const folder = (
    await client.get(`/api/v2/folder/${route.params.folder}` as string, {
      params: {
        shorten_content: 'true',
      },
    })
  ).data as Folder

  useTitle(`${folder.name} | Pastefy`)
  eventBus.emit('pageLoaded', 'folder')
  return folder
}, undefined)

const confirm = useConfirm()
const { t } = useTranslation()
