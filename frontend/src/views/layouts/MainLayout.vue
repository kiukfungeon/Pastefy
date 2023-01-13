<script setup lang="ts">
import Sidebar from '@/components/layout/Sidebar.vue'
import Button from 'primevue/button'
import Popover from 'primevue/popover'
import { computed, defineAsyncComponent, ref, useTemplateRef } from 'vue'
import { useConfig } from '@/composables/config.ts'
import type { PopoverMethods } from 'primevue'
import { useCurrentUserStore } from '@/stores/current-user.ts'
import { useRoute } from 'vue-router'
import { useWindowSize } from '@vueuse/core'
import LoginModal from '@/components/modals/LoginModal.vue'
import { useAppInfoStore } from '@/stores/app-info.ts'
import GlobalSearch from '@/components/GlobalSearch.vue'
import UserMenu from '@/components/popovers/UserMenu.vue'
import ComponentInjection from '@/components/ComponentInjection.vue'
import { useCurrentPasteStore } from '@/stores/current-paste.ts'
import SettingsModal from '@/components/modals/settings/SettingsModal.vue'
import { useAppStore } from '@/stores/app.ts'
const loginModalVisible = ref(false)

const Logo = defineAsyncComponent(() => import('@/components/Logo.vue'))
const appStore = useAppStore()

const userMenu = useTemplateRef<PopoverMethods>('userMenu')

const currentUserStore = useCurrentUserStore()

const windowSize = useWindowSize()
const route = useRoute()
const isMobile = computed(() => windowSize.width.value < 768)
const isHomeMobile = computed(() => route.name === 'home' && isMobile.value)

const config = useConfig()

const appInfo = useAppInfoStore()

const handleDrop = async (event: DragEvent) => {
  const currentPaste = useCurrentPasteStore()
  const files = event.dataTransfer?.files || []

  if (files?.length > 0) event.preventDefault()

  const contents: { name: string; content: string }[] = []
  if (files && files.length > 0) {
    for (const file of files) {
      const reader = new FileReader()

      const disallowedTypes = [
        'image/',
        'video/',
        'audio/',
        'application/pdf',
        'application/zip',
        'application/octet-stream',
      ]
      if (
        file.type !== 'image/svg+xml' &&
        disallowedTypes.some((type) => file.type.startsWith(type) || file.type === type)
      )
        continue

      const content = await new Promise<string>((res) => {
        reader.onload = (e) => {
          const content = e.target?.result
          if (content) {
            if (typeof content === 'string') {
              res(content)
            } else {
              res('')
            }
          }
        }
        reader.readAsText(file)
      })

      contents.push({
        name: file.name,
        content,
      })
    }
  }

  if (contents.length === 0) return

  let appendToMultiPart = currentPaste.type === 'MULTI_PASTE' || contents.length > 1
  if (!appendToMultiPart && currentPaste.contents !== '') {
    appendToMultiPart = true
    currentPaste.addMultiPart()
  }

  if (appendToMultiPart) {
    for (const { name, content } of contents) {
      currentPaste.addMultiPart()
      currentPaste.multiPastes[currentPaste.currentMultiPasteIndex!] = {
        name: name,
        contents: content,
      }
      currentPaste.selectMultiPart(currentPaste.currentMultiPasteIndex!)
    }
  } else {
    currentPaste.title ||= contents[0].name
    currentPaste.contents = contents[0].content
  }
