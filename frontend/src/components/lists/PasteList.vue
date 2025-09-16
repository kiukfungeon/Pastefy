<script setup lang="ts">
import { useAsyncState } from '@vueuse/core'
import { client } from '@/main.ts'
import type { Paste } from '@/types/paste.ts'
import PasteCard from '@/components/lists/PasteCard.vue'
import ErrorContainer from '@/components/ErrorContainer.vue'
import LoadingContainer from '@/components/LoadingContainer.vue'
import Pagination from '@/components/Pagination.vue'
import { useRouteQuery } from '@vueuse/router'
import { computed, watch } from 'vue'

const props = defineProps<
  (
    | {
        route: string
        params?: Record<string, string | number>
        pageLimit?: number
      }
    | { pastes: Paste[] }
  ) & { queryPrefix?: string; emptyMessage?: string }
>()

const page = useRouteQuery(`${props.queryPrefix || ''}page`, 1, { transform: Number })
const resolvedPageLimit = computed(() => ('pageLimit' in props ? props.pageLimit || 5 : 5))

const emit = defineEmits(['loaded'])

const {
  isLoading,
  state: pastes,
  error,
  execute: load,
} = useAsyncState(async () => {
  if ('pastes' in props && props.pastes) return props.pastes

  if (!('route' in props) || !props.route) return []

  const res = (
    await client.get(props.route, {
      params: {
        page_limit: resolvedPageLimit.value,
        page: page.value,
        shorten_content: 'true',
