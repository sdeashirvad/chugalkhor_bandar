import { Link } from 'react-router-dom'
import { Card, CardContent } from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'
import type { RelationshipSummary } from '@/types/api'

interface RelationshipCardProps {
  relationship: RelationshipSummary
}

export function RelationshipCard({ relationship }: RelationshipCardProps) {
  const targetName = relationship.targetCharacter?.name ?? relationship.title

  const card = (
    <Card className="transition-colors hover:bg-muted/40">
      <CardContent className="space-y-2 p-4">
        <div className="flex items-center justify-between gap-2">
          <Badge variant="outline">{relationship.relationshipType ?? 'Relationship'}</Badge>
          {relationship.status ? (
            <span className="text-xs text-muted-foreground">{relationship.status}</span>
          ) : null}
        </div>
        <p className="text-sm font-medium">{targetName}</p>
        {relationship.title && relationship.targetCharacter ? (
          <p className="text-xs text-muted-foreground">{relationship.title}</p>
        ) : null}
      </CardContent>
    </Card>
  )

  if (!relationship.targetCharacter?.id) {
    return card
  }

  return (
    <Link to={`/characters/${relationship.targetCharacter.id}`} className="block">
      {card}
    </Link>
  )
}
