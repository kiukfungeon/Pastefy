import { hljs } from '@/utils/highlight.ts'
import '@/utils/highlight-imports.ts'

export function estimateTitle(contents: string) {
  let matched = contents.match(/^#(#?)(#?)(#?)(#?) ([^\n]*)(\n.*)?/s)
  if (matched !== null) {
    return `${matched[5]}.md`
  }

  // Match HTML
  if (contents.startsWith('<!DOCTYPE html>')) {
    return `index.html`
  }
  if (contents.includes('<div>')) {
    return `file.html`
  }

  if (contents.startsWith('<template>')) {
    return `Component.vue`
  }
  // Match Python
  if (contents.includes('if __name__ == "__main__":')) {
    return `main.py`
  }

  // Match java
  matched = contents.match(
    /^\s?(package) .*;.*(public|private)\s(class|@?interface|abstract class|enum)\s([a-zA-Z-0-9]*)\s?.*{.*/s,
  )
  if (matched !== null) {
    return `${matched[4]}.java`
  }

  // Match PHP
  matched = contents.match(
    /^\s?<\?php.*\n(class|interface|abstract class|enum|trait)\s([a-zA-Z-0-9]*)\s?.*{.*/s,
  )
  if (matched !== null) {
    return `${matched[2]}.php`
  }
  // Match PHP
  if (contents.includes('<?php')) {
    return `index.php`
  }

  // Match CSharp
  matched = contents.match(
    /\s?namespace\s.*{.*\s(class|@?interface|abstract class|enum)\s([a-zA-Z-0-9]*)\s?.*{.*/s,
  )
  if (matched !== null) {
    return `${matched[2]}.cs`
  }
  // Match Javascript
  matched = contents.match(/.*export\sdefault\s(class|function)\s([a-zA-Z-0-9]*)\s?{/s)
  if (matched !== null) {
    return `${matched[2]}.js`
