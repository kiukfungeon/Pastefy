<script setup lang="ts">
import { onMounted } from 'vue'
import { useConfig } from '@/composables/config.ts'
import { useRoute, useRouter } from 'vue-router'
import { client } from '@/main.ts'
import { useCurrentUserStore } from '@/stores/current-user.ts'

const currentUserStore = useCurrentUserStore()
const config = useConfig()
const route = useRoute()
const router = useRouter()

