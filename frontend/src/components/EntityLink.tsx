import { Link } from 'react-router-dom'
import { entityPath } from '@/lib/routes'
import { cn } from '@/lib/utils'
import type { EntityReference } from '@/types/api'

interface EntityLinkProps {
  entity: EntityReference | null | undefined
  className?: string
  fallback?: string
}

export function EntityLink({ entity, className, fallback = '—' }: EntityLinkProps) {
  if (!entity) {
    return <span className={cn('text-muted-foreground', className)}>{fallback}</span>
  }

  return (
    <Link
      to={entityPath(entity)}
      className={cn('font-medium text-foreground hover:underline', className)}
    >
      {entity.name}
    </Link>
  )
}
