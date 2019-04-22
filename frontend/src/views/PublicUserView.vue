<script setup lang="ts">
import { useAsyncState, useTitle } from '@vueuse/core'
import { useRoute } from 'vue-router'
import { client } from '@/main.ts'
import type { PublicUser } from '@/types/user.ts'
import ErrorContainer from '@/components/ErrorContainer.vue'
import LoadingContainer from '@/components/LoadingContainer.vue'
import PasteList from '@/components/lists/PasteList.vue'
import ComponentInjection from '@/components/ComponentInjection.vue'
import Button from 'primevue/button'
import { useCurrentUserStore } from '@/stores/current-user.ts'

useTitle(`User | Pastefy`)

const route = useRoute()
const currentUserStore = useCurrentUserStore()

const {
  isLoading,
  state: user,
  error,
} = useAsyncState(async () => {
  const user = (
    await client.get(`/api/v2/public/user/${route.params.user}` as string, {
      params: {
        shorten_content: 'true',
      },
    })
  ).data as PublicUser

  useTitle(`${user.display_name} | Pastefy`)
  return user
}, undefined)
</script>
