import { Breadcrumbs } from '@/components/Breadcrumbs'
import { ErrorState } from '@/components/ErrorState'
import { PageHeader } from '@/components/PageHeader'
import { Button } from '@/components/ui/button'
import { useDiscardMemoryInboxItem, useMemoryInbox, useReviewMemoryInboxItem } from '@/hooks/useMemoryInbox'
import type { MemoryInboxItemResponse, MemoryInboxStatus } from '@/api/memoryInbox'

function isNew(status: MemoryInboxStatus) {
  return status === 'NEW'
}

function isReviewed(status: MemoryInboxStatus) {
  return status === 'REVIEWED'
}

function isPromoted(status: MemoryInboxStatus) {
  return status === 'PROMOTED'
}

function isDiscarded(status: MemoryInboxStatus) {
  return status === 'DISCARDED'
}

function isArchived(status: MemoryInboxStatus) {
  return status === 'ARCHIVED' || status === 'EXPIRED'
}

function InboxCard({
  item,
  onReview,
  onDiscard,
}: {
  item: MemoryInboxItemResponse
  onReview: (item: MemoryInboxItemResponse) => void
  onDiscard: (item: MemoryInboxItemResponse) => void
}) {
  const canAct = isNew(item.status)

  return (
    <article className="rounded-lg border border-border bg-card p-4">
      <div className="flex flex-wrap items-start justify-between gap-3">
        <div>
          <p className="text-xs uppercase tracking-wide text-muted-foreground">{item.source}</p>
          <h3 className="mt-1 text-lg font-semibold">{item.type}</h3>
        </div>
        <div className="text-right text-xs text-muted-foreground">
          <p>{item.importance}</p>
          <p className="mt-1">{item.status}</p>
        </div>
      </div>
      <p className="mt-3 text-sm text-muted-foreground">{item.summary}</p>
      <dl className="mt-3 grid gap-2 text-xs text-muted-foreground sm:grid-cols-2">
        <div>
          <dt>Confidence</dt>
          <dd className="font-medium text-foreground">{item.confidence}</dd>
        </div>
        <div>
          <dt>Created</dt>
          <dd className="font-medium text-foreground">{item.createdAt}</dd>
        </div>
        <div>
          <dt>Expires</dt>
          <dd className="font-medium text-foreground">{item.expiresAt}</dd>
        </div>
        <div>
          <dt>Evidence</dt>
          <dd className="font-medium text-foreground">{item.metadata.evidence ?? item.summary}</dd>
        </div>
      </dl>
      {item.artifactIds.length > 0 ? (
        <p className="mt-2 text-xs text-muted-foreground">Artifacts: {item.artifactIds.join(', ')}</p>
      ) : null}
      {item.analysisId ? (
        <p className="mt-1 text-xs text-muted-foreground">Analysis: {item.analysisId}</p>
      ) : null}
      {canAct ? (
        <div className="mt-4 flex gap-2">
          <Button type="button" onClick={() => onReview(item)}>
            Review
          </Button>
          <Button type="button" variant="outline" onClick={() => onDiscard(item)}>
            Discard
          </Button>
        </div>
      ) : null}
    </article>
  )
}

function InboxSection({
  title,
  items,
  onReview,
  onDiscard,
}: {
  title: string
  items: MemoryInboxItemResponse[]
  onReview: (item: MemoryInboxItemResponse) => void
  onDiscard: (item: MemoryInboxItemResponse) => void
}) {
  if (items.length === 0) {
    return null
  }

  return (
    <section className="space-y-4">
      <h2 className="text-lg font-semibold">{title}</h2>
      {items.map((item) => (
        <InboxCard key={item.id} item={item} onReview={onReview} onDiscard={onDiscard} />
      ))}
    </section>
  )
}

export function MemoryInboxPage() {
  const inbox = useMemoryInbox()
  const review = useReviewMemoryInboxItem()
  const discard = useDiscardMemoryInboxItem()

  const newItems = inbox.data?.filter((item) => isNew(item.status)) ?? []
  const reviewed = inbox.data?.filter((item) => isReviewed(item.status)) ?? []
  const promoted = inbox.data?.filter((item) => isPromoted(item.status)) ?? []
  const discarded = inbox.data?.filter((item) => isDiscarded(item.status)) ?? []
  const archived = inbox.data?.filter((item) => isArchived(item.status)) ?? []

  return (
    <div className="space-y-6">
      <Breadcrumbs items={[{ label: 'Memory Inbox' }]} />
      <PageHeader
        title="Memory Inbox"
        description="Candidates for long-term memory awaiting review. Nothing here is guaranteed to survive."
      />

      {inbox.isError ? <ErrorState error={inbox.error} title="Could not load memory inbox" /> : null}

      {!inbox.isLoading && inbox.data?.length === 0 ? (
        <p className="text-sm text-muted-foreground">
          No inbox items yet. Chat with Bandar to generate observations and artifacts that may become memories.
        </p>
      ) : null}

      <InboxSection
        title="New"
        items={newItems}
        onReview={(item) => review.mutate(item.id)}
        onDiscard={(item) => discard.mutate(item.id)}
      />
      <InboxSection
        title="Reviewed"
        items={reviewed}
        onReview={(item) => review.mutate(item.id)}
        onDiscard={(item) => discard.mutate(item.id)}
      />
      <InboxSection
        title="Promoted"
        items={promoted}
        onReview={(item) => review.mutate(item.id)}
        onDiscard={(item) => discard.mutate(item.id)}
      />
      <InboxSection
        title="Discarded"
        items={discarded}
        onReview={(item) => review.mutate(item.id)}
        onDiscard={(item) => discard.mutate(item.id)}
      />
      <InboxSection
        title="Archived"
        items={archived}
        onReview={(item) => review.mutate(item.id)}
        onDiscard={(item) => discard.mutate(item.id)}
      />
    </div>
  )
}
