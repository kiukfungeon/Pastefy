import { createRouter, createWebHistory } from 'vue-router'

import { startViewTransition } from 'vue-view-transitions'
import { eventBus } from '@/main.ts'
import { useConfig } from '@/composables/config.ts'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  scrollBehavior(to) {
    if (to.hash) {
      return {
        el: to.hash,
        top: 96,
        behavior: 'smooth',
      }
    }

    return {
      top: 0,
    }
  },
  routes: [
    {
      path: '/auth',
      name: 'auth',
      component: () => import('@/views/auth/LoginViaAPI.vue'),
    },
    {
      path: '/:paste/embed',
      name: 'paste-embed',
      component: () => import('@/views/PasteEmbed.vue'),
    },
    {
      path: '/tools',
      component: () => import('@/views/layouts/ToolLayout.vue'),
      children: [
        {
          path: '',
          name: 'tool-home',
          component: () => import('@/views/ToolIndexView.vue'),
        },
        {
          path: 'category/:category',
          name: 'tool-category',
          component: () => import('@/views/ToolCategoryView.vue'),
        },
        {
          path: 'convert/:tool',
          name: 'tool-conversion',
          component: () => import('@/views/ConversionToolView.vue'),
        },
        {
          path: 'utility/:tool',
          name: 'tool-utility',
          component: () => import('@/views/UtilityToolView.vue'),
        },
        {
          path: ':tool',
          name: 'tool-preview',
          component: () => import('@/views/PreviewToolView.vue'),
        },
      ],
    },
    {
      path: '/',
      component: () => import('@/views/layouts/MainLayout.vue'),
      children: [
        {
          path: '/',
          name: 'home',
          component: () => import('@/views/HomeView.vue'),
        },
        {
          path: '/home',
          name: 'home-page',
          component: () => import('@/views/HomeView.vue'),
        },
        {
          path: '/explore',
          name: 'explore',
          component: () => import('@/views/ExploreView.vue'),
        },
        {
          path: '/stars',
          name: 'stars',
          component: () => import('@/views/StarsView.vue'),
        },
        {
          path: '/apikeys',
          name: 'api-keys',
          component: () => import('@/views/developers/ApiKeysPage.vue'),
        },
