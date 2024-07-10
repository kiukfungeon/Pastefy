<script lang="ts" setup>
import { computed, ref, useTemplateRef, watch } from 'vue'
import Button from 'primevue/button'
import PasteCard from '@/components/lists/PasteCard.vue'
import { useAsyncState, useDebounceFn } from '@vueuse/core'
import { client } from '@/main.ts'
import type { Paste } from '@/types/paste.ts'
import ErrorContainer from '@/components/ErrorContainer.vue'
import LoadingContainer from '@/components/LoadingContainer.vue'
import { useAppStore } from '@/stores/app.ts'
import router from '@/router'
import { useCurrentUserStore } from '@/stores/current-user.ts'
import TagCard from '@/components/TagCard.vue'
import type { Tag } from '@/types/tags.ts'

const TAG_REGEX = /(@[a-zA-z0-9\n-]*(\s)?)/

const currentUserStore = useCurrentUserStore()

const searchInputValue = ref('')
const search = computed(() => searchInputValue.value.replace(TAG_REGEX, ''))

const currentIndex = ref(1)
const currentTagIndex = ref(0)

const currentFilterTag = computed(() =>
  searchInputValue.value.match(TAG_REGEX)?.[0]?.replace('@', '').trim(),
)

const { state: tags, execute: loadTags } = useAsyncState(async () => {
  if (currentFilterTag.value) return

  const tags = (
    await client.get('/api/v2/public/tags', {
      params: {
        search: search.value,
        page_limit: 4,
      },
    })
  ).data as Tag[]

  currentTagIndex.value = 0

  return tags
}, undefined)

const {
  isLoading,
  state: pastes,
  error,
  execute: loadMyPastes,
} = useAsyncState(
  async () => {
    const pastes: (Paste | string)[] = ['TAGS']

    await loadTags()
    if (appStore.searchShownEndpoints.myPastes && currentUserStore.user) {
      const myPastes = (
        await client.get('/api/v2/user/pastes', {
          params: {
            page_limit: 5,
            shorten_content: 'true',
            search: search.value,
            filter_tags: currentFilterTag.value,
          },
        })
      ).data

      if (myPastes.length > 0) {
        pastes.push('My Pastes', ...myPastes)
      }
    }
    if (appStore.searchShownEndpoints.publicPastes) {
      const publicPastes = (
        await client.get('/api/v2/public-pastes/latest', {
          params: {
            page_limit: 5,
            shorten_content: 'true',
            search: search.value,
            filter_tags: currentFilterTag.value,
          },
        })
      ).data

      if (publicPastes.length > 0) {
        pastes.push('Public Pastes', ...publicPastes)
      }
    }

    currentIndex.value = currentTagIndex.value !== undefined ? 2 : 1

    return pastes
  },
  undefined,
  { immediate: false },
)

const debounceSearch = useDebounceFn(() => {
  loadMyPastes()
}, 500)

const appStore = useAppStore()

const focusInput = (input: HTMLInputElement) => {
  input?.focus()
  setTimeout(() => {
    input?.focus()
  }, 100)
}

const entriesRef = useTemplateRef<typeof PasteCard>('entriesRef')

watch(currentIndex, () => {
  entriesRef.value?.[currentIndex.value!]?.scrollIntoView({
    block: 'end',
    inline: 'nearest',
  })
})

const down = (e: KeyboardEvent) => {
  currentIndex.value++

  while (
    pastes.value &&
    typeof pastes.value[currentIndex.value] === 'string' &&
    pastes.value[currentIndex.value] !== 'TAGS'
  ) {
    currentIndex.value++
  }

  if (pastes.value && currentIndex.value >= pastes.value.length) {
    currentIndex.value = 0
  }
  e.preventDefault()
}
const up = (e: KeyboardEvent) => {
  currentIndex.value--
  while (
    pastes.value &&
    typeof pastes.value[currentIndex.value] === 'string' &&
    pastes.value[currentIndex.value] !== 'TAGS'
  ) {
    currentIndex.value--
  }
  if (pastes.value && currentIndex.value < 0) {
    currentIndex.value = pastes.value.length - 1
  }
  e.preventDefault()
}

const left = () => {
  if (currentIndex.value !== 0) return
  currentTagIndex.value--
  if (tags.value && currentTagIndex.value < 0) {
    currentTagIndex.value = tags.value.length - 1
  }
}
const right = () => {
  if (currentIndex.value !== 0) return
  currentTagIndex.value++
  if (tags.value && currentTagIndex.value >= tags.value.length) {
    currentTagIndex.value = 0
  }
}

let initialLoad = false
watch(
  () => appStore.searchShown,
  () => {
    if (!initialLoad) {
      loadMyPastes()
      initialLoad = true
    }
  },
)

const enter = () => {
  if (currentIndex.value === 0 && currentTagIndex.value !== undefined) {
    if (tags.value && tags.value[currentTagIndex.value]) {
      searchInputValue.value = `@${tags.value[currentTagIndex.value].tag} `
      loadMyPastes()
    }
    return
  }
  entriesRef.value?.[currentIndex.value]?.click()
}

const openFullPageSearch = async () => {
  await router.push({
    name: 'search-pastes',
    query: {
      ...(search.value.trim() ? { search: search.value.trim() } : {}),
      ...(currentFilterTag.value ? { tags: currentFilterTag.value } : {}),
      scope: currentUserStore.user && appStore.searchShownEndpoints.myPastes ? 'mine' : 'public',
    },
  })
  appStore.searchShown = false
}
</script>
<template>
  <div
    class="fixed top-0 left-0 flex h-full w-full items-center justify-center gap-2 bg-black/10 p-3 dark:bg-black/40"
    v-if="appStore.searchShown"
    @click="appStore.searchShown = false"
  >
