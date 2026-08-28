<script setup lang="ts">
import { useTranslation } from 'i18next-vue'
import Button from 'primevue/button'
import InputText from 'primevue/inputtext'
import Select from 'primevue/select'
import Tag from 'primevue/tag'
import { useDebounceFn, useTitle } from '@vueuse/core'
import { useRouteQuery } from '@vueuse/router'
import { computed, ref, watch } from 'vue'

import PasteList from '@/components/lists/PasteList.vue'
import { client } from '@/main.ts'
import { useAppStore } from '@/stores/app.ts'
import { useCurrentUserStore } from '@/stores/current-user.ts'
import type { PublicUser } from '@/types/user.ts'

const { t } = useTranslation()
type Scope = 'public' | 'mine' | 'starred'
type VisibilityFilter = 'all' | 'PUBLIC' | 'UNLISTED' | 'PRIVATE'
type TypeFilter = 'all' | 'PASTE' | 'MULTI_PASTE'
type EncryptionFilter = 'all' | 'plain' | 'encrypted'
type FolderFilter = 'all' | 'root' | 'foldered'

const appStore = useAppStore()
const currentUserStore = useCurrentUserStore()

useTitle('Search | Pastefy')

const page = useRouteQuery('page', 1, { transform: Number })
const search = useRouteQuery<string>('search', '')
const scope = useRouteQuery<Scope>('scope', currentUserStore.user ? 'mine' : 'public')
const sort = useRouteQuery('sort', 'createdAt')
const pageLimit = useRouteQuery('page_limit', 12, { transform: Number })
const visibility = useRouteQuery<VisibilityFilter>('visibility', 'all')
const pasteType = useRouteQuery<TypeFilter>('type', 'all')
const encryption = useRouteQuery<EncryptionFilter>('encrypted', 'all')
const folder = useRouteQuery<FolderFilter>('folder', 'all')
const tags = useRouteQuery<string>('tags', '')

const searchInput = ref(search.value)
const tagsInput = ref(tags.value)
const matchedUser = ref<PublicUser | null | undefined>(undefined)

const commitSearch = useDebounceFn((value: string) => {
  search.value = value.trim()
}, 250)

const commitTags = useDebounceFn((value: string) => {
  tags.value = value.trim()
}, 250)

watch(
  () => search.value,
  (value) => {
    if (value !== searchInput.value) searchInput.value = value
  },
)

watch(
  () => tags.value,
  (value) => {
    if (value !== tagsInput.value) tagsInput.value = value
  },
)

watch(
  () => currentUserStore.user,
  (user) => {
    if (user) return
    if (scope.value !== 'public') scope.value = 'public'
  },
)

const scopeOptions = computed(
  () =>
    [
      {
        get label() { return t('views.searchPastes.options.public') },
        value: 'public' as Scope,
        icon: 'world',
        get description() { return t('views.searchPastes.descriptions.searchAllPublicPastes') },
      },
      ...(currentUserStore.user
        ? [
            {
              get label() { return t('views.searchPastes.options.mine') },
              value: 'mine' as Scope,
              icon: 'user',
              get description() { return t('views.searchPastes.descriptions.searchYourOwnPastes') },
            },
            {
              get label() { return t('views.searchPastes.options.starred') },
              value: 'starred' as Scope,
              icon: 'star',
              get description() { return t('views.searchPastes.descriptions.searchYourStarredCollection') },
            },
          ]
        : []),
    ] as const,
)

const sortOptions = [
  { get label() { return t('views.searchPastes.options.newestFirst') }, value: 'createdAt' },
  { get label() { return t('views.searchPastes.options.oldestFirst') }, value: '+createdAt' },
  { get label() { return t('views.searchPastes.options.titleAZ') }, value: '+title' },
  { get label() { return t('views.searchPastes.options.titleZA') }, value: 'title' },
]

const pageLimitOptions = [
  { get label() { return t('views.searchPastes.options.12PerPage') }, value: 12 },
  { get label() { return t('views.searchPastes.options.24PerPage') }, value: 24 },
  { get label() { return t('views.searchPastes.options.48PerPage') }, value: 48 },
]

const visibilityOptions = [
  { get label() { return t('views.searchPastes.options.allVisibilities') }, value: 'all' as VisibilityFilter },
  { get label() { return t('views.searchPastes.options.public') }, value: 'PUBLIC' as VisibilityFilter },
  { get label() { return t('views.searchPastes.options.unlisted') }, value: 'UNLISTED' as VisibilityFilter },
  { get label() { return t('views.searchPastes.options.private') }, value: 'PRIVATE' as VisibilityFilter },
]

const typeOptions = [
  { get label() { return t('views.searchPastes.options.allPasteTypes') }, value: 'all' as TypeFilter },
  { get label() { return t('views.searchPastes.options.singlePastes') }, value: 'PASTE' as TypeFilter },
  { get label() { return t('views.searchPastes.options.multiPastes') }, value: 'MULTI_PASTE' as TypeFilter },
]

const encryptionOptions = [
  { get label() { return t('views.searchPastes.options.allEncryptionStates') }, value: 'all' as EncryptionFilter },
  { get label() { return t('views.searchPastes.options.plainOnly') }, value: 'plain' as EncryptionFilter },
  { get label() { return t('views.searchPastes.options.encryptedOnly') }, value: 'encrypted' as EncryptionFilter },
]
