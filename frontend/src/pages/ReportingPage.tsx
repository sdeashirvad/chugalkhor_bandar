import { useState } from 'react'
import { Breadcrumbs } from '@/components/Breadcrumbs'
import { ErrorState } from '@/components/ErrorState'
import { PageHeader } from '@/components/PageHeader'
import { Button } from '@/components/ui/button'
import { useLatestConsolidationReport } from '@/hooks/useMemoryConsolidation'
import {
  usePreviewReportingHtml,
  usePreviewReportingJson,
  usePreviewReportingMarkdown,
  usePreviewReportingTxt,
  useReportingArchive,
  useReportingHistory,
  useSendTestReportEmail,
} from '@/hooks/useReporting'

function PreviewPanel({ title, content }: { title: string; content: string }) {
  return (
    <section className="rounded-lg border border-border bg-card p-4">
      <h3 className="mb-2 text-sm font-semibold">{title}</h3>
      <pre className="max-h-96 overflow-auto whitespace-pre-wrap text-xs text-muted-foreground">{content}</pre>
    </section>
  )
}

export function ReportingPage() {
  const latest = useLatestConsolidationReport()
  const history = useReportingHistory()
  const archive = useReportingArchive()
  const sendTest = useSendTestReportEmail()
  const previewHtml = usePreviewReportingHtml()
  const previewMd = usePreviewReportingMarkdown()
  const previewTxt = usePreviewReportingTxt()
  const previewJson = usePreviewReportingJson()
  const [previewContent, setPreviewContent] = useState<{ title: string; content: string } | null>(null)

  async function runPreview(
    title: string,
    mutation: { mutateAsync: () => Promise<string>; isPending: boolean },
  ) {
    const content = await mutation.mutateAsync()
    setPreviewContent({ title, content })
  }

  const latestArchive = archive.data?.[0]

  return (
    <div className="space-y-6">
      <Breadcrumbs items={[{ label: 'Reporting' }]} />
      <PageHeader
        title="Reporting & Delivery"
        description="Preview, archive, and deliver Bandar's memory consolidation reports."
        actions={
          <Button type="button" onClick={() => sendTest.mutate()} disabled={sendTest.isPending || latest.isError}>
            {sendTest.isPending ? 'Sending…' : 'Send Test Email'}
          </Button>
        }
      />

      {latest.isError ? (
        <ErrorState
          error={latest.error}
          title="No consolidation report available — run consolidation first"
        />
      ) : null}

      {sendTest.data ? (
        <section className="rounded-lg border border-border bg-card p-4 text-sm">
          <p>
            Test email status: <span className="font-medium">{sendTest.data.status}</span>
          </p>
          <p className="mt-1 text-muted-foreground">
            Sent: {sendTest.data.recipientsSent} · Failed: {sendTest.data.recipientsFailed}
          </p>
          {sendTest.data.error ? <p className="mt-1 text-destructive">{sendTest.data.error}</p> : null}
        </section>
      ) : null}

      {latest.data ? (
        <section className="rounded-lg border border-border bg-card p-4">
          <h2 className="mb-3 text-lg font-semibold">Latest Consolidation Report</h2>
          <dl className="grid gap-2 text-sm sm:grid-cols-3">
            <div>
              <dt className="text-muted-foreground">Run</dt>
              <dd className="font-medium">{latest.data.runId}</dd>
            </div>
            <div>
              <dt className="text-muted-foreground">Email Status</dt>
              <dd className="font-medium">{latest.data.emailStatus}</dd>
            </div>
            <div>
              <dt className="text-muted-foreground">Summary</dt>
              <dd className="font-medium">{latest.data.summary}</dd>
            </div>
          </dl>
        </section>
      ) : null}

      <section className="space-y-3">
        <h2 className="text-lg font-semibold">Preview</h2>
        <div className="flex flex-wrap gap-2">
          <Button
            type="button"
            variant="outline"
            disabled={previewHtml.isPending || latest.isError}
            onClick={() => runPreview('HTML Preview', previewHtml)}
          >
            Preview HTML
          </Button>
          <Button
            type="button"
            variant="outline"
            disabled={previewMd.isPending || latest.isError}
            onClick={() => runPreview('Markdown Preview', previewMd)}
          >
            Preview Markdown
          </Button>
          <Button
            type="button"
            variant="outline"
            disabled={previewTxt.isPending || latest.isError}
            onClick={() => runPreview('TXT Preview', previewTxt)}
          >
            Preview TXT
          </Button>
          <Button
            type="button"
            variant="outline"
            disabled={previewJson.isPending || latest.isError}
            onClick={() => runPreview('JSON Preview', previewJson)}
          >
            Preview JSON
          </Button>
        </div>
        {previewContent ? <PreviewPanel title={previewContent.title} content={previewContent.content} /> : null}
      </section>

      {latestArchive ? (
        <section className="rounded-lg border border-border bg-card p-4">
          <h2 className="mb-3 text-lg font-semibold">Latest Archive</h2>
          <p className="text-sm text-muted-foreground">Report {latestArchive.reportId} · {latestArchive.createdAt}</p>
          <div className="mt-3 grid gap-4 lg:grid-cols-2">
            <PreviewPanel title="Archived TXT" content={latestArchive.txtContent} />
            <PreviewPanel title="Archived Markdown" content={latestArchive.markdownContent} />
          </div>
        </section>
      ) : null}

      {history.data && history.data.length > 0 ? (
        <section className="rounded-lg border border-border bg-card p-4">
          <h2 className="mb-3 text-lg font-semibold">Delivery History</h2>
          <div className="space-y-3 text-sm">
            {history.data.map((entry) => (
              <article key={entry.id} className="rounded-md border border-border bg-muted/20 p-3">
                <p className="font-semibold">
                  {entry.recipient} · {entry.status} · attempt {entry.attempt}
                </p>
                <p className="mt-1 text-muted-foreground">
                  Provider: {entry.provider} · Latency: {entry.latencyMs} ms · {entry.createdAt}
                </p>
                {entry.providerMessageId ? (
                  <p className="mt-1 text-muted-foreground">Message ID: {entry.providerMessageId}</p>
                ) : null}
                {entry.error ? <p className="mt-1 text-destructive">{entry.error}</p> : null}
              </article>
            ))}
          </div>
        </section>
      ) : null}

      {archive.data && archive.data.length > 1 ? (
        <section className="rounded-lg border border-border bg-card p-4">
          <h2 className="mb-3 text-lg font-semibold">Archive</h2>
          <div className="space-y-2 text-sm">
            {archive.data.map((item) => (
              <p key={item.reportId} className="text-muted-foreground">
                {item.reportId} · {item.createdAt}
              </p>
            ))}
          </div>
        </section>
      ) : null}
    </div>
  )
}
