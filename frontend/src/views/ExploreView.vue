<script setup lang="ts">
import PasteList from '@/components/lists/PasteList.vue'
import { useAsyncState, useTitle } from '@vueuse/core'
import { client } from '@/main.ts'
import type { Tag } from '@/types/tags.ts'
import ErrorContainer from '@/components/ErrorContainer.vue'
import LoadingContainer from '@/components/LoadingContainer.vue'
import TagCard from '@/components/TagCard.vue'
import ShowSearchButton from '@/components/ShowSearchButton.vue'

useTitle(`Explore | Pastefy`)

const {
  isLoading: tagsLoading,
  state: tags,
  error: tagsError,
} = useAsyncState(async () => {
  return (
    await client.get('/api/v2/public/tags', {
      params: {
        page_limit: 4,
