import { ErrorState } from '@/components/ErrorState'
import { EmptyState } from '@/components/EmptyState'
import { PageHeader } from '@/components/PageHeader'
import { Button } from '@/components/ui/button'
import type { WorldEventResponse } from '@/api/livingWorld'
import { useLatestWorldTickDev, useRunWorldTick, useWorldEvents } from '@/hooks/useLivingWorld'
import { WorldCard } from '@/components/world/WorldCard'

function EventCard({ event }: { event: WorldEventResponse }) {
  return (
    <article className="world-card p-4">
      <p className="text-xs uppercase tracking-wide text-jungle-gold">{event.type}</p>
      <h3 className="mt-1 font-display text-lg font-semibold">{event.title}</h3>
      <p className="mt-2 text-sm text-muted-foreground">{event.summary}</p>
      <p className="mt-2 text-xs text-muted-foreground">{event.effectiveDate}</p>
    </article>
  )
}

interface LivingWorldPageProps {
  adminMode?: boolean
}

export function LivingWorldPage({ adminMode = false }: LivingWorldPageProps) {
  const events = useWorldEvents()
  const latestTick = useLatestWorldTickDev()
  const runTick = useRunWorldTick()

  const festivals = events.data?.filter((event) => event.type === 'FESTIVAL') ?? []
  const birthdays = events.data?.filter((event) => event.type === 'BIRTHDAY') ?? []
  const promises = events.data?.filter((event) => event.type === 'PROMISE_DUE') ?? []
  const activities = events.data?.filter((event) => event.type === 'CHARACTER_ACTIVITY') ?? []
  const gossip = events.data?.filter((event) => event.type === 'ANNOUNCEMENT') ?? []

  return (
    <div className="space-y-6">
      <PageHeader
        title={adminMode ? 'Living World Tick' : 'Living World'}
        description={
          adminMode
            ? 'Developer observability for autonomous world ticks.'
            : 'A bulletin from the beating heart of the Jungle.'
        }
        actions={
          adminMode ? (
            <Button type="button" onClick={() => runTick.mutate()} disabled={runTick.isPending}>
              {runTick.isPending ? 'Ticking…' : 'Run World Tick'}
            </Button>
          ) : undefined
        }
      />

      {latestTick.data ? (
        <WorldCard title="World Date" subtitle={`Latest tick: ${latestTick.data.worldDate}`}>
          <p className="text-sm text-muted-foreground">
            {latestTick.data.eventsGenerated} events · {latestTick.data.artifactsGenerated} artifacts ·{' '}
            {latestTick.data.notificationsGenerated} notifications
          </p>
        </WorldCard>
      ) : null}

      {events.isError ? <ErrorState error={events.error} title="The Jungle bulletin is unavailable" /> : null}

      <section className="grid gap-4 lg:grid-cols-3">
        <WorldCard title="Today" subtitle="Festivals">
          {festivals.length > 0 ? festivals.map((e) => <EventCard key={e.id} event={e} />) : <p className="text-sm text-muted-foreground">No festivals today.</p>}
        </WorldCard>
        <WorldCard title="Today" subtitle="Birthdays">
          {birthdays.length > 0 ? birthdays.map((e) => <EventCard key={e.id} event={e} />) : <p className="text-sm text-muted-foreground">No birthdays today.</p>}
        </WorldCard>
        <WorldCard title="Needs Attention" subtitle="Promises">
          {promises.length > 0 ? promises.map((e) => <EventCard key={e.id} event={e} />) : <p className="text-sm text-muted-foreground">No promises due.</p>}
        </WorldCard>
      </section>

      {activities.length > 0 ? (
        <section className="space-y-4">
          <h2 className="font-display text-lg font-semibold">Autonomous Activity</h2>
          {activities.map((event) => (
            <EventCard key={event.id} event={event} />
          ))}
        </section>
      ) : null}

      {gossip.length > 0 ? (
        <section className="space-y-4">
          <h2 className="font-display text-lg font-semibold">Word on the Wind</h2>
          {gossip.map((event) => (
            <EventCard key={event.id} event={event} />
          ))}
        </section>
      ) : null}

      {events.data && events.data.length > 0 ? (
        <section className="space-y-4">
          <h2 className="font-display text-lg font-semibold">Recently Happened</h2>
          {events.data.slice(0, 10).map((event) => (
            <EventCard key={event.id} event={event} />
          ))}
        </section>
      ) : (
        <EmptyState title="The Jungle is quiet for now." description="Nothing has stirred in the living world yet." />
      )}
    </div>
  )
}
