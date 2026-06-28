import { Badge } from '@/components/ui/badge'
import { Breadcrumbs } from '@/components/Breadcrumbs'
import { ErrorState } from '@/components/ErrorState'
import { PageHeader } from '@/components/PageHeader'
import { SectionCard } from '@/components/SectionCard'
import { StatCard } from '@/components/StatCard'
import { Skeleton } from '@/components/ui/skeleton'
import { useWorldStatus } from '@/hooks/useWorldStatus'
import { formatDateTime, formatProvider } from '@/lib/utils'

export function WorldDashboardPage() {
  const { data, isLoading, isError, error } = useWorldStatus()

  if (isLoading) {
    return (
      <div className="space-y-6">
        <Skeleton className="h-8 w-48" />
        <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
          {Array.from({ length: 8 }).map((_, index) => (
            <Skeleton key={index} className="h-28" />
          ))}
        </div>
      </div>
    )
  }

  if (isError || !data) {
    return <ErrorState error={error} title="Unable to load world status" />
  }

  const statusVariant =
    data.status === 'READY' ? 'success' : data.status === 'STARTING' ? 'warning' : 'outline'

  return (
    <div>
      <Breadcrumbs items={[{ label: 'World' }]} />
      <PageHeader
        title="World Dashboard"
        description="Navigate the compiled Jungle through linked entities."
        actions={<Badge variant={statusVariant}>{data.status}</Badge>}
      />

      <div className="mb-6 grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
        <StatCard label="Bootstrap Version" value={data.bootstrapVersion} />
        <StatCard label="Persistence" value={formatProvider(data.persistenceProvider)} />
        <StatCard label="Runtime Started" value={formatDateTime(data.runtimeStartedAt)} />
        <StatCard label="Timeline Entries" value={data.timelineEntries} />
      </div>

      <div className="mb-6 grid gap-4 sm:grid-cols-2 xl:grid-cols-3">
        <StatCard label="Characters" value={data.characters} />
        <StatCard label="Stories" value={data.stories} />
        <StatCard label="Territories" value={data.territories} />
        <StatCard label="Places" value={data.places} />
        <StatCard label="Organizations" value={data.organizations} />
        <StatCard label="Relationships" value={data.relationships} />
      </div>

      <div className="grid gap-4 lg:grid-cols-2">
        <SectionCard title="Characters by Species">
          {Object.keys(data.charactersBySpecies).length === 0 ? (
            <p className="text-sm text-muted-foreground">No species data yet.</p>
          ) : (
            <dl className="grid gap-2 sm:grid-cols-2">
              {Object.entries(data.charactersBySpecies).map(([species, count]) => (
                <div key={species} className="flex justify-between rounded-md border border-border px-3 py-2">
                  <dt className="text-sm">{species}</dt>
                  <dd className="text-sm font-semibold">{count}</dd>
                </div>
              ))}
            </dl>
          )}
        </SectionCard>
        <SectionCard title="Stories by Era">
          {Object.keys(data.storiesByEra).length === 0 ? (
            <p className="text-sm text-muted-foreground">No era data yet.</p>
          ) : (
            <dl className="grid gap-2 sm:grid-cols-2">
              {Object.entries(data.storiesByEra).map(([era, count]) => (
                <div key={era} className="flex justify-between rounded-md border border-border px-3 py-2">
                  <dt className="text-sm">{era}</dt>
                  <dd className="text-sm font-semibold">{count}</dd>
                </div>
              ))}
            </dl>
          )}
        </SectionCard>
      </div>
    </div>
  )
}
