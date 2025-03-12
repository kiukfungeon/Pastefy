<script lang="ts" setup>
import { useRoute } from 'vue-router'
import Paste from '@/components/Paste.vue'
import ComponentInjection from '@/components/ComponentInjection.vue'
import { onMounted } from 'vue'
import { useUrlSearchParams } from '@vueuse/core'

const route = useRoute()

const settings = useUrlSearchParams<{
  hideTitle?: boolean
  hideActions?: boolean
  addPadding?: boolean
