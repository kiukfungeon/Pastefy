<script setup lang="ts">
import PasteVisibilityIcon from '@/components/PasteVisibilityIcon.vue'
import Select from 'primevue/select'

const filters = defineModel<{
  visibility: string
