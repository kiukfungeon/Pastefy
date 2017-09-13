<script setup lang="ts">
import { useAsyncState } from '@vueuse/core'
import { client } from '@/main.ts'
import ErrorContainer from '@/components/ErrorContainer.vue'
import type { Folder } from '@/types/folder.ts'
import FolderCard from '@/components/lists/FolderCard.vue'
import LoadingContainer from '@/components/LoadingContainer.vue'
import CreateFolderModal from '@/components/modals/CreateFolderModal.vue'
import { ref } from 'vue'

const props = defineProps<
  {
    parent?: string
  } & (
    | {
        route: string
        params?: Record<string, string | number>
      }
    | {
        folders: Folder[]
      }
  )
>()
