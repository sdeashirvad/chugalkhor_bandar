import { Link, useParams } from 'react-router-dom'
import { Breadcrumbs } from '@/components/Breadcrumbs'
import { EntityChipList } from '@/components/EntityChipList'
import { ErrorState } from '@/components/ErrorState'
import { PageHeader } from '@/components/PageHeader'
import { SectionCard } from '@/components/SectionCard'
import { Skeleton } from '@/components/ui/skeleton'
import { TimelineList, splitTimelineEntries } from '@/components/TimelineList'
import { useStory } from '@/hooks/useStories'
import { formatSectionLabel } from '@/lib/utils'

const TIMELINE_SECTIONS = ['beginning', 'keyEvents', 'ending', 'canonicalConsequences', 'consequences']

export function StoryDetailsPage() {
  const { id } = useParams()
  const { data, isLoading, isError, error } = useStory(id)

  if (isLoading) {
    return (
      <div className="space-y-4">
        <Skeleton className="h-8 w-72" />
        <Skeleton className="h-40 w-full" />
      </div>
    )
  }

  if (isError || !data) {
    return <ErrorState error={error} title="Story not found" />
  }

  const extraSections = Object.keys(data.sections).filter(
    (key) =>
      key !== 'summary' &&
      key !== 'secrets' &&
      !TIMELINE_SECTIONS.includes(key) &&
      key !== 'participants' &&
      key !== 'majorPlaces',
  )

  return (
    <div className="space-y-6">
      <Breadcrumbs
        items={[
          { label: 'World', to: '/world' },
          { label: 'Stories', to: '/stories' },
          { label: data.title },
        ]}
      />
      <PageHeader title={data.title} description={data.era || data.id} />

      {data.summary ? <SectionCard title="Summary">{data.summary}</SectionCard> : null}

      <SectionCard title="Participants">
        <EntityChipList items={data.participants} emptyLabel="No participants listed." />
      </SectionCard>

      <SectionCard title="Places">
        <EntityChipList items={data.places} emptyLabel="No places listed." />
      </SectionCard>

      {TIMELINE_SECTIONS.filter((key) => data.sections[key]).map((key) => {
        const content = data.sections[key]
        const title = formatSectionLabel(key)
        if (key === 'keyEvents') {
          return (
            <SectionCard key={key} title={title}>
              <TimelineList items={splitTimelineEntries(content)} />
            </SectionCard>
          )
        }
        return (
          <SectionCard key={key} title={title}>
            {content}
          </SectionCard>
        )
      })}

      {extraSections.map((key) => (
        <SectionCard key={key} title={formatSectionLabel(key)}>
          {data.sections[key]}
        </SectionCard>
      ))}

      {Object.keys(data.linkedStories).length > 0 ? (
        <SectionCard title="Linked Stories">
          <ul className="space-y-2 text-sm">
            {Object.entries(data.linkedStories).map(([linkedId, label]) => (
              <li key={linkedId}>
                <Link to={`/stories/${linkedId}`} className="font-medium hover:underline">
                  {label || linkedId}
                </Link>
              </li>
            ))}
          </ul>
        </SectionCard>
      ) : null}
    </div>
  )
}
