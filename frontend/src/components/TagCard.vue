<script setup lang="ts">
import type { Tag } from '@/types/tags.ts'

defineProps<{
  tag: Tag
  small?: boolean
  selected?: boolean
}>()
