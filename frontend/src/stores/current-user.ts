import { ref } from 'vue'
import { defineStore } from 'pinia'
import type { User } from '@/types/user.ts'
import { client } from '@/main.ts'
import { useConfig } from '@/composables/config.ts'
import { useUserFoldersStore } from '@/stores/user-folders.ts'

export const useCurrentUserStore = defineStore('current-user', () => {
  const user = ref<undefined | User>(undefined)
  const authTypes = ref<string[]>([])
  const userLoading = ref(false)

  const config = useConfig()

