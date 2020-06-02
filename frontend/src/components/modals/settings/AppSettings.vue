<script lang="ts" setup>
import Divider from 'primevue/divider'
import { useConfig } from '@/composables/config.ts'
import Checkbox from 'primevue/checkbox'
import ThemeSwitcher from '@/components/ThemeSwitcher.vue'
import PasteVisibilityIcon from '@/components/PasteVisibilityIcon.vue'
import Select from 'primevue/select'
import LanguageSwitcher from '@/components/LanguageSwitcher.vue'

const config = useConfig()
