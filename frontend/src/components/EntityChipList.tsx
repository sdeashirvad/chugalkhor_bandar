import { Link } from 'react-router-dom'
import { Badge } from '@/components/ui/badge'
import { entityPath } from '@/lib/routes'
import type { EntityReference } from '@/types/api'

interface EntityChipListProps {
  items: EntityReference[]
  emptyLabel?: string
}

export function EntityChipList({ items, emptyLabel = 'None listed' }: EntityChipListProps) {
  if (items.length === 0) {
    return <p className="text-sm text-muted-foreground">{emptyLabel}</p>
  }

  return (
    <div className="flex flex-wrap gap-2">
      {items.map((item) => (
        <Link key={`${item.type}-${item.id}`} to={entityPath(item)}>
          <Badge variant="outline" className="cursor-pointer hover:bg-accent">
            {item.name}
          </Badge>
        </Link>
      ))}
    </div>
  )
}
