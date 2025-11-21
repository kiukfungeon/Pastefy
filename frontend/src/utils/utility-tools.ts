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
