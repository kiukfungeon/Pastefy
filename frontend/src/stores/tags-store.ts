import { ref } from 'vue'
import { defineStore } from 'pinia'
import { client } from '@/main.ts'
import type { Tag } from '@/types/tags.ts'

export const useTagsStore = defineStore('tag-cache', () => {
