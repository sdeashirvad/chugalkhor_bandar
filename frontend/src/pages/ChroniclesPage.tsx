import { useMemo, useState } from 'react'
import { ErrorState } from '@/components/ErrorState'
import { PageHeader } from '@/components/PageHeader'
import { Button } from '@/components/ui/button'
import type { ChronicleResponse } from '@/api/chronicles'
import { useChronicles, useWriteChronicles } from '@/hooks/useChronicles'

type GroupBy = 'category' | 'visibility' | 'date'

function ChronicleCard({ chronicle }: { chronicle: ChronicleResponse }) {
  const [showProvenance, setShowProvenance] = useState(false)

  return (
    <article className="world-card p-5">
      <div className="flex flex-wrap items-start justify-between gap-3">
        <div>
          <p className="text-xs uppercase tracking-wide text-muted-foreground">
            {chronicle.category} · {chronicle.visibility} · v{chronicle.version}
          </p>
          <h3 className="mt-1 text-lg font-semibold">{chronicle.title}</h3>
        </div>
        <div className="text-right text-xs text-muted-foreground">
          <p>{chronicle.confidence}</p>
          <p className="mt-1">{chronicle.chronicleDate}</p>
        </div>
      </div>
      <p className="mt-3 text-sm text-muted-foreground">{chronicle.summary}</p>
      <p className="mt-2 text-sm italic">{chronicle.body}</p>
      <dl className="mt-3 grid gap-2 text-xs text-muted-foreground sm:grid-cols-2">
        <div>
          <dt>Owner</dt>
          <dd className="font-medium text-foreground">{chronicle.ownerCharacterId}</dd>
        </div>
        <div>
          <dt>Created</dt>
          <dd className="font-medium text-foreground">{chronicle.createdAt}</dd>
        </div>
      </dl>
      <Button
        type="button"
        variant="outline"
        size="sm"
        className="mt-3"
        onClick={() => setShowProvenance((value) => !value)}
      >
        {showProvenance ? 'Hide Provenance' : 'View Provenance'}
      </Button>
      {showProvenance ? (
        <ul className="mt-3 space-y-2 text-xs text-muted-foreground">
          {chronicle.provenance.chain.map((link) => (
            <li key={`${link.stage}-${link.entityId}`} className="rounded-md border border-border bg-muted/20 p-2">
              <span className="font-medium text-foreground">{link.stage}</span> · {link.entityId} — {link.label}
            </li>
          ))}
        </ul>
      ) : null}
    </article>
  )
}

function groupChronicles(chronicles: ChronicleResponse[], groupBy: GroupBy) {
  const groups = new Map<string, ChronicleResponse[]>()
  for (const chronicle of chronicles) {
    const key =
      groupBy === 'category'
        ? chronicle.category
        : groupBy === 'visibility'
          ? chronicle.visibility
          : chronicle.chronicleDate
    const existing = groups.get(key) ?? []
    existing.push(chronicle)
    groups.set(key, existing)
  }
  return [...groups.entries()].sort(([a], [b]) => a.localeCompare(b))
}

export function ChroniclesPage({ adminMode = false }: { adminMode?: boolean }) {
  const chronicles = useChronicles()
  const write = useWriteChronicles()
  const [groupBy, setGroupBy] = useState<GroupBy>('category')

  const grouped = useMemo(
    () => (chronicles.data ? groupChronicles(chronicles.data, groupBy) : []),
    [chronicles.data, groupBy],
  )

  return (
    <div className="space-y-6">
      <PageHeader
        title={adminMode ? 'Chronicle Writer' : 'Chronicles'}
        description="The memory of the Jungle — permanent, append-only history."
        actions={
          adminMode ? (
            <Button type="button" onClick={() => write.mutate()} disabled={write.isPending}>
              {write.isPending ? 'Writing…' : 'Write Chronicles'}
            </Button>
          ) : undefined
        }
      />

      <div className="flex flex-wrap gap-2">
        {(['category', 'visibility', 'date'] as GroupBy[]).map((option) => (
          <Button
            key={option}
            type="button"
            variant={groupBy === option ? 'default' : 'outline'}
            size="sm"
            onClick={() => setGroupBy(option)}
          >
            Group by {option}
          </Button>
        ))}
      </div>

      {write.data ? (
        <section className="rounded-lg border border-border bg-card p-4 text-sm">
          <p>
            Latest write: {write.data.chroniclesWritten} written · {write.data.skipped} skipped
          </p>
        </section>
      ) : null}

      {chronicles.isError ? <ErrorState error={chronicles.error} title="Failed to load chronicles" /> : null}

      {grouped.length === 0 && !chronicles.isLoading ? (
        <p className="text-sm text-muted-foreground">
          No chronicles yet. Run consolidation, then write chronicles from trusted candidates.
        </p>
      ) : null}

      {grouped.map(([group, items]) => (
        <section key={group} className="space-y-4">
          <h2 className="text-lg font-semibold">{group}</h2>
          {items.map((chronicle) => (
            <ChronicleCard key={chronicle.id} chronicle={chronicle} />
          ))}
        </section>
      ))}
    </div>
  )
}
