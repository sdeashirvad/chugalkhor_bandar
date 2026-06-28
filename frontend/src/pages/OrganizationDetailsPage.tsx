import { useParams } from 'react-router-dom'
import { Breadcrumbs } from '@/components/Breadcrumbs'
import { EntityChipList } from '@/components/EntityChipList'
import { EntityLink } from '@/components/EntityLink'
import { ErrorState } from '@/components/ErrorState'
import { PageHeader } from '@/components/PageHeader'
import { PropertyGrid } from '@/components/PropertyGrid'
import { SectionCard } from '@/components/SectionCard'
import { Skeleton } from '@/components/ui/skeleton'
import { useOrganization } from '@/hooks/useOrganizations'
import { formatSectionLabel } from '@/lib/utils'

export function OrganizationDetailsPage() {
  const { id } = useParams()
  const { data, isLoading, isError, error } = useOrganization(id)

  if (isLoading) {
    return (
      <div className="space-y-4">
        <Skeleton className="h-8 w-64" />
        <Skeleton className="h-32 w-full" />
      </div>
    )
  }

  if (isError || !data) {
    return <ErrorState error={error} title="Organization not found" />
  }

  return (
    <div className="space-y-6">
      <Breadcrumbs
        items={[
          { label: 'World', to: '/world' },
          { label: 'Organizations', to: '/organizations' },
          { label: data.name },
        ]}
      />
      <PageHeader title={data.name} description={data.type || data.id} />

      <PropertyGrid
        items={[
          { label: 'Leader', value: <EntityLink entity={data.leader} /> },
          { label: 'Headquarters', value: <EntityLink entity={data.headquarters} /> },
        ]}
      />

      <SectionCard title="Members">
        <EntityChipList items={data.members} emptyLabel="No members listed." />
      </SectionCard>

      {Object.entries(data.sections).map(([key, value]) => (
        <SectionCard key={key} title={formatSectionLabel(key)}>
          {value}
        </SectionCard>
      ))}
    </div>
  )
}
