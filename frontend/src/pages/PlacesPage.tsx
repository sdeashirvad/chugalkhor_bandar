import { Link } from 'react-router-dom'
import { Breadcrumbs } from '@/components/Breadcrumbs'
import { EmptyState } from '@/components/EmptyState'
import { ErrorState } from '@/components/ErrorState'
import { EntityLink } from '@/components/EntityLink'
import { PageHeader } from '@/components/PageHeader'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Skeleton } from '@/components/ui/skeleton'
import { usePlaces } from '@/hooks/usePlaces'

export function PlacesPage() {
  const { data, isLoading, isError, error } = usePlaces()

  if (isLoading) {
    return (
      <div className="space-y-4">
        <Skeleton className="h-8 w-32" />
        <Skeleton className="h-64 w-full" />
      </div>
    )
  }

  if (isError) {
    return <ErrorState error={error} title="Unable to load places" />
  }

  return (
    <div>
      <Breadcrumbs items={[{ label: 'World', to: '/world' }, { label: 'Places' }]} />
      <PageHeader title="Places" description="Named locations across the Jungle." />

      {!data || data.length === 0 ? (
        <EmptyState title="No places found." />
      ) : (
        <div className="grid gap-4 md:grid-cols-2">
          {data.map((place) => (
            <Link key={place.id} to={`/places/${place.id}`} className="block">
              <Card className="h-full transition-colors hover:bg-muted/30">
                <CardHeader>
                  <CardTitle>{place.name}</CardTitle>
                </CardHeader>
                <CardContent className="space-y-1 text-sm text-muted-foreground">
                  <p>{place.type || 'Place'}</p>
                  <p>
                    Territory: <EntityLink entity={place.territory} fallback="—" />
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
