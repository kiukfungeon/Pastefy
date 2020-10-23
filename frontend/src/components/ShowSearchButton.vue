<script setup lang="ts">
import { useAppStore } from '@/stores/app.ts'

const appStore = useAppStore()

const props = defineProps<{
  publicPastes?: boolean
  myPastes?: boolean
