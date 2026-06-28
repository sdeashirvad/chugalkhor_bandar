import { ErrorState } from '@/components/ErrorState'
import { PageHeader } from '@/components/PageHeader'
import { Button } from '@/components/ui/button'
import { useArtifacts, useCancelArtifact, useFulfillArtifact } from '@/hooks/useArtifacts'
import type { ConversationArtifactResponse, ConversationArtifactStatus } from '@/api/artifacts'

function isActive(status: ConversationArtifactStatus) {
  return status === 'NEW' || status === 'ACTIVE'
}

function isFulfilled(status: ConversationArtifactStatus) {
  return status === 'FULFILLED'
}

function isArchived(status: ConversationArtifactStatus) {
  return status === 'ARCHIVED' || status === 'CANCELLED' || status === 'EXPIRED'
}

function ArtifactCard({
  artifact,
  onFulfill,
  onCancel,
}: {
  artifact: ConversationArtifactResponse
  onFulfill: (artifact: ConversationArtifactResponse) => void
  onCancel: (artifact: ConversationArtifactResponse) => void
}) {
  const canAct = isActive(artifact.status)

  return (
    <article className="world-card p-5">
      <div className="flex flex-wrap items-start justify-between gap-3">
        <div>
          <p className="text-xs uppercase tracking-wide text-muted-foreground">{artifact.type}</p>
          <h3 className="mt-1 text-lg font-semibold">{artifact.title}</h3>
        </div>
        <div className="text-right text-xs text-muted-foreground">
          <p>{artifact.priority}</p>
          <p className="mt-1">{artifact.status}</p>
        </div>
      </div>
      <p className="mt-3 text-sm text-muted-foreground">{artifact.summary}</p>
      <dl className="mt-3 grid gap-2 text-xs text-muted-foreground sm:grid-cols-2">
        <div>
          <dt>Owner</dt>
          <dd className="font-medium text-foreground">{artifact.ownerCharacterId}</dd>
        </div>
        <div>
          <dt>Recipient</dt>
          <dd className="font-medium text-foreground">{artifact.recipientCharacterId}</dd>
        </div>
        <div>
          <dt>Created</dt>
          <dd className="font-medium text-foreground">{artifact.createdAt}</dd>
        </div>
        <div>
          <dt>Expires</dt>
          <dd className="font-medium text-foreground">{artifact.expiresAt}</dd>
        </div>
      </dl>
      {canAct ? (
        <div className="mt-4 flex gap-2">
          <Button type="button" onClick={() => onFulfill(artifact)}>
            Fulfill
          </Button>
          <Button type="button" variant="outline" onClick={() => onCancel(artifact)}>
            Cancel
          </Button>
        </div>
      ) : null}
    </article>
  )
}

function ArtifactSection({
  title,
  artifacts,
  onFulfill,
  onCancel,
}: {
  title: string
  artifacts: ConversationArtifactResponse[]
  onFulfill: (artifact: ConversationArtifactResponse) => void
  onCancel: (artifact: ConversationArtifactResponse) => void
}) {
  if (artifacts.length === 0) {
    return null
  }

  return (
    <section className="space-y-4">
      <h2 className="text-lg font-semibold">{title}</h2>
      {artifacts.map((artifact) => (
        <ArtifactCard
          key={artifact.id}
          artifact={artifact}
          onFulfill={onFulfill}
          onCancel={onCancel}
        />
      ))}
    </section>
  )
}

export function ArtifactsPage({ adminMode = false }: { adminMode?: boolean }) {
  const artifacts = useArtifacts()
  const fulfill = useFulfillArtifact()
  const cancel = useCancelArtifact()

  const active = artifacts.data?.filter((artifact) => isActive(artifact.status)) ?? []
  const fulfilled = artifacts.data?.filter((artifact) => isFulfilled(artifact.status)) ?? []
  const archived = artifacts.data?.filter((artifact) => isArchived(artifact.status)) ?? []

  return (
    <div className="space-y-6">
      <PageHeader
        title={adminMode ? 'Artifacts Trace' : 'Unfinished Matters'}
        description="Promises, reminders, story seeds — open threads in the Jungle."
      />

      {artifacts.isError ? <ErrorState error={artifacts.error} title="Could not load artifacts" /> : null}

      {!artifacts.isLoading && artifacts.data?.length === 0 ? (
        <p className="text-sm text-muted-foreground">No artifacts yet. Chat with Bandar to create unfinished intentions.</p>
      ) : null}

      <ArtifactSection
        title="Active"
        artifacts={active}
        onFulfill={(artifact) => fulfill.mutate(artifact.id)}
        onCancel={(artifact) => cancel.mutate(artifact.id)}
      />
      <ArtifactSection
        title="Fulfilled"
        artifacts={fulfilled}
        onFulfill={(artifact) => fulfill.mutate(artifact.id)}
        onCancel={(artifact) => cancel.mutate(artifact.id)}
      />
      <ArtifactSection
        title="Archived"
        artifacts={archived}
        onFulfill={(artifact) => fulfill.mutate(artifact.id)}
        onCancel={(artifact) => cancel.mutate(artifact.id)}
      />
    </div>
  )
}
