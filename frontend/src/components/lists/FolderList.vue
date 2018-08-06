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

const {
  isLoading,
  state: folders,
  error,
} = useAsyncState(async () => {
  if ('folders' in props && typeof props.folders !== 'undefined') return props.folders

  if (!('route' in props) || !props.route) return [] as Folder[]

  return (
    await client.get(props.route, {
      params: {
        shorten_content: 'true',
        hide_children: 'true',
        ...(props.params || {}),
      },
    })
  ).data as Folder[]
}, undefined)

const creatFolderModalVisible = ref(false)
</script>
