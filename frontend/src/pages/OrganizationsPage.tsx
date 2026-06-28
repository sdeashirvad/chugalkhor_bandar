import { Link } from 'react-router-dom'
import { Breadcrumbs } from '@/components/Breadcrumbs'
import { EmptyState } from '@/components/EmptyState'
import { ErrorState } from '@/components/ErrorState'
import { EntityLink } from '@/components/EntityLink'
import { PageHeader } from '@/components/PageHeader'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Skeleton } from '@/components/ui/skeleton'
import { useOrganizations } from '@/hooks/useOrganizations'

export function OrganizationsPage() {
  const { data, isLoading, isError, error } = useOrganizations()

  if (isLoading) {
    return (
      <div className="space-y-4">
        <Skeleton className="h-8 w-40" />
        <Skeleton className="h-64 w-full" />
      </div>
    )
  }

  if (isError) {
    return <ErrorState error={error} title="Unable to load organizations" />
  }

  return (
    <div>
      <Breadcrumbs items={[{ label: 'World', to: '/world' }, { label: 'Organizations' }]} />
      <PageHeader title="Organizations" description="Institutions and groups in the Jungle." />

      {!data || data.length === 0 ? (
        <EmptyState title="No organizations found." />
      ) : (
        <div className="grid gap-4 md:grid-cols-2">
          {data.map((organization) => (
            <Link key={organization.id} to={`/organizations/${organization.id}`} className="block">
              <Card className="h-full transition-colors hover:bg-muted/30">
                <CardHeader>
                  <CardTitle>{organization.name}</CardTitle>
                </CardHeader>
                <CardContent className="space-y-1 text-sm text-muted-foreground">
                  <p>{organization.type || 'Organization'}</p>
                  <p>
                    Leader: <EntityLink entity={organization.leader} fallback="—" />
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
