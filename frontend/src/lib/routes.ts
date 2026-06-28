import type { EntityReference } from '@/types/api'

export function entityPath(entity: Pick<EntityReference, 'type' | 'id'>): string {
  switch (entity.type) {
    case 'character':
      return `/characters/${entity.id}`
    case 'story':
      return `/stories/${entity.id}`
    case 'territory':
      return `/territories/${entity.id}`
    case 'place':
      return `/places/${entity.id}`
    case 'organization':
      return `/organizations/${entity.id}`
    default:
      return '/world'
  }
}
