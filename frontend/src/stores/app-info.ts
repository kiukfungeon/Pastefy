import { ref } from 'vue'
import { defineStore } from 'pinia'
import { client } from '@/main.ts'

export type Config = {
  custom_logo?: string
  custom_footer?: Record<string, string>
  login_required_for_read?: boolean
