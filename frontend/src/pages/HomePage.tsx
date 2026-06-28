import { Link } from 'react-router-dom'
import { Bell, MapPin, ScrollText, Sparkles } from 'lucide-react'
import { HomeBlock } from '@/components/world/WorldCard'
import { ErrorState } from '@/components/ErrorState'
import { LoadingSpinner } from '@/components/LoadingSpinner'
import { PageHeader } from '@/components/PageHeader'
import { getCharacterAvatar } from '@/lib/avatars'
import { useHomeSummary } from '@/hooks/useHomeSummary'

export function HomePage() {
  const summary = useHomeSummary()

  if (summary.isLoading) {
    return <LoadingSpinner label="Bandar is checking the Jungle…" />
  }

  if (summary.isError) {
    return <ErrorState error={summary.error} title="The Jungle is having a quiet moment" />
  }

  const { session, character, workingMemory, lastBandarLine, unreadCount, activeArtifacts, recentEvents, latestChronicle, worldDate } =
    summary.data!

  return (
    <div className="space-y-6">
      <PageHeader
        title="Home"
        description="You are here. The Jungle is alive."
      />

      <HomeBlock label="Where you are now">
        <div className="flex items-center gap-4">
          <img
            src={getCharacterAvatar(session.currentCharacter.id)}
            alt=""
            className="h-16 w-16 rounded-full border-2 border-jungle-gold/30 object-cover"
          />
          <div>
            <p className="font-display text-xl font-semibold">{session.currentCharacter.displayName}</p>
            {session.currentCharacter.currentLocation ? (
              <p className="mt-1 flex items-center gap-1 text-sm text-muted-foreground">
                <MapPin className="h-3.5 w-3.5" aria-hidden />
                {session.currentCharacter.currentLocation}
              </p>
            ) : character?.currentPlaceName ? (
              <p className="mt-1 flex items-center gap-1 text-sm text-muted-foreground">
                <MapPin className="h-3.5 w-3.5" aria-hidden />
                {character.currentPlaceName}
              </p>
            ) : null}
            {session.currentCharacter.homeTerritory ? (
              <p className="mt-1 text-xs text-muted-foreground">Realm of {session.currentCharacter.homeTerritory}</p>
            ) : null}
          </div>
        </div>
      </HomeBlock>

      <div className="grid gap-4 lg:grid-cols-2">
        <HomeBlock label="What Bandar last noticed">
          {workingMemory?.activeTopic ? (
            <p className="text-sm">{workingMemory.activeTopic}</p>
          ) : lastBandarLine ? (
            <p className="text-sm italic text-muted-foreground">"{lastBandarLine}"</p>
          ) : (
            <p className="text-sm text-muted-foreground">Bandar has not heard anything new yet.</p>
          )}
          <Link to="/chat" className="mt-3 inline-block text-sm font-medium text-jungle-moss hover:underline">
            Speak with Bandar →
          </Link>
        </HomeBlock>

        <HomeBlock label="Letters waiting">
          {unreadCount > 0 ? (
            <>
              <p className="text-sm">Bandar has {unreadCount} invitation{unreadCount === 1 ? '' : 's'} for you.</p>
              <Link
                to="/notifications"
                className="mt-3 inline-flex h-9 items-center rounded-md border border-border bg-card px-4 text-sm font-medium hover:bg-accent"
              >
                <Bell className="mr-1 h-3.5 w-3.5" aria-hidden />
                Open Letters
              </Link>
            </>
          ) : (
            <p className="text-sm text-muted-foreground">Nothing is unread at the moment.</p>
          )}
        </HomeBlock>
      </div>

      <HomeBlock label="What the Jungle is doing">
        {worldDate ? <p className="mb-3 text-xs text-muted-foreground">World date: {worldDate}</p> : null}
        {recentEvents.length > 0 ? (
          <ul className="space-y-2 text-sm">
            {recentEvents.slice(0, 4).map((event) => (
              <li key={event.id} className="flex items-start gap-2">
                <Sparkles className="mt-0.5 h-3.5 w-3.5 shrink-0 text-jungle-gold" aria-hidden />
                <span>
                  <span className="font-medium">{event.title}</span>
                  <span className="text-muted-foreground"> — {event.summary}</span>
                </span>
              </li>
            ))}
          </ul>
        ) : (
          <p className="text-sm text-muted-foreground">The Jungle is quiet for now.</p>
        )}
        <Link to="/living-world" className="mt-3 inline-block text-sm font-medium text-jungle-moss hover:underline">
          See the Living World →
        </Link>
      </HomeBlock>

      <div className="grid gap-4 lg:grid-cols-2">
        <HomeBlock label="Unfinished matters">
          {activeArtifacts > 0 ? (
            <>
              <p className="text-sm">{activeArtifacts} open intention{activeArtifacts === 1 ? '' : 's'} await you.</p>
              <Link to="/artifacts" className="mt-3 inline-block text-sm font-medium text-jungle-moss hover:underline">
                View unfinished matters →
              </Link>
            </>
          ) : (
            <p className="text-sm text-muted-foreground">No promises or story seeds are waiting.</p>
          )}
        </HomeBlock>

        <HomeBlock label="Recent history">
          {latestChronicle ? (
            <>
              <p className="font-display font-medium">{latestChronicle.title}</p>
              <p className="mt-1 text-sm text-muted-foreground">{latestChronicle.summary}</p>
              <Link to="/chronicles" className="mt-3 inline-flex items-center gap-1 text-sm font-medium text-jungle-moss hover:underline">
                <ScrollText className="h-3.5 w-3.5" aria-hidden />
                Browse Chronicles →
              </Link>
            </>
          ) : (
            <p className="text-sm text-muted-foreground">The archives are still being written.</p>
          )}
        </HomeBlock>
      </div>
    </div>
  )
}
