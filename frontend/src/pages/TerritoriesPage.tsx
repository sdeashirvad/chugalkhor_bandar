import { Link } from 'react-router-dom'
import { Breadcrumbs } from '@/components/Breadcrumbs'
import { EmptyState } from '@/components/EmptyState'
import { ErrorState } from '@/components/ErrorState'
import { EntityLink } from '@/components/EntityLink'
import { PageHeader } from '@/components/PageHeader'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Skeleton } from '@/components/ui/skeleton'
import { useTerritories } from '@/hooks/useTerritories'

export function TerritoriesPage() {
  const { data, isLoading, isError, error } = useTerritories()

  if (isLoading) {
    return (
      <div className="space-y-4">
        <Skeleton className="h-8 w-40" />
        <div className="grid gap-4 md:grid-cols-2">
          {Array.from({ length: 3 }).map((_, index) => (
            <Skeleton key={index} className="h-28" />
          ))}
        </div>
      </div>
    )
  }

  if (isError) {
    return <ErrorState error={error} title="Unable to load territories" />
  }

  return (
    <div>
      <Breadcrumbs items={[{ label: 'World', to: '/world' }, { label: 'Territories' }]} />
      <PageHeader title="Territories" description="Governed jungles and their rulers." />

      {!data || data.length === 0 ? (
        <EmptyState title="No territories found." />
      ) : (
        <div className="grid gap-4 md:grid-cols-2">
          {data.map((territory) => (
            <Link key={territory.id} to={`/territories/${territory.id}`} className="block">
              <Card className="h-full transition-colors hover:bg-muted/30">
                <CardHeader>
                  <CardTitle>{territory.name}</CardTitle>
                </CardHeader>
                <CardContent>
                  <p className="text-sm text-muted-foreground">
                    Ruler: <EntityLink entity={territory.ruler} fallback="Unknown" />
                  </p>
                </CardContent>
              </Card>
            </Link>
          ))}
        </div>
      )}
    </div>
  )
}
