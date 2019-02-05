<script lang="ts" setup>
import { useRoute } from 'vue-router'
import Paste from '@/components/Paste.vue'
import ComponentInjection from '@/components/ComponentInjection.vue'
import { onMounted } from 'vue'

const route = useRoute()

onMounted(async () => {
