import { useParams } from 'react-router-dom'
import { Breadcrumbs } from '@/components/Breadcrumbs'
import { EntityChipList } from '@/components/EntityChipList'
import { EntityLink } from '@/components/EntityLink'
import { ErrorState } from '@/components/ErrorState'
import { PageHeader } from '@/components/PageHeader'
import { PropertyGrid } from '@/components/PropertyGrid'
import { SectionCard } from '@/components/SectionCard'
import { Skeleton } from '@/components/ui/skeleton'
import { useTerritory } from '@/hooks/useTerritories'
import { formatSectionLabel } from '@/lib/utils'

const DETAIL_SECTIONS = ['goals', 'knownGoals', 'government', 'primaryInstitutions', 'history']

export function TerritoryDetailsPage() {
  const { id } = useParams()
  const { data, isLoading, isError, error } = useTerritory(id)

  if (isLoading) {
    return (
      <div className="space-y-4">
        <Skeleton className="h-8 w-64" />
        <Skeleton className="h-32 w-full" />
      </div>
    )
  }

  if (isError || !data) {
    return <ErrorState error={error} title="Territory not found" />
  }

  const extraSections = Object.keys(data.sections).filter(
    (key) => key !== 'secrets' && !DETAIL_SECTIONS.includes(key),
  )

  return (
    <div className="space-y-6">
      <Breadcrumbs
        items={[
          { label: 'World', to: '/world' },
          { label: 'Territories', to: '/territories' },
          { label: data.name },
        ]}
      />
      <PageHeader title={data.name} description={data.id} />

      <PropertyGrid items={[{ label: 'Ruler', value: <EntityLink entity={data.ruler} /> }]} />

      <SectionCard title="Ministers">
        <EntityChipList items={data.ministers} emptyLabel="No ministers listed." />
      </SectionCard>

      <SectionCard title="Places">
        <EntityChipList items={data.places} emptyLabel="No places linked yet." />
      </SectionCard>

      {[...DETAIL_SECTIONS, ...extraSections]
        .filter((key, index, array) => array.indexOf(key) === index && data.sections[key])
        .map((key) => (
          <SectionCard key={key} title={formatSectionLabel(key)}>
            {data.sections[key]}
          </SectionCard>
        ))}
    </div>
  )
}
