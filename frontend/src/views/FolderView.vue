<script setup lang="ts">
import PasteList from '@/components/lists/PasteList.vue'
import Button from 'primevue/button'
import FolderList from '@/components/lists/FolderList.vue'
import { useAsyncState, useTitle } from '@vueuse/core'
import type { Folder } from '@/types/folder.ts'
import { client, eventBus } from '@/main.ts'
import { useRoute } from 'vue-router'
import ErrorContainer from '@/components/ErrorContainer.vue'
import LoadingContainer from '@/components/LoadingContainer.vue'
import { useConfirm } from 'primevue/useconfirm'
import router from '@/router'
import { useTranslation } from 'i18next-vue'

useTitle(`Folder | Pastefy`)

const route = useRoute()

const {
