export type PreviewToolDefinition = {
  slug: string
  title: string
  shortTitle: string
  description: string
  fileName: string
  type: string
  icon: string
  category:
    | 'Docs'
    | 'Data'
    | 'Visual'
    | 'API'
    | 'Infra'
    | 'Operations'
    | 'Automation'
    | 'Security'
    | 'Terminal'
  keywords: string[]
  example: string
}

export const previewTools: PreviewToolDefinition[] = [
  {
    slug: 'markdown-editor',
    title: 'Markdown Editor & Article Preview',
    shortTitle: 'Markdown',
    description:
      'Write Markdown on the left and see the rendered article preview on the right before you decide to publish it.',
    fileName: 'article.md',
    type: 'markdown',
    icon: 'file-text',
    category: 'Docs',
    keywords: ['markdown', 'article', 'reader mode', 'documentation'],
    example: `# Pastefy Tooling

Build a polished article before you publish it.

## Why this is useful
- Draft docs with a clean article preview
- Share snippets as readable content
- Paste only when you are ready

\`\`\`ts
console.log('hello from pastefy')
\`\`\`
`,
  },
  {
    slug: 'json-viewer',
    title: 'JSON Viewer & Formatter',
    shortTitle: 'JSON',
    description:
      'Inspect, format, and preview JSON structures with a live editor and the richer JSON tools side by side.',
    fileName: 'payload.json',
    type: 'json',
    icon: 'braces',
    category: 'Data',
    keywords: ['json', 'viewer', 'formatter', 'api payload'],
    example: `{
  "service": "pastefy",
  "environment": "staging",
  "features": {
    "readerMode": true,
    "presentationMode": true
  },
  "users": [
    { "id": 1, "name": "Ada", "role": "admin" },
    { "id": 2, "name": "Linus", "role": "user" }
  ]
}
`,
  },
  {
    slug: 'html-preview',
    title: 'HTML Preview Sandbox',
    shortTitle: 'HTML',
    description:
      'Prototype HTML snippets in a lightweight split view and render them in the built-in sandbox before sharing.',
    fileName: 'index.html',
    type: 'html',
    icon: 'world-code',
    category: 'Visual',
    keywords: ['html', 'preview', 'sandbox', 'component playground'],
    example: `<!DOCTYPE html>
<html>
  <head>
    <meta charset="UTF-8" />
    <title>Pastefy HTML Preview</title>
    <style>
      body { font-family: sans-serif; padding: 2rem; background: linear-gradient(135deg, #f7f3ff, #eef8ff); }
      .card { background: white; padding: 1.5rem; border-radius: 18px; box-shadow: 0 18px 40px rgba(15, 23, 42, 0.12); }
    </style>
  </head>
  <body>
    <article class="card">
      <h1>Hello from Pastefy</h1>
      <p>This should render as a mini webpage.</p>
    </article>
  </body>
</html>
`,
  },
  {
    slug: 'xml-viewer',
    title: 'XML Viewer & Inspector',
    shortTitle: 'XML',
    description:
      'Load XML, inspect nested nodes, and validate the rendered structure in a tool page built for config and document files.',
    fileName: 'pom.xml',
    type: 'xml',
    icon: 'file-code',
    category: 'Data',
    keywords: ['xml', 'inspector', 'pom', 'config'],
    example: `<project>
  <modelVersion>4.0.0</modelVersion>
  <groupId>app.pastefy</groupId>
  <artifactId>pastefy-demo</artifactId>
  <version>1.0.0</version>

  <properties>
    <java.version>21</java.version>
  </properties>

  <dependencies>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
  </dependencies>
</project>
`,
  },
  {
    slug: 'yaml-viewer',
    title: 'YAML Viewer & Tree Explorer',
    shortTitle: 'YAML',
    description:
      'Explore YAML like a structured document with collapsible sections, a live editor, and an instant preview.',
    fileName: 'docker-compose.yml',
    type: 'yaml',
    icon: 'list-tree',
    category: 'Data',
    keywords: ['yaml', 'viewer', 'tree', 'compose'],
    example: `version: "3.9"

services:
  web:
    image: pastefy/web:latest
    ports:
      - "8080:8080"
    environment:
      APP_ENV: production
      LOG_LEVEL: info

  redis:
    image: redis:7
    restart: unless-stopped
`,
  },
  {
    slug: 'csv-viewer',
    title: 'CSV Table Preview',
    shortTitle: 'CSV',
    description:
      'Preview CSV data in a readable table layout and tweak the source without needing to create a paste first.',
    fileName: 'report.csv',
    type: 'csv',
    icon: 'table',
    category: 'Data',
    keywords: ['csv', 'table', 'spreadsheet'],
    example: `name,role,team
Ada,admin,platform
Linus,user,backend
Grace,user,frontend
`,
  },
  {
    slug: 'mermaid-live-editor',
    title: 'Mermaid Diagram Live Preview',
    shortTitle: 'Mermaid',
    description:
      'Edit Mermaid diagrams with a live visual render so architecture and flowchart snippets are share-ready faster.',
    fileName: 'architecture.mermaid',
    type: 'mermaid',
    icon: 'chart-arrows-vertical',
    category: 'Visual',
    keywords: ['mermaid', 'diagram', 'flowchart', 'architecture'],
    example: `graph TD
  User --> Frontend
  Frontend --> Backend
  Backend --> Postgres
  Backend --> Redis
`,
  },
  {
    slug: 'geojson-viewer',
    title: 'GeoJSON Map Preview',
    shortTitle: 'GeoJSON',
    description:
      'Preview spatial data with a map-first experience while editing raw GeoJSON side by side.',
    fileName: 'map.geojson',
    type: 'geojson',
    icon: 'map-2',
    category: 'Visual',
    keywords: ['geojson', 'map', 'location data'],
    example: `{
  "type": "FeatureCollection",
  "features": [
    {
      "type": "Feature",
      "properties": {
        "name": "Berlin"
      },
      "geometry": {
        "type": "Point",
        "coordinates": [13.4050, 52.5200]
      }
    }
  ]
}
`,
  },
  {
    slug: 'svg-viewer',
    title: 'SVG Viewer',
    shortTitle: 'SVG',
    description:
      'Test SVG snippets in a clean split layout and see the resulting vector preview immediately.',
    fileName: 'diagram.svg',
    type: 'xml',
    icon: 'vector',
    category: 'Visual',
    keywords: ['svg', 'vector', 'icon', 'diagram'],
    example: `<svg width="300" height="120" xmlns="http://www.w3.org/2000/svg">
  <rect x="10" y="10" width="280" height="100" rx="16" fill="#111827"/>
  <text x="150" y="68" font-size="24" text-anchor="middle" fill="#ffffff">Pastefy SVG</text>
</svg>
`,
  },
  {
    slug: 'diff-viewer',
    title: 'Diff Viewer',
    shortTitle: 'Diff',
