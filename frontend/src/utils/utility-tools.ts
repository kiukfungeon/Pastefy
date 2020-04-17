export type UtilityToolDefinition = {
  slug: string
  title: string
  shortTitle: string
  description: string
  icon: string
  category:
    | 'Inspect'
    | 'Generate'
    | 'Security'
    | 'Automation'
    | 'Encode'
    | 'Time'
    | 'Visualize'
    | 'Operations'
    | 'Server'
    | 'Deploy'
  keywords: string[]
}

export const utilityTools: UtilityToolDefinition[] = [
  {
    slug: 'data-to-mermaid',
    title: 'Data to Mermaid Charts',
    shortTitle: 'Mermaid Charts',
    description:
      'Turn JSON, YAML, or CSV datasets into Mermaid pie, bar, line, grouped, and combo charts with field mapping and aggregation.',
    icon: 'chart-bar',
    category: 'Visualize',
    keywords: ['mermaid', 'chart', 'csv', 'json', 'yaml', 'visualize', 'diagram'],
  },
  {
    slug: 'mermaid-theme-builder',
    title: 'Mermaid Theme Builder',
    shortTitle: 'Theme Builder',
    description:
      'Tune Mermaid colors, fonts, spacing, and diagram styling, then export the themed config plus diagram.',
    icon: 'palette',
    category: 'Visualize',
    keywords: ['mermaid', 'theme', 'diagram', 'colors', 'styling'],
  },
  {
    slug: 'data-to-mermaid-er',
    title: 'Data to Mermaid ER Diagram',
    shortTitle: 'Data -> ER',
    description:
      'Infer Mermaid ER diagrams from JSON, YAML, or CSV schema-like data structures.',
    icon: 'chart-arcs-3',
    category: 'Visualize',
    keywords: ['mermaid', 'er diagram', 'json schema', 'yaml schema', 'csv'],
  },
  {
    slug: 'sql-to-mermaid-er',
    title: 'SQL to Mermaid ER',
    shortTitle: 'SQL -> ER',
    description:
      'Parse CREATE TABLE statements into Mermaid ER diagrams with tables and foreign keys.',
    icon: 'database',
    category: 'Visualize',
    keywords: ['sql', 'er diagram', 'mermaid', 'database', 'schema'],
  },
  {
    slug: 'infra-to-mermaid',
    title: 'Infra to Mermaid',
    shortTitle: 'Infra',
    description:
      'Convert Compose, Kubernetes, Terraform, and GitHub Actions definitions into Mermaid architecture flowcharts.',
    icon: 'hierarchy-3',
    category: 'Visualize',
    keywords: ['infrastructure', 'mermaid', 'docker compose', 'kubernetes', 'terraform', 'github actions'],
  },
  {
    slug: 'markdown-to-mermaid',
    title: 'Markdown to Mermaid Mindmap / Flow',
    shortTitle: 'Markdown -> Mermaid',
    description:
      'Turn Markdown headings and bullet outlines into Mermaid mindmaps or flowcharts.',
    icon: 'markdown',
    category: 'Visualize',
    keywords: ['markdown', 'mermaid', 'mindmap', 'flowchart', 'outline'],
  },
  {
    slug: 'data-to-class-diagram',
    title: 'JSON / YAML to Class Diagram',
    shortTitle: 'Data -> Class',
    description:
      'Map nested structured data to Mermaid class diagrams with inferred fields and relationships.',
    icon: 'binary-tree-2',
    category: 'Visualize',
    keywords: ['json', 'yaml', 'class diagram', 'mermaid', 'schema'],
  },
  {
    slug: 'mermaid-image-export',
    title: 'Mermaid Image Export Tool',
    shortTitle: 'Mermaid Export',
    description:
      'Export Mermaid diagrams as PNG or SVG with padding, scale, background, and watermark options.',
    icon: 'photo-down',
    category: 'Visualize',
    keywords: ['mermaid', 'png', 'svg', 'export', 'image'],
  },
  {
    slug: 'diff-lab',
    title: 'Diff Lab',
    shortTitle: 'Diff',
    description:
      'Compare two textareas, inspect a rendered diff, and export the unified patch.',
    icon: 'git-compare',
    category: 'Inspect',
    keywords: ['diff', 'compare', 'patch', 'unified diff', 'changes'],
  },
  {
    slug: 'jwt-inspector',
    title: 'JWT Inspector',
    shortTitle: 'JWT',
    description:
      'Decode JWTs, inspect claims, and check timestamps without sending the token anywhere.',
    icon: 'shield-lock',
    category: 'Security',
    keywords: ['jwt', 'token', 'claims', 'auth', 'bearer'],
  },
  {
    slug: 'hash-generator',
    title: 'Hash Generator',
    shortTitle: 'Hash',
    description:
      'Generate SHA hashes for text or payloads and compare lengths at a glance.',
    icon: 'fingerprint',
    category: 'Security',
    keywords: ['hash', 'sha256', 'checksum', 'digest'],
  },
  {
    slug: 'secret-generator',
    title: 'Secret Generator',
    shortTitle: 'Secrets',
    description:
      'Generate UUIDs, hex tokens, and Base64URL secrets for quick local testing and setup.',
    icon: 'sparkles',
    category: 'Generate',
    keywords: ['uuid', 'secret', 'token', 'generator', 'base64url'],
  },
  {
    slug: 'password-generator',
    title: 'Password Generator',
    shortTitle: 'Passwords',
    description:
      'Generate passwords with configurable character sets, length, count, and ambiguity rules.',
    icon: 'key',
    category: 'Security',
    keywords: ['password', 'secret', 'generator', 'security'],
  },
  {
    slug: 'cron-explainer',
    title: 'Cron Explainer',
    shortTitle: 'Cron',
    description:
      'Understand cron expressions with a readable breakdown of schedule fields and timing.',
    icon: 'clock-code',
    category: 'Automation',
    keywords: ['cron', 'schedule', 'automation', 'timing'],
  },
  {
    slug: 'base64-lab',
    title: 'Base64 Lab',
    shortTitle: 'Base64',
    description:
      'Encode and decode Base64 or Base64URL strings locally for payload and token workflows.',
    icon: 'binary',
    category: 'Encode',
    keywords: ['base64', 'base64url', 'encode', 'decode'],
  },
  {
    slug: 'url-encoder',
    title: 'URL Encoder / Decoder',
    shortTitle: 'URL Encode',
    description:
      'Encode and decode URL components and full query values without leaving the browser.',
    icon: 'link',
    category: 'Encode',
    keywords: ['url', 'encode', 'decode', 'querystring'],
  },
  {
    slug: 'timestamp-converter',
    title: 'Timestamp Converter',
    shortTitle: 'Timestamp',
    description:
      'Convert between Unix timestamps, ISO dates, and local times for debugging and docs.',
    icon: 'clock-hour-4',
    category: 'Time',
    keywords: ['timestamp', 'unix', 'iso', 'date', 'time'],
  },
  {
    slug: 'slug-generator',
    title: 'Slug Generator',
    shortTitle: 'Slug',
    description:
      'Generate slugs, snake_case, and other URL-friendly variants from plain text.',
    icon: 'writing',
    category: 'Generate',
    keywords: ['slug', 'kebab-case', 'snake_case', 'url'],
  },
  {
    slug: 'case-converter',
