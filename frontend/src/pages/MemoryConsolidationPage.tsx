import { Breadcrumbs } from '@/components/Breadcrumbs'
import { ErrorState } from '@/components/ErrorState'
import { PageHeader } from '@/components/PageHeader'
import { Button } from '@/components/ui/button'
import {
  useConsolidationHistory,
  useLatestConsolidationReport,
  useRunConsolidation,
} from '@/hooks/useMemoryConsolidation'

function ReportCard({ report }: { report: import('@/api/memoryConsolidation').MemoryConsolidationReportResponse }) {
  return (
    <article className="rounded-lg border border-border bg-card p-4">
      <div className="flex flex-wrap items-start justify-between gap-3">
        <div>
          <p className="text-xs uppercase tracking-wide text-muted-foreground">Run {report.runId.slice(0, 8)}</p>
          <h3 className="mt-1 text-lg font-semibold">{report.summary}</h3>
        </div>
        <div className="text-right text-xs text-muted-foreground">
          <p>{report.durationMs} ms</p>
          <p className="mt-1">{report.emailStatus}</p>
        </div>
      </div>
      <dl className="mt-3 grid gap-2 text-xs text-muted-foreground sm:grid-cols-3">
        <div>
          <dt>Processed</dt>
          <dd className="font-medium text-foreground">{report.processed}</dd>
        </div>
        <div>
          <dt>Promoted</dt>
          <dd className="font-medium text-foreground">{report.promoted}</dd>
        </div>
        <div>
          <dt>Discarded</dt>
          <dd className="font-medium text-foreground">{report.discarded}</dd>
        </div>
        <div>
          <dt>Candidates</dt>
          <dd className="font-medium text-foreground">{report.candidateCount}</dd>
        </div>
        <div>
          <dt>Pending</dt>
          <dd className="font-medium text-foreground">{report.pending}</dd>
        </div>
        <div>
          <dt>Completed</dt>
          <dd className="font-medium text-foreground">{report.completedAt}</dd>
        </div>
      </dl>
      {report.reflection ? (
        <p className="mt-3 text-sm italic text-muted-foreground">{report.reflection}</p>
      ) : null}
    </article>
  )
}

export function MemoryConsolidationPage() {
  const latest = useLatestConsolidationReport()
  const history = useConsolidationHistory()
  const run = useRunConsolidation()

  return (
    <div className="space-y-6">
      <Breadcrumbs items={[{ label: 'Memory Consolidation' }]} />
      <PageHeader
        title="Memory Consolidation"
        description="Bandar's nightly sleep cycle — reviewing the inbox, promoting memories, and preparing chronicle candidates."
        actions={
          <Button type="button" onClick={() => run.mutate()} disabled={run.isPending}>
            {run.isPending ? 'Running…' : 'Run Consolidation'}
          </Button>
        }
      />

      {latest.isError ? (
        <ErrorState
          error={latest.error}
          title="No consolidation run yet — trigger a manual run to review the memory inbox"
        />
      ) : null}

      {latest.data ? (
        <section className="space-y-4">
          <h2 className="text-lg font-semibold">Latest Run</h2>
          <ReportCard report={latest.data} />
          <section className="rounded-lg border border-border bg-card p-4">
            <h3 className="mb-2 text-sm font-semibold">TXT Report</h3>
            <pre className="overflow-x-auto whitespace-pre-wrap text-xs text-muted-foreground">{latest.data.txtReport}</pre>
          </section>
        </section>
      ) : null}

      {history.data && history.data.length > 1 ? (
        <section className="space-y-4">
          <h2 className="text-lg font-semibold">History</h2>
          {history.data.slice(1).map((report) => (
            <ReportCard key={report.runId} report={report} />
          ))}
        </section>
      ) : null}
    </div>
  )
}
