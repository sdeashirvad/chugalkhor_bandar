import { useMemo } from 'react'
import { useNavigate } from 'react-router-dom'
import { FEATURED_CHARACTER_IDS } from '@/config/world'
import { ResidentCard } from '@/components/world/ResidentCard'
import { ErrorState } from '@/components/ErrorState'
import { LoadingSpinner } from '@/components/LoadingSpinner'
import { PageHeader } from '@/components/PageHeader'
import { SearchBox } from '@/components/SearchBox'
import { useCharacters } from '@/hooks/useCharacters'
import { useSession } from '@/hooks/useSession'
import { useWorkingMemory } from '@/hooks/useWorkingMemory'
import { useWorldEvents } from '@/hooks/useLivingWorld'
import { useState } from 'react'

export function CharactersPage() {
  const [search, setSearch] = useState('')
  const navigate = useNavigate()
  const { data: session } = useSession()
  const { data, isLoading, isError, error } = useCharacters()
  const workingMemory = useWorkingMemory()
  const worldEvents = useWorldEvents()

  const activityByCharacter = useMemo(() => {
    const map = new Map<string, string>()
    for (const event of worldEvents.data ?? []) {
      if (event.type !== 'CHARACTER_ACTIVITY') continue
      for (const participant of event.participants) {
        if (!map.has(participant)) map.set(participant, event.summary)
      }
    }
    return map
  }, [worldEvents.data])

  const filtered = useMemo(() => {
    if (!data) return []
    const query = search.trim().toLowerCase()
    const sorted = [...data].sort((a, b) => a.name.localeCompare(b.name, undefined, { sensitivity: 'base' }))
    if (!query) return sorted
    return sorted.filter(
      (character) =>
        character.name.toLowerCase().includes(query) ||
        character.species.toLowerCase().includes(query) ||
        character.titles.some((title) => title.toLowerCase().includes(query)),
    )
  }, [data, search])

  const featured = filtered.filter((c) => (FEATURED_CHARACTER_IDS as readonly string[]).includes(c.id))
  const others = filtered.filter((c) => !(FEATURED_CHARACTER_IDS as readonly string[]).includes(c.id))

  if (isLoading) {
    return <LoadingSpinner label="Searching the palace archives…" />
  }

  if (isError) {
    return <ErrorState error={error} title="Bandar could not find the residents" />
  }

  return (
    <div className="space-y-6">
      <PageHeader
        title="Residents of the Jungle"
        description="Those who live, rule, and wander here."
      />
      <SearchBox value={search} onChange={setSearch} placeholder="Search residents…" />

      {featured.length > 0 ? (
        <section className="space-y-4">
          <h2 className="font-display text-lg font-semibold">Active Residents</h2>
          <div className="grid gap-4 lg:grid-cols-2 xl:grid-cols-3">
            {featured.map((character) => (
              <ResidentCard
                key={character.id}
                id={character.id}
                name={character.name}
                titles={character.titles}
                currentPlaceName={character.currentPlaceName}
                lastSeenAt={character.lastSeenAt}
                featured
                isCurrentUser={session?.currentCharacter.id === character.id}
                activityHint={activityByCharacter.get(character.id) ?? null}
                lastTopic={
                  session?.currentCharacter.id === character.id ? workingMemory.data?.activeTopic ?? null : null
                }
                onClick={() => navigate(`/characters/${character.id}`)}
              />
            ))}
          </div>
        </section>
      ) : null}

      {others.length > 0 ? (
        <section className="space-y-4">
          <h2 className="font-display text-lg font-semibold text-muted-foreground">Other Residents</h2>
          <div className="grid gap-3 sm:grid-cols-2 lg:grid-cols-3">
            {others.map((character) => (
              <ResidentCard
                key={character.id}
                id={character.id}
                name={character.name}
                titles={character.titles}
                currentPlaceName={character.currentPlaceName}
                lastSeenAt={character.lastSeenAt}
                activityHint={activityByCharacter.get(character.id) ?? null}
                onClick={() => navigate(`/characters/${character.id}`)}
              />
            ))}
          </div>
        </section>
      ) : null}
    </div>
  )
}
