import { Badge } from '@/components/ui/badge'

interface BadgeListProps {
  items: string[]
  emptyLabel?: string
}

export function BadgeList({ items, emptyLabel = 'None' }: BadgeListProps) {
  if (items.length === 0) {
    return <span className="text-sm text-muted-foreground">{emptyLabel}</span>
  }

  return (
    <div className="flex flex-wrap gap-2">
      {items.map((item) => (
        <Badge key={item} variant="outline">
          {item}
        </Badge>
      ))}
    </div>
  )
}
