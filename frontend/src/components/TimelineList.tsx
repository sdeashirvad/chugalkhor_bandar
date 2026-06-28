interface TimelineListProps {
  items: string[]
}

export function TimelineList({ items }: TimelineListProps) {
  if (items.length === 0) {
    return <p className="text-sm text-muted-foreground">No timeline entries.</p>
  }

  return (
    <ol className="space-y-4 border-l border-border pl-4">
      {items.map((item, index) => (
        <li key={`${index}-${item.slice(0, 24)}`} className="relative">
          <span className="absolute -left-[1.3rem] top-1.5 h-2 w-2 rounded-full bg-zinc-400" />
          <p className="whitespace-pre-wrap text-sm leading-relaxed">{item}</p>
        </li>
      ))}
    </ol>
  )
}

export function splitTimelineEntries(text: string): string[] {
  if (!text.trim()) return []
  return text
    .split(/\n---\n|\n### /)
    .map((entry) => entry.trim())
    .filter(Boolean)
}
