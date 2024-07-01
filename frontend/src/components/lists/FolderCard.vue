<script setup lang="ts">
import type { Folder } from '@/types/folder.ts'

defineProps<{
  folder: Folder
  disabled?: boolean
