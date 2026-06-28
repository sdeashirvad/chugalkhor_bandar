import { useMemo } from 'react'
import { useCharacters } from '@/hooks/useCharacters'
import { useOrganizations } from '@/hooks/useOrganizations'
import { usePlaces } from '@/hooks/usePlaces'
import { useStories } from '@/hooks/useStories'
import { useTerritories } from '@/hooks/useTerritories'
import type { SearchResult } from '@/types/api'

export function useWorldSearch(query: string) {
  const characters = useCharacters()
  const stories = useStories()
  const territories = useTerritories()
  const places = usePlaces()
  const organizations = useOrganizations()

  const isLoading =
    characters.isLoading ||
    stories.isLoading ||
    territories.isLoading ||
    places.isLoading ||
    organizations.isLoading

  const results = useMemo(() => {
    const normalized = query.trim().toLowerCase()
    if (!normalized) return [] as SearchResult[]

    const matches: SearchResult[] = []

    characters.data?.forEach((character) => {
      if (
        character.name.toLowerCase().includes(normalized) ||
        character.species.toLowerCase().includes(normalized)
      ) {
        matches.push({
          id: character.id,
          name: character.name,
          type: 'character',
          subtitle: character.species,
        })
      }
    })

    stories.data?.forEach((story) => {
      if (story.title.toLowerCase().includes(normalized) || story.summary.toLowerCase().includes(normalized)) {
        matches.push({ id: story.id, name: story.title, type: 'story', subtitle: story.era })
      }
    })

    territories.data?.forEach((territory) => {
      if (territory.name.toLowerCase().includes(normalized)) {
        matches.push({
          id: territory.id,
          name: territory.name,
          type: 'territory',
          subtitle: territory.ruler?.name,
        })
      }
    })

    places.data?.forEach((place) => {
      if (place.name.toLowerCase().includes(normalized) || place.type.toLowerCase().includes(normalized)) {
        matches.push({
          id: place.id,
          name: place.name,
          type: 'place',
          subtitle: place.territory?.name,
        })
      }
    })

    organizations.data?.forEach((organization) => {
      if (
        organization.name.toLowerCase().includes(normalized) ||
        organization.type.toLowerCase().includes(normalized)
      ) {
        matches.push({
          id: organization.id,
          name: organization.name,
          type: 'organization',
          subtitle: organization.leader?.name,
        })
      }
    })

    return matches.sort((a, b) => a.name.localeCompare(b.name))
  }, [
    query,
    characters.data,
    stories.data,
    territories.data,
    places.data,
    organizations.data,
  ])

  return { results, isLoading }
}
