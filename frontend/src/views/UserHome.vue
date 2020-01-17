<script setup lang="ts">
import { useTranslation } from 'i18next-vue'
import PasteList from '@/components/lists/PasteList.vue'
import FolderList from '@/components/lists/FolderList.vue'
import { useTitle } from '@vueuse/core'
import { computed } from 'vue'
import ComponentInjection from '@/components/ComponentInjection.vue'
import { eventBus } from '@/main.ts'
import ShowSearchButton from '@/components/ShowSearchButton.vue'
import { useCurrentUserStore } from '@/stores/current-user.ts'
import { useAppStore } from '@/stores/app.ts'
import { useConfig } from '@/composables/config.ts'
import { useRouter } from 'vue-router'

const { t } = useTranslation()
const currentUserStore = useCurrentUserStore()
const appStore = useAppStore()
const config = useConfig()
const router = useRouter()

const quickActions = computed(() => [
  {
    key: 'create-paste',
    title: t('paste.create'),
    get description() { return t('views.userHome.descriptions.openTheEditorAndStartaNewPasteRightAway') },
    icon: 'edit',
  },
  {
    key: 'search-pastes',
    title: t('nav.search'),
    get description() { return t('views.userHome.descriptions.findSnippetsTitlesAndContentAcrossYourSavedWork') },
    icon: 'search',
  },
  {
