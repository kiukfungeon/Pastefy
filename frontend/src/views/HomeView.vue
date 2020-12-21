<script setup lang="ts">
import { useCurrentUserStore } from '@/stores/current-user.ts'
import { useTitle } from '@vueuse/core'
import UserHome from '@/views/UserHome.vue'
import Highlighted from '@/components/Highlighted.vue'
import FolderCard from '@/components/lists/FolderCard.vue'
const currentUserStore = useCurrentUserStore()
import Button from 'primevue/button'
import { useAppInfoStore } from '@/stores/app-info.ts'
import LoginModal from '@/components/modals/LoginModal.vue'
import { ref } from 'vue'
import { eventBus } from '@/main.ts'

useTitle(`Pastefy • Share your code snippets`)

const appInfo = useAppInfoStore()

const loginModalVisible = ref(false)

const exampleCode = `function sayHello(name: string) {
  const hello =  \`Hello \${name}!\`;
}

sayHello('World')`
const exampleIntegrationCode = `import { PastefyClient }
    from '@interaapps/pastefy';

const client
  = new PastefyClient(apiKey${window.location.origin !== 'https://pastefy.app' ? `, "${window.location.origin}"` : ''});

console.log(
  await client.getPaste('id')
);

await client.createPaste({
  title: 'Hello World',
  content: 'This is a sample paste.'
});`
</script>

<template>
  <main class="mx-auto w-full max-w-[1200px]">
    <UserHome v-if="currentUserStore.user" />
    <div v-else :ref="() => eventBus.emit('pageLoaded', 'home')">
      <div class="mx-auto mb-10 max-w-[1000px]">
        <div class="mb-5 flex items-center justify-between">
          <h1 class="text-3xl font-bold">{{ $t('views.homeView.pastefy') }}</h1>

          <Button
            v-if="!currentUserStore.user && currentUserStore.authTypes?.[0]"
            @click="currentUserStore.authTypes?.length > 1 ? (loginModalVisible = true) : null"
            :as="currentUserStore.authTypes?.length === 1 ? 'a' : 'button'"
            :href="`/api/v2/auth/oauth2/${currentUserStore.authTypes[0]}`"
            icon="ti ti-user text-lg"
            severity="contrast"
            :label="$t('auth.login')"
            text
            size="small"
            :loading="currentUserStore.userLoading"
          />
        </div>

        <div class="mb-3 flex flex-col gap-3 md:grid md:h-[18rem] md:grid-cols-[2fr_1fr]">
          <div
            class="flex h-full flex-col items-end overflow-hidden rounded-xl border border-neutral-200 bg-neutral-100 md:grid md:grid-cols-2 dark:border-neutral-700 dark:bg-neutral-800"
          >
            <div class="flex h-full flex-col gap-3 p-4">
              <h2 class="text-xl font-bold">{{ $t('home.shareCode') }}</h2>
              <p>{{ $t('home.shareCodeDescription') }}</p>
            </div>
            <div>
              <div
                class="rounded-tl-md border-t border-l border-neutral-300 dark:border-neutral-700"
              >
                <Highlighted :contents="exampleCode" file-name=".js" />
              </div>
            </div>
          </div>

          <div
            class="flex h-full flex-col justify-between overflow-hidden rounded-xl border border-neutral-200 bg-neutral-100 dark:border-neutral-700 dark:bg-neutral-800"
          >
            <div class="flex h-full flex-col gap-1 p-4 pb-0">
