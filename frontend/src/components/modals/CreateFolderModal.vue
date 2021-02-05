<script lang="ts" setup>
import Dialog from 'primevue/dialog'
import InputText from 'primevue/inputtext'
import Button from 'primevue/button'
import { ref, watch } from 'vue'
import { useAsyncState } from '@vueuse/core'
import { client } from '@/main.ts'
import router from '@/router'
import type { Folder } from '@/types/folder.ts'
import { useUserFoldersStore } from '@/stores/user-folders.ts'
import ErrorContainer from '@/components/ErrorContainer.vue'
const visible = defineModel<boolean>('visible')

const props = defineProps<{
  parent?: string
}>()

const name = ref('')

watch(visible, () => {
  name.value = ''
})

const userFolderStore = useUserFoldersStore()

const {
  execute: createFolder,
  isLoading,
  error,
} = useAsyncState(
  async () => {
    const folder = (
