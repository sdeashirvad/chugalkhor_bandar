import { formatRelativeTime } from '@/lib/relativeTime'

interface LastSeenBadgeProps {
  lastSeenAt: string | null
  lastTopic?: string | null
}

export function LastSeenBadge({ lastSeenAt, lastTopic }: LastSeenBadgeProps) {
  const relative = formatRelativeTime(lastSeenAt)

  return (
    <div className="rounded-lg border border-border/60 bg-muted/40 px-3 py-2 text-xs text-muted-foreground">
      <p className="font-medium text-foreground">Last Monkey Talk</p>
      {relative ? <p className="mt-1">Last seen: {relative}</p> : <p className="mt-1">Not seen in the Jungle yet</p>}
      {lastTopic ? <p className="mt-1 italic">Last heard discussing {lastTopic}</p> : null}
    </div>
  )
}
