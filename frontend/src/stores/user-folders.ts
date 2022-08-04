import { ref } from 'vue'
import { defineStore } from 'pinia'
import { client } from '@/main.ts'
import type { Folder } from '@/types/folder.ts'

export const useUserFoldersStore = defineStore('user-folders', () => {
  const folders = ref<Folder[]>([])
  const isLoading = ref<boolean>(false)

  async function fetchFolders() {
    isLoading.value = true
