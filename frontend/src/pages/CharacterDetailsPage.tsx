import { Link, useParams } from 'react-router-dom'
import { BadgeList } from '@/components/BadgeList'
import { Breadcrumbs } from '@/components/Breadcrumbs'
import { EntityLink } from '@/components/EntityLink'
import { ErrorState } from '@/components/ErrorState'
import { PropertyGrid } from '@/components/PropertyGrid'
import { RelationshipCard } from '@/components/RelationshipCard'
import { SectionCard } from '@/components/SectionCard'
import { Skeleton } from '@/components/ui/skeleton'
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table'
import { useCharacter } from '@/hooks/useCharacters'
import { getCharacterAvatar } from '@/lib/avatars'

export function CharacterDetailsPage() {
  const { id } = useParams()
  const { data, isLoading, isError, error } = useCharacter(id)

  if (isLoading) {
    return (
      <div className="space-y-4">
        <Skeleton className="h-8 w-64" />
        <Skeleton className="h-40 w-full" />
        <Skeleton className="h-40 w-full" />
      </div>
    )
  }

  if (isError || !data) {
    return <ErrorState error={error} title="Character not found" />
  }

  const preferenceEntries = Object.entries(data.preferences)
  const subtitle = [data.titles.join(' · '), data.id].filter(Boolean).join(' — ')

  return (
    <div className="space-y-6">
      <Breadcrumbs
        items={[
          { label: 'World', to: '/world' },
          { label: 'Characters', to: '/characters' },
          { label: data.name },
        ]}
      />

      <div className="flex flex-col items-center gap-4 sm:flex-row sm:items-start">
        <img
          src={getCharacterAvatar(data.id)}
          alt=""
          className="h-24 w-24 shrink-0 rounded-full border-2 border-jungle-gold/30 object-cover sm:h-28 sm:w-28"
        />
        <div className="min-w-0 text-center sm:text-left">
          <h1 className="font-display text-3xl font-semibold tracking-tight sm:text-2xl">{data.name}</h1>
          {subtitle ? <p className="mt-1 text-base text-muted-foreground sm:text-sm">{subtitle}</p> : null}
        </div>
      </div>

      <PropertyGrid
        items={[
          {
            label: 'Current Place',
            value: (
              <EntityLink
                entity={
                  data.currentLocation.placeId
                    ? {
                        id: data.currentLocation.placeId,
                        name: data.currentLocation.placeName ?? data.currentLocation.placeId,
                        type: 'place',
                      }
                    : null
                }
              />
            ),
          },
          {
            label: 'Current Territory',
            value: <EntityLink entity={data.currentTerritory} />,
          },
          { label: 'Titles', value: <BadgeList items={data.titles} /> },
        ]}
      />

      {data.profile ? <SectionCard title="Profile">{data.profile}</SectionCard> : null}
      {data.history ? <SectionCard title="History">{data.history}</SectionCard> : null}
      {data.assets ? <SectionCard title="Assets">{data.assets}</SectionCard> : null}

      {data.publicFacts.length > 0 ? (
        <SectionCard title="Public Facts">
          <ul className="list-disc space-y-1 pl-5">
            {data.publicFacts.map((fact) => (
              <li key={fact}>{fact}</li>
            ))}
          </ul>
        </SectionCard>
      ) : null}

      {data.relationships.length > 0 ? (
        <div className="space-y-3">
          <h2 className="text-lg font-semibold">Relationships</h2>
          <div className="grid gap-3 md:grid-cols-2">
            {data.relationships.map((relationship) => (
              <RelationshipCard key={relationship.id} relationship={relationship} />
            ))}
          </div>
        </div>
      ) : null}

      {preferenceEntries.length > 0 ? (
        <SectionCard title="Preferences">
          <dl className="grid gap-3 sm:grid-cols-2">
            {preferenceEntries.map(([key, value]) => (
              <div key={key}>
                <dt className="text-xs font-medium uppercase tracking-wide text-muted-foreground">{key}</dt>
                <dd className="mt-1 text-sm">{value}</dd>
              </div>
            ))}
          </dl>
        </SectionCard>
      ) : null}

      {data.organizations.length > 0 ? (
        <SectionCard title="Organizations">
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Organization</TableHead>
                <TableHead>Role</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {data.organizations.map((organization) => (
                <TableRow key={organization.organizationId}>
                  <TableCell>
                    <Link to={`/organizations/${organization.organizationId}`} className="font-medium hover:underline">
                      {organization.organizationName}
                    </Link>
                  </TableCell>
                  <TableCell>{organization.role}</TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </SectionCard>
      ) : null}
    </div>
  )
}
