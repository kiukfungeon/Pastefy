<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useTitle } from '@vueuse/core'
import AnalyticsDashboard from '@/components/analytics/AnalyticsDashboard.vue'

useTitle('Paste analytics | pastefy')
const route = useRoute()
