import type { RouteLocationRaw } from 'vue-router'

import { conversionTools } from '@/utils/conversion-tools.ts'
import { previewTools } from '@/utils/preview-tools.ts'
import { utilityTools } from '@/utils/utility-tools.ts'

export type ToolCategorySlug =
  | 'docs'
  | 'data'
  | 'visual'
  | 'mermaid'
  | 'api'
  | 'infra'
  | 'operations'
  | 'server'
  | 'deploy'
  | 'automation'
  | 'security'
  | 'terminal'
  | 'convert'
  | 'inspect'
  | 'generate'
  | 'encode'
  | 'time'
  | 'visualize'

export type ToolCategoryDefinition = {
  slug: ToolCategorySlug
  title: string
  icon: string
  scope: 'preview' | 'utility' | 'conversion' | 'mixed'
  eyebrow: string
  description: string
  seoTitle: string
  seoDescription: string
}

export type ToolCatalogEntry = {
  kind: 'preview' | 'conversion' | 'utility'
  slug: string
  title: string
  shortTitle: string
  description: string
  icon: string
  category: string
  keywords: string[]
  meta: string
  to: RouteLocationRaw
}

export const toolCategoryDefinitions: ToolCategoryDefinition[] = [
  {
    slug: 'docs',
    title: 'Docs Tools',
    icon: 'file-text',
    scope: 'preview',
    eyebrow: 'Documentation',
    description: 'Tools for markdown articles, diffs, and shareable documentation workflows.',
    seoTitle: 'Pastefy Docs Tools',
    seoDescription:
      'Explore Pastefy documentation tools for Markdown articles, diffs, and readable share modes built for developer docs.',
  },
  {
    slug: 'data',
    title: 'Data Tools',
    icon: 'braces',
    scope: 'preview',
    eyebrow: 'Structured Data',
    description: 'Inspect JSON, YAML, XML, CSV, and other structured formats with richer previews.',
    seoTitle: 'Pastefy Data Tools',
    seoDescription:
      'Use Pastefy data tools to inspect and preview JSON, YAML, XML, CSV, and other structured developer formats.',
  },
  {
    slug: 'visual',
    title: 'Visual Tools',
    icon: 'sparkles',
    scope: 'preview',
    eyebrow: 'Visual Preview',
    description: 'Preview HTML, SVG, Mermaid, and spatial formats in a more visual editing workflow.',
    seoTitle: 'Pastefy Visual Preview Tools',
    seoDescription:
      'Use Pastefy visual tools to preview HTML, SVG, Mermaid diagrams, GeoJSON maps, and other visual formats.',
  },
  {
    slug: 'mermaid',
    title: 'Mermaid Tools',
    icon: 'chart-arrows-vertical',
    scope: 'mixed',
    eyebrow: 'Mermaid',
    description:
      'Find Mermaid-focused editors, theme builders, exporters, and diagram generators in one place.',
    seoTitle: 'Pastefy Mermaid Tools',
    seoDescription:
      'Use Pastefy Mermaid tools to edit diagrams, build themes, export images, and generate Mermaid charts, ER diagrams, class diagrams, and infra views.',
  },
  {
    slug: 'api',
    title: 'API Tools',
    icon: 'api',
    scope: 'preview',
    eyebrow: 'API Workflow',
    description: 'Work with HTTP requests, payloads, and API-facing developer content in focused tools.',
    seoTitle: 'Pastefy API Tools',
    seoDescription:
      'Use Pastefy API tools to inspect HTTP requests, payload formats, and API-oriented developer snippets before sharing.',
  },
  {
    slug: 'infra',
    title: 'Infrastructure Tools',
    icon: 'building-warehouse',
    scope: 'preview',
    eyebrow: 'Infrastructure',
    description: 'Preview Terraform, config files, and infrastructure-focused snippets in a cleaner workspace.',
    seoTitle: 'Pastefy Infrastructure Tools',
    seoDescription:
      'Use Pastefy infrastructure tools for Terraform, environment files, configs, and infrastructure-oriented workflows.',
  },
  {
    slug: 'operations',
    title: 'Operations Tools',
    icon: 'activity-heartbeat',
    scope: 'mixed',
    eyebrow: 'Operations',
    description:
      'Inspect logs, alerting rules, and production-facing operational data with tools built for day-two work.',
    seoTitle: 'Pastefy Operations Tools',
    seoDescription:
      'Use Pastefy operations tools to inspect logs, alert rules, and production-focused operational content and diagnostics.',
  },
  {
    slug: 'server',
    title: 'Server Config Tools',
    icon: 'server',
    scope: 'utility',
    eyebrow: 'Server Config',
    description:
      'Generate deploy-ready Caddy, NGINX, and Apache website configs with the common options you usually need.',
    seoTitle: 'Pastefy Server Config Tools',
    seoDescription:
      'Use Pastefy server config tools to generate Caddy, NGINX, and Apache configs for static sites and reverse proxies.',
  },
  {
    slug: 'deploy',
    title: 'Deployment Tools',
    icon: 'rocket',
    scope: 'utility',
    eyebrow: 'Deployment',
    description:
      'Generate Dockerfiles, Compose stacks, startup scripts, and systemd units for common deployment workflows.',
    seoTitle: 'Pastefy Deployment Tools',
    seoDescription:
      'Use Pastefy deployment tools to generate Dockerfiles, Compose files, startup scripts, and systemd services.',
  },
  {
    slug: 'automation',
    title: 'Automation Tools',
    icon: 'clock-code',
    scope: 'mixed',
    eyebrow: 'Automation',
    description: 'Build around workflows, schedules, GitHub Actions, and automation-ready developer content.',
    seoTitle: 'Pastefy Automation Tools',
    seoDescription:
      'Use Pastefy automation tools for cron schedules, GitHub Actions, Ansible, and automation-focused developer workflows.',
  },
  {
    slug: 'security',
    title: 'Security Tools',
    icon: 'shield-lock',
    scope: 'mixed',
    eyebrow: 'Security',
