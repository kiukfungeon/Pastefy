import { ref, watch } from 'vue'
import { defineStore } from 'pinia'
import type { MultiPastePart, Paste, PasteType, PasteVisibility } from '@/types/paste.ts'
import CryptoJS from 'crypto-js'
import { useAppInfoStore } from '@/stores/app-info.ts'
import { useConfig } from '@/composables/config.ts'
import router from '@/router'

export const useCurrentPasteStore = defineStore('current-paste', () => {
  const config = useConfig()
  const title = ref('')
  const contents = ref('')
  const visibility = ref<PasteVisibility>(config.value.defaultVisibility || 'UNLISTED')

  const currentId = ref<string | undefined>(undefined)
  const expiresAt = ref<string | undefined>(undefined)
  const expiresAtEnabled = ref(false)
  const type = ref<PasteType>('PASTE')
  const multiPastes = ref<MultiPastePart[]>([])
  const currentMultiPasteIndex = ref<undefined | number>(undefined)
  const encrypted = ref(false)
  const password = ref('')
  const tags = ref<string[]>([])
  const forkedFrom = ref<string | undefined>(undefined)
  const folder = ref<string | undefined>(undefined)
  const ai = ref<boolean>(true)

  const appInfo = useAppInfoStore()

  function forkFrom(paste: Paste) {
    title.value = paste.title
    contents.value = paste.content
    forkedFrom.value = paste.id

    if (paste.type === 'MULTI_PASTE') {
      multiPastes.value = JSON.parse(paste.content)
      type.value = 'MULTI_PASTE'
      selectMultiPart(0)
    }

    if (window.innerWidth < 768) {
      router.push({ name: 'home' })
    } else {
      config.value.sideBarShown = true
    }
  }

  function editFrom(paste: Paste, pw?: string) {
    forkFrom(paste)
    forkedFrom.value = undefined
    password.value = pw || ''

    folder.value = paste.folder
    encrypted.value = paste.encrypted
    visibility.value = paste.visibility
    currentId.value = paste.id
    tags.value = paste.tags || []
    expiresAt.value = paste.expire_at
    if (expiresAt.value) expiresAtEnabled.value = true
