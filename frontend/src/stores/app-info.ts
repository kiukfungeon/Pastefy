import { ref } from 'vue'
import { defineStore } from 'pinia'
import { client } from '@/main.ts'

export type Config = {
  custom_logo?: string
  custom_footer?: Record<string, string>
  login_required_for_read?: boolean
  login_required_for_create?: boolean
  encryption_is_default?: boolean
  public_pastes_enabled?: boolean
  custom_name?: string
  ai_enabled: boolean
  analytics_enabled: boolean
}

