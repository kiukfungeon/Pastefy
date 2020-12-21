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
            { text: 'API', link: '/api/index.md' },
            { text: 'Self-Hostable', link: '/self-hosting/index.md' },
            { text: 'Tech-Stack', link: '/features/tech-stack.md' },
          ]
        },
        {
          text: 'Integrations',
          items: [
            { text: 'Visual Studio Code', link: '/features/integrations/vscode.md' },
            { text: 'Raycast', link: '/features/integrations/raycast.md' },
            { text: 'Asciinema', link: '/features/integrations/asciinema.md' },
            { text: 'ActivePieces', link: '/features/integrations/active-pieces.md' },
          ]
        }
      ],
      "/self-hosting/": [
        {
          text: 'Self-Hosting',
          items: [
            { text: 'Getting Started', link: '/self-hosting/index.md' },
            { text: 'Installation', link: '/self-hosting/installation', items: [
                { text: 'Docker', link: '/self-hosting/installation/docker.md' },
                { text: 'Docker-Compose', link: '/self-hosting/installation/docker-compose.md' },
            ] },
            { text: 'Configuration (.env)', link: '/self-hosting/configuration.md' },
            { text: 'OAuth / Login', link: '/self-hosting/oauth.md' },
          ]
        }
      ],
      "/api/": [
