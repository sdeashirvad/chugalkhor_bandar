import { useParams } from 'react-router-dom'
import { Breadcrumbs } from '@/components/Breadcrumbs'
import { EntityChipList } from '@/components/EntityChipList'
import { EntityLink } from '@/components/EntityLink'
import { ErrorState } from '@/components/ErrorState'
import { PageHeader } from '@/components/PageHeader'
import { PropertyGrid } from '@/components/PropertyGrid'
import { SectionCard } from '@/components/SectionCard'
import { Skeleton } from '@/components/ui/skeleton'
import { usePlace } from '@/hooks/usePlaces'
import { formatSectionLabel } from '@/lib/utils'

export function PlaceDetailsPage() {
  const { id } = useParams()
  const { data, isLoading, isError, error } = usePlace(id)

  if (isLoading) {
    return (
      <div className="space-y-4">
        <Skeleton className="h-8 w-64" />
        <Skeleton className="h-32 w-full" />
      </div>
    )
  }

  if (isError || !data) {
    return <ErrorState error={error} title="Place not found" />
  }

  return (
    <div className="space-y-6">
      <Breadcrumbs
        items={[
          { label: 'World', to: '/world' },
          { label: 'Places', to: '/places' },
          { label: data.name },
        ]}
      />
      <PageHeader title={data.name} description={data.type || data.id} />

      <PropertyGrid
        items={[
          { label: 'Territory', value: <EntityLink entity={data.territory} /> },
          { label: 'Owner', value: <EntityLink entity={data.owner} /> },
        ]}
      />

      <SectionCard title="Connected Places">
        <EntityChipList items={data.connectedPlaces} emptyLabel="No connected places listed." />
      </SectionCard>

      {Object.entries(data.sections).map(([key, value]) => (
        <SectionCard key={key} title={formatSectionLabel(key)}>
          {value}
        </SectionCard>
      ))}
    </div>
  )
}
