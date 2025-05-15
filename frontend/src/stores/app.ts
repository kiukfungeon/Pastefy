import { ref } from 'vue'
import { defineStore } from 'pinia'

export const useAppStore = defineStore('app-store', () => {
  const searchShown = ref(false)
  const codeMirrorAvailable = ref(false)
  const createPasteFullscreenRequested = ref(false)
