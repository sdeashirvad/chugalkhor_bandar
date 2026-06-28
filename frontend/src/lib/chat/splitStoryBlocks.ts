export function splitStoryBlocks(content: string): string[] {
  return content
    .replace(/\r\n/g, '\n')
    .split(/\n\s*\n/)
    .map((block) => block.trim())
    .filter(Boolean)
}

export function isMarkdownListBlock(block: string): boolean {
  return /^([*+-]|\d+\.)\s+/m.test(block)
}

export function isNarrationBlock(block: string): boolean {
  const trimmed = block.trim()
  if (/^\([^)]+\)$/.test(trimmed)) return true
  if (/^\*[^*\n]+\*$/.test(trimmed)) return true
  if (/^_[^_\n]+_$/.test(trimmed)) return true
  if (/^(Bandar|He|She)\s+[a-z].*\.$/i.test(trimmed) && trimmed.length < 120) return true
  return false
}

export function stripNarrationMarkers(text: string): string {
  return text
    .replace(/^\((.+)\)$/, '$1')
    .replace(/^\*(.+)\*$/, '$1')
    .replace(/^_(.+)_$/, '$1')
}

export function splitInlineNarration(text: string): Array<{ type: 'text' | 'narration'; value: string }> {
  const parts = text.split(/(\([^)]+\))/g).filter(Boolean)
  return parts.map((part) =>
    /^\([^)]+\)$/.test(part) ? { type: 'narration', value: part } : { type: 'text', value: part },
  )
}
