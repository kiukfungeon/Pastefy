import { defineConfig } from 'vitepress'
import { useSidebar } from 'vitepress-openapi'
import spec from '../openapi.json' with { type: 'json' }

const sidebar = useSidebar({
  spec
})

// https://vitepress.dev/reference/site-config
export default defineConfig({
  title: "Pastefy Docs",
  description: "Pastefy Docs",
  themeConfig: {
    // https://vitepress.dev/reference/default-theme-config
    nav: [
      { text: 'API', link: '/api/index' },
      { text: 'Features', link: '/features/index' },
      { text: 'Self-Hosting', link: '/self-hosting/index' },
      { text: 'InteraApps', link: 'https://interaapps.de' },
      { text: 'Pastefy', link: 'https://pastefy.app' },
    ],

    sidebar: {
      "/features/": [
        {
          text: 'Features',
          items: [
            { text: 'Getting Started', link: '/features/index.md' },
            { text: 'Previews', link: '/features/previews.md' },
