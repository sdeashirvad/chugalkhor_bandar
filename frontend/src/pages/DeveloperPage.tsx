import { useState } from 'react'
import { Breadcrumbs } from '@/components/Breadcrumbs'
import { ErrorState } from '@/components/ErrorState'
import { PageHeader } from '@/components/PageHeader'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { useContextPlan, useContextResolve } from '@/hooks/useContextPlan'
import { useLlmGenerate } from '@/hooks/useLlmGenerate'
import { usePromptCompose } from '@/hooks/usePromptCompose'
import { usePromptBudget, usePromptProfile } from '@/hooks/usePromptProfile'
import { useAllNotificationsDev, useNotificationGenerationTrace } from '@/hooks/useNotifications'
import { useAllArtifactsDev, useArtifactGenerationTrace } from '@/hooks/useArtifacts'
import {
  useAllCognitiveAnalysesDev,
  useCognitiveAnalysisExecution,
  useCognitiveObservations,
  useCognitiveRecommendations,
} from '@/hooks/useCognitiveAnalysis'
import { useAllMemoryInboxDev, useMemoryInboxGenerationTrace } from '@/hooks/useMemoryInbox'
import {
  useAllCandidatesDev,
  useConsolidationExecutionDev,
} from '@/hooks/useMemoryConsolidation'
import {
  usePreviewReportingHtml,
  useReportingArchive,
  useReportingConfiguration,
  useReportingHistory,
  useSendTestReportEmail,
} from '@/hooks/useReporting'
import {
  useChronicleWriteExecutionDev,
  useChronicles,
  useWriteChronicles,
} from '@/hooks/useChronicles'
import { useLatestWorldTickDev, useRunWorldTick, useWorldEvents } from '@/hooks/useLivingWorld'
import { useBehaviorEngineProfile } from '@/hooks/useBehaviorEngine'
import { useConversationDirectorPlan } from '@/hooks/useConversationDirector'
import { useRebuildWorkingMemory, useWorkingMemory } from '@/hooks/useWorkingMemory'

function MessageForm({
  latestMessage,
  onChange,
  onSubmit,
  isPending,
  submitLabel,
  pendingLabel,
}: {
  latestMessage: string
  onChange: (value: string) => void
  onSubmit: (event: React.FormEvent) => void
  isPending: boolean
  submitLabel: string
  pendingLabel: string
}) {
  return (
    <form className="flex flex-col gap-3 sm:flex-row" onSubmit={onSubmit}>
      <Input
        value={latestMessage}
        onChange={(event) => onChange(event.target.value)}
        placeholder="Latest user message"
        aria-label="Latest user message"
      />
      <Button type="submit" disabled={isPending}>
        {isPending ? pendingLabel : submitLabel}
      </Button>
    </form>
  )
}

export function ContextPlanPanel() {
  const [latestMessage, setLatestMessage] = useState('Where am I in the Jungle?')
  const contextPlan = useContextPlan()

  function handleSubmit(event: React.FormEvent) {
    event.preventDefault()
    contextPlan.mutate({ latestMessage })
  }

  return (
    <div className="space-y-6">
      <PageHeader
        title="Context Plan"
        description="Deterministic planning only — references, not prompt text."
      />

      <MessageForm
        latestMessage={latestMessage}
        onChange={setLatestMessage}
        onSubmit={handleSubmit}
        isPending={contextPlan.isPending}
        submitLabel="Plan Context"
        pendingLabel="Planning…"
      />

      {contextPlan.isError ? <ErrorState error={contextPlan.error} title="Planning failed" /> : null}

      {contextPlan.data ? (
        <div className="space-y-6">
          <p className="text-sm text-muted-foreground">
            Total estimated tokens: {contextPlan.data.totalEstimatedTokens}
          </p>

          <section>
            <h2 className="mb-3 text-lg font-semibold">Ordered Sections</h2>
            <div className="overflow-x-auto rounded-lg border border-border">
              <table className="min-w-full text-sm">
                <thead className="bg-muted/50 text-left">
                  <tr>
                    <th className="px-3 py-2 font-medium">Priority</th>
                    <th className="px-3 py-2 font-medium">Type</th>
                    <th className="px-3 py-2 font-medium">Source</th>
                    <th className="px-3 py-2 font-medium">Reference</th>
                    <th className="px-3 py-2 font-medium">Tokens</th>
                  </tr>
                </thead>
                <tbody>
                  {contextPlan.data.sections.map((section) => (
                    <tr key={`${section.type}-${section.contentReference}`} className="border-t border-border">
                      <td className="px-3 py-2">{section.priority}</td>
                      <td className="px-3 py-2 font-medium">{section.type}</td>
                      <td className="px-3 py-2">{section.source}</td>
                      <td className="px-3 py-2 font-mono text-xs">{section.contentReference}</td>
                      <td className="px-3 py-2">{section.estimatedTokens}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </section>

          <section>
            <h2 className="mb-3 text-lg font-semibold">Planning Trace</h2>
            <ul className="space-y-2">
              {contextPlan.data.trace.entries.map((entry) => (
                <li key={entry.type} className="rounded-md border border-border bg-card px-3 py-2 text-sm">
                  <p className="font-medium">{entry.type}</p>
                  <p className="text-muted-foreground">{entry.reason}</p>
                </li>
              ))}
            </ul>
          </section>
        </div>
      ) : null}
    </div>
  )
}

export function KnowledgeFragmentsPanel() {
  const [latestMessage, setLatestMessage] = useState('Where am I in the Jungle?')
  const contextResolve = useContextResolve()

  function handleSubmit(event: React.FormEvent) {
    event.preventDefault()
    contextResolve.mutate({ latestMessage })
  }

  return (
    <div className="space-y-6">
      <PageHeader
        title="Knowledge Fragments"
        description="Fine-grained semantic fragments retrieved for the latest message."
      />

      <MessageForm
        latestMessage={latestMessage}
        onChange={setLatestMessage}
        onSubmit={handleSubmit}
        isPending={contextResolve.isPending}
        submitLabel="Load Fragments"
        pendingLabel="Loading…"
      />

      {contextResolve.isError ? <ErrorState error={contextResolve.error} title="Fragment resolution failed" /> : null}

      {contextResolve.data ? (
        <div className="space-y-6">
          <p className="text-sm text-muted-foreground">
            Total estimated tokens: {contextResolve.data.totalEstimatedTokens}
          </p>

          <section className="space-y-4">
            {contextResolve.data.fragments.map((fragment) => (
              <article key={fragment.fragmentId} className="rounded-lg border border-border bg-card p-4">
                <div className="mb-2 flex flex-wrap items-center gap-2 text-sm">
                  <span className="font-semibold">{fragment.title}</span>
                  <span className="text-muted-foreground">{fragment.fragmentType}</span>
                  <span className="text-muted-foreground">tokens {fragment.estimatedTokens}</span>
                </div>
                <p className="mb-2 text-xs text-muted-foreground">
                  Source: {fragment.sourceDocument} / {fragment.sourceSection}
                </p>
                {fragment.tags.length > 0 ? (
                  <p className="mb-2 text-xs text-muted-foreground">Tags: {fragment.tags.join(', ')}</p>
                ) : null}
                <p className="mb-2 text-xs text-muted-foreground">Reason: {fragment.selectionReason}</p>
                <pre className="whitespace-pre-wrap rounded-md bg-muted/40 p-3 text-sm">{fragment.content}</pre>
              </article>
            ))}
          </section>
        </div>
      ) : null}
    </div>
  )
}

export function PromptCompositionPanel() {
  const [latestMessage, setLatestMessage] = useState('Where am I in the Jungle?')
  const [view, setView] = useState<'internal' | 'llm'>('internal')
  const promptCompose = usePromptCompose()

  function handleSubmit(event: React.FormEvent) {
    event.preventDefault()
    promptCompose.mutate({ latestMessage })
  }

  return (
    <div className="space-y-6">
      <PageHeader
        title="Prompt Composition"
        description="Structured, provider-independent prompt sections. Not a final LLM prompt."
      />

      <MessageForm
        latestMessage={latestMessage}
        onChange={setLatestMessage}
        onSubmit={handleSubmit}
        isPending={promptCompose.isPending}
        submitLabel="Compose Prompt"
        pendingLabel="Composing…"
      />

      {promptCompose.isError ? <ErrorState error={promptCompose.error} title="Composition failed" /> : null}

      {promptCompose.data ? (
        <div className="space-y-6">
          <section className="rounded-lg border border-border bg-card p-4">
            <h2 className="mb-3 text-lg font-semibold">Identity Diagnostics</h2>
            <dl className="grid gap-4 text-sm md:grid-cols-2">
              {promptCompose.data.sections
                .filter((section) => section.sectionType === 'CURRENT_USER')
                .map((section) => (
                  <div key={section.sectionType} className="md:col-span-2">
                    <dt className="text-muted-foreground">Current User</dt>
                    <dd className="mt-1 whitespace-pre-wrap rounded-md bg-muted/40 p-3">{section.content}</dd>
                  </div>
                ))}
              {promptCompose.data.sections
                .filter((section) => section.sectionType === 'RELATIONSHIP_TO_BANDAR')
                .map((section) => (
                  <div key={section.sectionType} className="md:col-span-2">
                    <dt className="text-muted-foreground">Relationship To Bandar</dt>
                    <dd className="mt-1 whitespace-pre-wrap rounded-md bg-muted/40 p-3">{section.content}</dd>
                  </div>
                ))}
            </dl>
          </section>

          <div className="flex flex-wrap gap-4 text-sm text-muted-foreground">
            <span>Total estimated tokens: {promptCompose.data.totalEstimatedTokens}</span>
            <span>Required sections: {promptCompose.data.requiredSectionCount}</span>
            <span>Optional sections: {promptCompose.data.optionalSectionCount}</span>
          </div>

          <div className="flex flex-wrap items-center gap-2">
            <button
              type="button"
              className={`rounded-md px-3 py-1.5 text-sm font-medium ${
                view === 'internal' ? 'bg-primary text-primary-foreground' : 'bg-muted text-muted-foreground'
              }`}
              onClick={() => setView('internal')}
            >
              Internal View
            </button>
            <button
              type="button"
              className={`rounded-md px-3 py-1.5 text-sm font-medium ${
                view === 'llm' ? 'bg-primary text-primary-foreground' : 'bg-muted text-muted-foreground'
              }`}
              onClick={() => setView('llm')}
            >
              LLM View
            </button>
          </div>

          {view === 'llm' ? (
            <section className="space-y-4">
              <h2 className="text-lg font-semibold">LLM View</h2>
              <p className="text-sm text-muted-foreground">
                Exact messages sent to the model after in-world semantic transformation.
              </p>
              {promptCompose.data.llmMessages.map((message, index) => (
                <article key={`llm-${index}`} className="rounded-lg border border-border bg-card p-4">
                  <div className="mb-2 text-sm font-semibold uppercase tracking-wide text-muted-foreground">
                    {message.role}
                  </div>
                  <pre className="whitespace-pre-wrap rounded-md bg-muted/40 p-3 text-sm">{message.content}</pre>
                </article>
              ))}
            </section>
          ) : (
            <>
              <section>
                <h2 className="mb-3 text-lg font-semibold">Internal View</h2>
                <p className="mb-4 text-sm text-muted-foreground">
                  Technical section names, fragment metadata, and token estimates.
                </p>
                <div className="overflow-x-auto rounded-lg border border-border">
                  <table className="min-w-full text-sm">
                    <thead className="bg-muted/50 text-left">
                      <tr>
                        <th className="px-3 py-2 font-medium">Priority</th>
                        <th className="px-3 py-2 font-medium">Title</th>
                        <th className="px-3 py-2 font-medium">Type</th>
                        <th className="px-3 py-2 font-medium">Fragment</th>
                        <th className="px-3 py-2 font-medium">Required</th>
                        <th className="px-3 py-2 font-medium">Tokens</th>
                      </tr>
                    </thead>
                    <tbody>
                      {promptCompose.data.sections.map((section) => (
                        <tr key={`${section.sectionType}-${section.priority}`} className="border-t border-border">
                          <td className="px-3 py-2">{section.priority}</td>
                          <td className="px-3 py-2 font-medium">{section.title}</td>
                          <td className="px-3 py-2 text-muted-foreground">{section.sectionType}</td>
                          <td className="px-3 py-2 font-mono text-xs text-muted-foreground">
                            {section.fragmentId || section.fragmentType || '—'}
                          </td>
                          <td className="px-3 py-2">{section.required ? 'Yes' : 'No'}</td>
                          <td className="px-3 py-2">{section.estimatedTokens}</td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              </section>

              <section className="space-y-4">
                <h2 className="text-lg font-semibold">Section Content</h2>
                {promptCompose.data.sections.map((section) => (
                  <article
                    key={`content-${section.sectionType}-${section.priority}`}
                    className="rounded-lg border border-border bg-card p-4"
                  >
                    <div className="mb-2 flex flex-wrap items-center gap-2 text-sm">
                      <span className="font-semibold">{section.title}</span>
                      <span className="text-muted-foreground">{section.sectionType}</span>
                      {section.fragmentId ? (
                        <span className="font-mono text-xs text-muted-foreground">{section.fragmentId}</span>
                      ) : null}
                      <span className="text-muted-foreground">{section.required ? 'required' : 'optional'}</span>
                      <span className="text-muted-foreground">tokens {section.estimatedTokens}</span>
                    </div>
                    <pre className="whitespace-pre-wrap rounded-md bg-muted/40 p-3 text-sm">{section.content}</pre>
                  </article>
                ))}
              </section>
            </>
          )}
        </div>
      ) : null}
    </div>
  )
}

export function LlmRequestPanel() {
  const [latestMessage, setLatestMessage] = useState('Where am I in the Jungle?')
  const llmGenerate = useLlmGenerate()

  function handleSubmit(event: React.FormEvent) {
    event.preventDefault()
    llmGenerate.mutate({ latestMessage })
  }

  return (
    <div className="space-y-6">
      <PageHeader
        title="LLM Request"
        description="Full pipeline through provider adapter and active LLM provider."
      />

      <MessageForm
        latestMessage={latestMessage}
        onChange={setLatestMessage}
        onSubmit={handleSubmit}
        isPending={llmGenerate.isPending}
        submitLabel="Generate Reply"
        pendingLabel="Generating…"
      />

      {llmGenerate.isError ? <ErrorState error={llmGenerate.error} title="Generation failed" /> : null}

      {llmGenerate.data ? (
        <div className="space-y-6">
          <section className="rounded-lg border border-border bg-card p-4">
            <h2 className="mb-3 text-lg font-semibold">Provider</h2>
            <dl className="grid gap-2 text-sm sm:grid-cols-2">
              <div>
                <dt className="text-muted-foreground">Type</dt>
                <dd className="font-medium">{llmGenerate.data.provider.type}</dd>
              </div>
              <div>
                <dt className="text-muted-foreground">Name</dt>
                <dd className="font-medium">{llmGenerate.data.provider.name}</dd>
              </div>
              <div>
                <dt className="text-muted-foreground">Model</dt>
                <dd className="font-medium">{llmGenerate.data.provider.model}</dd>
              </div>
              <div>
                <dt className="text-muted-foreground">Healthy</dt>
                <dd className="font-medium">{llmGenerate.data.provider.healthy ? 'Yes' : 'No'}</dd>
              </div>
            </dl>
            <p className="mt-3 text-sm text-muted-foreground">{llmGenerate.data.provider.description}</p>
          </section>

          <section>
            <h2 className="mb-3 text-lg font-semibold">Provider Request</h2>
            <div className="mb-4 flex flex-wrap gap-4 text-sm text-muted-foreground">
              <span>Model: {llmGenerate.data.request.model}</span>
              <span>Temperature: {llmGenerate.data.request.temperature}</span>
              <span>Max output tokens: {llmGenerate.data.request.maxOutputTokens}</span>
            </div>

            {Object.keys(llmGenerate.data.request.metadata).length > 0 ? (
              <div className="mb-4 rounded-md border border-border bg-muted/30 p-3 text-sm">
                <p className="mb-2 font-medium">Metadata</p>
                <dl className="grid gap-1 sm:grid-cols-2">
                  {Object.entries(llmGenerate.data.request.metadata).map(([key, value]) => (
                    <div key={key}>
                      <dt className="text-muted-foreground">{key}</dt>
                      <dd>{value}</dd>
                    </div>
                  ))}
                </dl>
              </div>
            ) : null}

            <div className="space-y-4">
              {llmGenerate.data.request.messages.map((message, index) => (
                <article key={`${message.role}-${index}`} className="rounded-lg border border-border bg-card p-4">
                  <div className="mb-2 flex flex-wrap items-center gap-2 text-sm">
                    <span className="font-semibold">{message.role}</span>
                    {message.sectionType ? (
                      <span className="text-muted-foreground">{message.sectionType}</span>
                    ) : null}
                  </div>
                  <pre className="whitespace-pre-wrap rounded-md bg-muted/40 p-3 text-sm">{message.content}</pre>
                </article>
              ))}
            </div>
          </section>

          <section className="rounded-lg border border-border bg-card p-4">
            <h2 className="mb-3 text-lg font-semibold">Provider Response</h2>
            <div className="mb-4 grid gap-2 text-sm sm:grid-cols-2">
              <div>
                <p className="text-muted-foreground">Active provider</p>
                <p className="font-medium">{llmGenerate.data.provider.type}</p>
              </div>
              <div>
                <p className="text-muted-foreground">API key index</p>
                <p className="font-medium">
                  {llmGenerate.data.response.providerMetadata.keyIndex ?? '—'}
                </p>
              </div>
              <div>
                <p className="text-muted-foreground">Model</p>
                <p className="font-medium">
                  {llmGenerate.data.response.providerMetadata.model ?? llmGenerate.data.provider.model}
                </p>
              </div>
              <div>
                <p className="text-muted-foreground">Latency</p>
                <p className="font-medium">{llmGenerate.data.response.latencyMs} ms</p>
              </div>
              <div>
                <p className="text-muted-foreground">Prompt tokens</p>
                <p className="font-medium">{llmGenerate.data.response.tokenUsage.promptTokens}</p>
              </div>
              <div>
                <p className="text-muted-foreground">Completion tokens</p>
                <p className="font-medium">{llmGenerate.data.response.tokenUsage.completionTokens}</p>
              </div>
              <div>
                <p className="text-muted-foreground">Finish reason</p>
                <p className="font-medium">{llmGenerate.data.response.finishReason}</p>
              </div>
              <div>
                <p className="text-muted-foreground">Retry count</p>
                <p className="font-medium">{llmGenerate.data.response.providerMetadata.retryCount ?? '0'}</p>
              </div>
            </div>
            <p className="mb-2 text-sm font-medium text-muted-foreground">Response preview</p>
            <pre className="whitespace-pre-wrap rounded-md bg-muted/40 p-3 text-sm">{llmGenerate.data.response.reply}</pre>
          </section>
        </div>
      ) : null}
    </div>
  )
}

export function ContextProfilePanel() {
  const [latestMessage, setLatestMessage] = useState('Where am I in the Jungle?')
  const promptProfile = usePromptProfile()

  function handleSubmit(event: React.FormEvent) {
    event.preventDefault()
    promptProfile.mutate({ latestMessage })
  }

  return (
    <div className="space-y-6">
      <PageHeader
        title="Context Profile"
        description="Intent-specific profile selection. Deterministic rules only."
      />

      <MessageForm
        latestMessage={latestMessage}
        onChange={setLatestMessage}
        onSubmit={handleSubmit}
        isPending={promptProfile.isPending}
        submitLabel="Select Profile"
        pendingLabel="Selecting…"
      />

      {promptProfile.isError ? <ErrorState error={promptProfile.error} title="Profile selection failed" /> : null}

      {promptProfile.data ? (
        <div className="space-y-6">
          <section className="rounded-lg border border-border bg-card p-4">
            <h2 className="mb-3 text-lg font-semibold">Selected Profile</h2>
            <dl className="grid gap-2 text-sm sm:grid-cols-2">
              <div>
                <dt className="text-muted-foreground">Type</dt>
                <dd className="font-medium">{promptProfile.data.profile.type}</dd>
              </div>
              <div>
                <dt className="text-muted-foreground">Name</dt>
                <dd className="font-medium">{promptProfile.data.profile.displayName}</dd>
              </div>
            </dl>
            <p className="mt-3 text-sm text-muted-foreground">{promptProfile.data.profile.description}</p>
            <p className="mt-3 text-sm">
              <span className="font-medium">Reason:</span> {promptProfile.data.selectionReason}
            </p>
          </section>

          <section>
            <h2 className="mb-3 text-lg font-semibold">Section Preferences</h2>
            <div className="grid gap-4 md:grid-cols-2">
              <div className="rounded-lg border border-border bg-card p-4">
                <h3 className="mb-2 font-medium">Preferred</h3>
                <ul className="space-y-1 text-sm text-muted-foreground">
                  {promptProfile.data.profile.preferredSections.map((section) => (
                    <li key={section}>{section}</li>
                  ))}
                </ul>
              </div>
              <div className="rounded-lg border border-border bg-card p-4">
                <h3 className="mb-2 font-medium">Reduced</h3>
                <ul className="space-y-1 text-sm text-muted-foreground">
                  {promptProfile.data.profile.reducedSections.length > 0 ? (
                    promptProfile.data.profile.reducedSections.map((section) => (
                      <li key={section}>{section}</li>
                    ))
                  ) : (
                    <li>None</li>
                  )}
                </ul>
              </div>
            </div>
          </section>
        </div>
      ) : null}
    </div>
  )
}

export function BudgetAllocationPanel() {
  const [latestMessage, setLatestMessage] = useState('Where am I in the Jungle?')
  const promptBudget = usePromptBudget()

  function handleSubmit(event: React.FormEvent) {
    event.preventDefault()
    promptBudget.mutate({ latestMessage })
  }

  return (
    <div className="space-y-6">
      <PageHeader
        title="Budget Allocation"
        description="Token budgets per section under the selected profile and provider capabilities."
      />

      <MessageForm
        latestMessage={latestMessage}
        onChange={setLatestMessage}
        onSubmit={handleSubmit}
        isPending={promptBudget.isPending}
        submitLabel="Allocate Budget"
        pendingLabel="Allocating…"
      />

      {promptBudget.isError ? <ErrorState error={promptBudget.error} title="Budget allocation failed" /> : null}

      {promptBudget.data ? (
        <div className="space-y-6">
          <div className="flex flex-wrap gap-4 text-sm text-muted-foreground">
            <span>Profile: {promptBudget.data.profile.type}</span>
            <span>Total prompt tokens: {promptBudget.data.totalPromptTokens}</span>
            <span>Remaining budget: {promptBudget.data.remainingBudget}</span>
            <span>Available: {promptBudget.data.budget.totalAvailableTokens}</span>
          </div>

          <section>
            <h2 className="mb-3 text-lg font-semibold">Section Budgets</h2>
            <div className="overflow-x-auto rounded-lg border border-border">
              <table className="min-w-full text-sm">
                <thead className="bg-muted/50 text-left">
                  <tr>
                    <th className="px-3 py-2 font-medium">Section</th>
                    <th className="px-3 py-2 font-medium">Max</th>
                    <th className="px-3 py-2 font-medium">Min</th>
                    <th className="px-3 py-2 font-medium">Allocated</th>
                    <th className="px-3 py-2 font-medium">Required</th>
                    <th className="px-3 py-2 font-medium">Truncated</th>
                  </tr>
                </thead>
                <tbody>
                  {promptBudget.data.sections.map((entry) => (
                    <tr key={entry.section.sectionType} className="border-t border-border">
                      <td className="px-3 py-2 font-medium">{entry.section.sectionType}</td>
                      <td className="px-3 py-2">{entry.budget.maxTokens}</td>
                      <td className="px-3 py-2">{entry.budget.minimumTokens}</td>
                      <td className="px-3 py-2">{entry.allocatedTokens}</td>
                      <td className="px-3 py-2">{entry.budget.required ? 'Yes' : 'No'}</td>
                      <td className="px-3 py-2">{entry.truncated ? 'Yes' : 'No'}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </section>

          {promptBudget.data.droppedSections.length > 0 ? (
            <section>
              <h2 className="mb-3 text-lg font-semibold">Dropped Sections</h2>
              <ul className="space-y-2">
                {promptBudget.data.droppedSections.map((section) => (
                  <li key={section.sectionType} className="rounded-md border border-border bg-card px-3 py-2 text-sm">
                    <p className="font-medium">{section.title} ({section.sectionType})</p>
                    <p className="text-muted-foreground">{section.reason}</p>
                  </li>
                ))}
              </ul>
            </section>
          ) : null}
        </div>
      ) : null}
    </div>
  )
}

export function IdentityDiagnosticsPanel() {
  const [latestMessage, setLatestMessage] = useState('Who am I?')
  const promptProfile = usePromptProfile()
  const contextResolve = useContextResolve()
  const promptCompose = usePromptCompose()

  function handleSubmit(event: React.FormEvent) {
    event.preventDefault()
    const request = { latestMessage }
    promptProfile.mutate(request)
    contextResolve.mutate(request)
    promptCompose.mutate(request)
  }

  const isPending = promptProfile.isPending || contextResolve.isPending || promptCompose.isPending
  const isError = promptProfile.isError || contextResolve.isError || promptCompose.isError
  const error = promptProfile.error ?? contextResolve.error ?? promptCompose.error

  return (
    <div className="space-y-6">
      <PageHeader
        title="Identity Diagnostics"
        description="Profile selection, current user semantics, relationship knowledge, fragments, and prompt sections."
      />

      <MessageForm
        latestMessage={latestMessage}
        onChange={setLatestMessage}
        onSubmit={handleSubmit}
        isPending={isPending}
        submitLabel="Run Diagnostics"
        pendingLabel="Running…"
      />

      {isError ? <ErrorState error={error} title="Diagnostics failed" /> : null}

      {promptProfile.data ? (
        <section className="rounded-lg border border-border bg-card p-4">
          <h2 className="mb-3 text-lg font-semibold">Selected Context Profile</h2>
          <dl className="grid gap-2 text-sm sm:grid-cols-2">
            <div>
              <dt className="text-muted-foreground">Type</dt>
              <dd className="font-medium">{promptProfile.data.profile.type}</dd>
            </div>
            <div>
              <dt className="text-muted-foreground">Name</dt>
              <dd className="font-medium">{promptProfile.data.profile.displayName}</dd>
            </div>
          </dl>
          <p className="mt-3 text-sm">
            <span className="font-medium">Reason:</span> {promptProfile.data.selectionReason}
          </p>
        </section>
      ) : null}

      {promptCompose.data ? (
        <section className="rounded-lg border border-border bg-card p-4">
          <h2 className="mb-3 text-lg font-semibold">Current User & Relationship</h2>
          <dl className="space-y-4 text-sm">
            {promptCompose.data.sections
              .filter((section) => section.sectionType === 'CURRENT_USER')
              .map((section) => (
                <div key={section.sectionType}>
                  <dt className="text-muted-foreground">Current User</dt>
                  <dd className="mt-1 whitespace-pre-wrap rounded-md bg-muted/40 p-3">{section.content}</dd>
                </div>
              ))}
            {promptCompose.data.sections
              .filter((section) => section.sectionType === 'RELATIONSHIP_TO_BANDAR')
              .map((section) => (
                <div key={section.sectionType}>
                  <dt className="text-muted-foreground">Relationship To Bandar</dt>
                  <dd className="mt-1 whitespace-pre-wrap rounded-md bg-muted/40 p-3">{section.content}</dd>
                </div>
              ))}
          </dl>
        </section>
      ) : null}

      {contextResolve.data ? (
        <section>
          <h2 className="mb-3 text-lg font-semibold">Knowledge Fragments</h2>
          <div className="space-y-3">
            {contextResolve.data.fragments.map((fragment) => (
              <article key={fragment.fragmentId} className="rounded-lg border border-border bg-card p-4 text-sm">
                <p className="font-semibold">{fragment.title}</p>
                <p className="text-muted-foreground">{fragment.fragmentType}</p>
              </article>
            ))}
          </div>
        </section>
      ) : null}

      {promptCompose.data ? (
        <section>
          <h2 className="mb-3 text-lg font-semibold">Prompt Sections</h2>
          <ul className="space-y-2 text-sm">
            {promptCompose.data.sections.map((section) => (
              <li key={`${section.sectionType}-${section.priority}`} className="rounded-md border border-border bg-card px-3 py-2">
                <span className="font-medium">{section.title}</span>
                <span className="text-muted-foreground"> — {section.sectionType}</span>
              </li>
            ))}
          </ul>
        </section>
      ) : null}
    </div>
  )
}

function WorkingMemoryPanel() {
  const workingMemory = useWorkingMemory()
  const rebuild = useRebuildWorkingMemory()

  return (
    <div className="space-y-6">
      <PageHeader
        title="Working Memory"
        description="Session-scoped cognitive state rebuilt deterministically from the active conversation."
      />

      <div className="flex gap-2">
        <Button type="button" variant="outline" onClick={() => workingMemory.refetch()} disabled={workingMemory.isFetching}>
          Refresh
        </Button>
        <Button type="button" onClick={() => rebuild.mutate()} disabled={rebuild.isPending}>
          {rebuild.isPending ? 'Rebuilding…' : 'Rebuild'}
        </Button>
      </div>

      {workingMemory.isError ? <ErrorState error={workingMemory.error} title="Working memory unavailable" /> : null}

      {workingMemory.data ? (
        <>
          <section className="rounded-lg border border-border bg-card p-4">
            <h2 className="mb-3 text-lg font-semibold">Current State</h2>
            <dl className="grid gap-3 text-sm sm:grid-cols-2">
              <div>
                <dt className="text-muted-foreground">Active Topic</dt>
                <dd className="font-medium">{workingMemory.data.activeTopic}</dd>
              </div>
              <div>
                <dt className="text-muted-foreground">Mood</dt>
                <dd className="font-medium">{workingMemory.data.conversationMood}</dd>
              </div>
              <div>
                <dt className="text-muted-foreground">Current Story</dt>
                <dd className="font-medium">{workingMemory.data.currentStory || '(none)'}</dd>
              </div>
              <div>
                <dt className="text-muted-foreground">Last Updated</dt>
                <dd className="font-medium">{workingMemory.data.lastUpdated}</dd>
              </div>
              <div>
                <dt className="text-muted-foreground">Version</dt>
                <dd className="font-medium">{workingMemory.data.version}</dd>
              </div>
            </dl>
          </section>

          <section className="rounded-lg border border-border bg-card p-4">
            <h2 className="mb-3 text-lg font-semibold">Lists</h2>
            <dl className="space-y-4 text-sm">
              {[
                ['Active Entities', workingMemory.data.activeEntities],
                ['Unanswered Questions', workingMemory.data.unansweredQuestions],
                ['Recent Promises', workingMemory.data.recentPromises],
                ['Important Facts', workingMemory.data.importantFacts],
              ].map(([label, items]) => (
                <div key={label as string}>
                  <dt className="text-muted-foreground">{label as string}</dt>
                  <dd className="mt-1">
                    {(items as string[]).length === 0 ? (
                      <span className="text-muted-foreground">(none)</span>
                    ) : (
                      <ul className="list-disc space-y-1 pl-5">
                        {(items as string[]).map((item) => (
                          <li key={item}>{item}</li>
                        ))}
                      </ul>
                    )}
                  </dd>
                </div>
              ))}
            </dl>
          </section>

          <section className="rounded-lg border border-border bg-card p-4">
            <h2 className="mb-3 text-lg font-semibold">Heuristic Trace</h2>
            <div className="space-y-3 text-sm">
              {workingMemory.data.fieldTraces.map((trace) => (
                <article key={trace.field} className="rounded-md border border-border bg-muted/20 p-3">
                  <p className="font-semibold">{trace.field}</p>
                  <p className="mt-1 whitespace-pre-wrap">{trace.value}</p>
                  <p className="mt-2 text-muted-foreground">{trace.heuristic}</p>
                </article>
              ))}
            </div>
          </section>
        </>
      ) : null}
    </div>
  )
}

function ConversationDirectorPanel() {
  const plan = useConversationDirectorPlan()

  return (
    <div className="space-y-6">
      <PageHeader
        title="Conversation Director"
        description="Deterministic executive planning for how Bandar should respond next."
      />

      <div className="flex gap-2">
        <Button type="button" variant="outline" onClick={() => plan.refetch()} disabled={plan.isFetching}>
          Refresh
        </Button>
      </div>

      {plan.isError ? (
        <ErrorState
          error={plan.error}
          title="No conversation plan yet — send a message in chat first"
        />
      ) : null}

      {plan.data ? (
        <>
          <section className="rounded-lg border border-border bg-card p-4">
            <h2 className="mb-3 text-lg font-semibold">Current Plan</h2>
            <dl className="grid gap-3 text-sm sm:grid-cols-2">
              <div>
                <dt className="text-muted-foreground">Current Goal</dt>
                <dd className="font-medium">{plan.data.goal}</dd>
              </div>
              <div>
                <dt className="text-muted-foreground">Confidence</dt>
                <dd className="font-medium">{plan.data.confidence.toFixed(2)}</dd>
              </div>
              <div>
                <dt className="text-muted-foreground">Continue Conversation</dt>
                <dd className="font-medium">{plan.data.continueConversation ? 'Yes' : 'No'}</dd>
              </div>
              <div>
                <dt className="text-muted-foreground">Conversation Energy</dt>
                <dd className="font-medium">{plan.data.conversationEnergy}</dd>
              </div>
              <div>
                <dt className="text-muted-foreground">Conversation Arc</dt>
                <dd className="font-medium">{plan.data.conversationArc}</dd>
              </div>
              <div>
                <dt className="text-muted-foreground">Derived Message Count</dt>
                <dd className="font-medium">{plan.data.expectedMessageCount}</dd>
              </div>
              <div>
                <dt className="text-muted-foreground">Tone</dt>
                <dd className="font-medium">{plan.data.suggestedTone || '(none)'}</dd>
              </div>
              <div>
                <dt className="text-muted-foreground">Outcome</dt>
                <dd className="font-medium">{plan.data.outcome}</dd>
              </div>
              <div>
                <dt className="text-muted-foreground">Delays (ms)</dt>
                <dd className="font-medium">
                  {plan.data.delays.length === 0 ? '(none)' : plan.data.delays.join(', ')}
                </dd>
              </div>
              <div>
                <dt className="text-muted-foreground">Created At</dt>
                <dd className="font-medium">{plan.data.createdAt}</dd>
              </div>
            </dl>
            <dl className="mt-4 grid gap-3 text-sm sm:grid-cols-2">
              <div>
                <dt className="text-muted-foreground">Ask Follow-up Question</dt>
                <dd className="font-medium">{plan.data.askFollowUpQuestion ? 'Yes' : 'No'}</dd>
              </div>
              <div>
                <dt className="text-muted-foreground">Tell Story</dt>
                <dd className="font-medium">{plan.data.tellStory ? 'Yes' : 'No'}</dd>
              </div>
              <div>
                <dt className="text-muted-foreground">Tell Joke</dt>
                <dd className="font-medium">{plan.data.tellJoke ? 'Yes' : 'No'}</dd>
              </div>
              <div>
                <dt className="text-muted-foreground">End Conversation</dt>
                <dd className="font-medium">{plan.data.endConversation ? 'Yes' : 'No'}</dd>
              </div>
            </dl>
          </section>

          <section className="rounded-lg border border-border bg-card p-4">
            <h2 className="mb-3 text-lg font-semibold">Planning Trace</h2>
            <div className="space-y-3 text-sm">
              {plan.data.trace.map((entry) => (
                <article key={entry.rule} className="rounded-md border border-border bg-muted/20 p-3">
                  <p className="font-semibold">{entry.rule}</p>
                  <p className="mt-2 text-muted-foreground">{entry.reason}</p>
                </article>
              ))}
            </div>
          </section>

          <section className="rounded-lg border border-border bg-card p-4">
            <h2 className="mb-3 text-lg font-semibold">Execution Timeline</h2>
            {plan.data.timeline.length === 0 ? (
              <p className="text-sm text-muted-foreground">No execution events yet.</p>
            ) : (
              <div className="space-y-3 text-sm">
                {plan.data.timeline.map((entry, index) => (
                  <article
                    key={`${entry.event}-${entry.replyIndex}-${index}`}
                    className="rounded-md border border-border bg-muted/20 p-3"
                  >
                    <p className="font-semibold">
                      Reply {entry.replyIndex + 1}: {entry.event}
                    </p>
                    <p className="mt-1 text-muted-foreground">At: {entry.at}</p>
                    {entry.delayMs > 0 ? (
                      <p className="mt-1 text-muted-foreground">Delay: {entry.delayMs} ms</p>
                    ) : null}
                  </article>
                ))}
              </div>
            )}
          </section>

          <section className="rounded-lg border border-border bg-card p-4">
            <h2 className="mb-3 text-lg font-semibold">Executed Plan</h2>
            <dl className="grid gap-3 text-sm sm:grid-cols-2">
              <div>
                <dt className="text-muted-foreground">Executed</dt>
                <dd className="font-medium">{plan.data.executed ? 'Yes' : 'No'}</dd>
              </div>
              <div>
                <dt className="text-muted-foreground">Delivered Messages</dt>
                <dd className="font-medium">{plan.data.executedMessageCount}</dd>
              </div>
              <div>
                <dt className="text-muted-foreground">Cancelled Messages</dt>
                <dd className="font-medium">{plan.data.cancelledMessageCount}</dd>
              </div>
              <div>
                <dt className="text-muted-foreground">Interrupted</dt>
                <dd className="font-medium">{plan.data.isInterrupted ? 'Yes' : 'No'}</dd>
              </div>
              <div>
                <dt className="text-muted-foreground">Interruption Reason</dt>
                <dd className="font-medium">{plan.data.interruptionReason || '(none)'}</dd>
              </div>
              <div>
                <dt className="text-muted-foreground">Started At</dt>
                <dd className="font-medium">{plan.data.startedAt ?? '(none)'}</dd>
              </div>
              <div>
                <dt className="text-muted-foreground">Completed At</dt>
                <dd className="font-medium">{plan.data.completedAt ?? '(none)'}</dd>
              </div>
              <div>
                <dt className="text-muted-foreground">Delivered Message IDs</dt>
                <dd className="font-medium break-all">
                  {plan.data.deliveredMessageIds.length === 0
                    ? '(none)'
                    : plan.data.deliveredMessageIds.join(', ')}
                </dd>
              </div>
              <div>
                <dt className="text-muted-foreground">Cancelled Reply Indexes</dt>
                <dd className="font-medium">
                  {plan.data.cancelledReplyIndexes.length === 0
                    ? '(none)'
                    : plan.data.cancelledReplyIndexes.join(', ')}
                </dd>
              </div>
            </dl>
          </section>
        </>
      ) : null}
    </div>
  )
}

function BehaviorEnginePanel() {
  const profile = useBehaviorEngineProfile()

  return (
    <div className="space-y-6">
      <PageHeader
        title="Behavior Engine"
        description="Deterministic style selection for how Bandar expresses himself in this conversation."
      />

      <div className="flex gap-2">
        <Button type="button" variant="outline" onClick={() => profile.refetch()} disabled={profile.isFetching}>
          Refresh
        </Button>
      </div>

      {profile.isError ? (
        <ErrorState
          error={profile.error}
          title="No behavior profile yet — send a message in chat first"
        />
      ) : null}

      {profile.data ? (
        <>
          <section className="rounded-lg border border-border bg-card p-4">
            <h2 className="mb-3 text-lg font-semibold">Current Behavior</h2>
            <dl className="grid gap-3 text-sm sm:grid-cols-2">
              <div>
                <dt className="text-muted-foreground">Conversation Flavor</dt>
                <dd className="font-medium">{profile.data.conversationFlavor}</dd>
              </div>
              <div>
                <dt className="text-muted-foreground">Opening Style</dt>
                <dd className="font-medium">{profile.data.openingStyle}</dd>
              </div>
              <div>
                <dt className="text-muted-foreground">Narration Style</dt>
                <dd className="font-medium">{profile.data.narrationStyle}</dd>
              </div>
              <div>
                <dt className="text-muted-foreground">Humor Level</dt>
                <dd className="font-medium">{profile.data.humorLevel}</dd>
              </div>
              <div>
                <dt className="text-muted-foreground">Curiosity Level</dt>
                <dd className="font-medium">{profile.data.curiosityLevel}</dd>
              </div>
              <div>
                <dt className="text-muted-foreground">Ending Style</dt>
                <dd className="font-medium">{profile.data.endingStyle}</dd>
              </div>
              <div>
                <dt className="text-muted-foreground">Energy Modifier</dt>
                <dd className="font-medium">{profile.data.energyModifier}</dd>
              </div>
              <div>
                <dt className="text-muted-foreground">Storytelling Preference</dt>
                <dd className="font-medium">{profile.data.storytellingPreference}</dd>
              </div>
              <div>
                <dt className="text-muted-foreground">Created At</dt>
                <dd className="font-medium">{profile.data.createdAt}</dd>
              </div>
            </dl>
          </section>

          <section className="rounded-lg border border-border bg-card p-4">
            <h2 className="mb-3 text-lg font-semibold">Planning Trace</h2>
            <div className="space-y-3 text-sm">
              {profile.data.trace.map((entry) => (
                <article key={entry.rule} className="rounded-md border border-border bg-muted/20 p-3">
                  <p className="font-semibold">{entry.rule}</p>
                  <p className="mt-2 text-muted-foreground">{entry.reason}</p>
                </article>
              ))}
            </div>
          </section>

          <section className="rounded-lg border border-border bg-card p-4">
            <h2 className="mb-3 text-lg font-semibold">Behavior Rules Applied</h2>
            <p className="text-sm text-muted-foreground">
              {profile.data.trace.length === 0
                ? 'No rules recorded.'
                : `${profile.data.trace.length} rule(s) contributed to this profile.`}
            </p>
            <ul className="mt-3 list-disc space-y-1 pl-5 text-sm">
              {profile.data.trace.map((entry) => (
                <li key={`rule-${entry.rule}`}>{entry.rule}</li>
              ))}
            </ul>
          </section>
        </>
      ) : null}
    </div>
  )
}

function LivingNotificationsPanel() {
  const generation = useNotificationGenerationTrace()
  const allNotifications = useAllNotificationsDev()

  return (
    <div className="space-y-6">
      <PageHeader
        title="Living Notifications"
        description="Deterministic notification generation and lifecycle for the current character."
      />

      <div className="flex gap-2">
        <Button type="button" variant="outline" onClick={() => generation.refetch()} disabled={generation.isFetching}>
          Refresh Generation
        </Button>
        <Button
          type="button"
          variant="outline"
          onClick={() => allNotifications.refetch()}
          disabled={allNotifications.isFetching}
        >
          Refresh All Notifications
        </Button>
      </div>

      {generation.isError ? (
        <ErrorState
          error={generation.error}
          title="No generation trace yet — log in again to trigger notification generation"
        />
      ) : null}

      {generation.data ? (
        <>
          <section className="rounded-lg border border-border bg-card p-4">
            <h2 className="mb-3 text-lg font-semibold">Latest Generation</h2>
            <dl className="grid gap-3 text-sm sm:grid-cols-2">
              <div>
                <dt className="text-muted-foreground">Character</dt>
                <dd className="font-medium">{generation.data.characterId}</dd>
              </div>
              <div>
                <dt className="text-muted-foreground">Generated At</dt>
                <dd className="font-medium">{generation.data.generatedAt}</dd>
              </div>
              <div>
                <dt className="text-muted-foreground">Notifications Created</dt>
                <dd className="font-medium">{generation.data.generatedNotifications.length}</dd>
              </div>
            </dl>
          </section>

          <section className="rounded-lg border border-border bg-card p-4">
            <h2 className="mb-3 text-lg font-semibold">Rules Applied</h2>
            <div className="space-y-3 text-sm">
              {generation.data.trace.map((entry) => (
                <article key={entry.rule} className="rounded-md border border-border bg-muted/20 p-3">
                  <p className="font-semibold">{entry.rule}</p>
                  <p className="mt-2 text-muted-foreground">{entry.reason}</p>
                </article>
              ))}
            </div>
          </section>

          <section className="rounded-lg border border-border bg-card p-4">
            <h2 className="mb-3 text-lg font-semibold">Generated Notifications</h2>
            <div className="space-y-3 text-sm">
              {generation.data.generatedNotifications.map((notification) => (
                <article key={notification.id} className="rounded-md border border-border bg-muted/20 p-3">
                  <p className="font-semibold">
                    {notification.type} · {notification.priority}
                  </p>
                  <p className="mt-1 text-muted-foreground">Trigger: {notification.trigger}</p>
                  <p className="mt-1 text-muted-foreground">Status: {notification.status}</p>
                  <p className="mt-1 text-muted-foreground">Expires: {notification.expiresAt}</p>
                  <p className="mt-2 whitespace-pre-wrap">{notification.summary}</p>
                </article>
              ))}
            </div>
          </section>
        </>
      ) : null}

      {allNotifications.data ? (
        <section className="rounded-lg border border-border bg-card p-4">
          <h2 className="mb-3 text-lg font-semibold">Delivery History</h2>
          <div className="space-y-3 text-sm">
            {allNotifications.data.map((notification) => (
              <article key={`history-${notification.id}`} className="rounded-md border border-border bg-muted/20 p-3">
                <p className="font-semibold">{notification.title}</p>
                <p className="mt-1 text-muted-foreground">
                  {notification.type} · {notification.status} · {notification.createdAt}
                </p>
              </article>
            ))}
          </div>
        </section>
      ) : null}
    </div>
  )
}

function ConversationArtifactsPanel() {
  const generation = useArtifactGenerationTrace()
  const allArtifacts = useAllArtifactsDev()

  return (
    <div className="space-y-6">
      <PageHeader
        title="Conversation Artifacts"
        description="Deterministic artifact creation, ownership, and lifecycle for the current character."
      />

      <div className="flex gap-2">
        <Button type="button" variant="outline" onClick={() => generation.refetch()} disabled={generation.isFetching}>
          Refresh Generation
        </Button>
        <Button type="button" variant="outline" onClick={() => allArtifacts.refetch()} disabled={allArtifacts.isFetching}>
          Refresh All Artifacts
        </Button>
      </div>

      {generation.isError ? (
        <ErrorState
          error={generation.error}
          title="No generation trace yet — send a chat message to trigger artifact creation"
        />
      ) : null}

      {generation.data ? (
        <>
          <section className="rounded-lg border border-border bg-card p-4">
            <h2 className="mb-3 text-lg font-semibold">Latest Generation</h2>
            <dl className="grid gap-3 text-sm sm:grid-cols-2">
              <div>
                <dt className="text-muted-foreground">Character</dt>
                <dd className="font-medium">{generation.data.characterId}</dd>
              </div>
              <div>
                <dt className="text-muted-foreground">Conversation</dt>
                <dd className="font-medium">{generation.data.conversationId}</dd>
              </div>
              <div>
                <dt className="text-muted-foreground">Generated At</dt>
                <dd className="font-medium">{generation.data.generatedAt}</dd>
              </div>
              <div>
                <dt className="text-muted-foreground">Artifacts Created</dt>
                <dd className="font-medium">{generation.data.generatedArtifacts.length}</dd>
              </div>
            </dl>
          </section>

          <section className="rounded-lg border border-border bg-card p-4">
            <h2 className="mb-3 text-lg font-semibold">Creation Rules</h2>
            <div className="space-y-3 text-sm">
              {generation.data.trace.map((entry) => (
                <article key={entry.rule} className="rounded-md border border-border bg-muted/20 p-3">
                  <p className="font-semibold">{entry.rule}</p>
                  <p className="mt-2 text-muted-foreground">{entry.reason}</p>
                </article>
              ))}
            </div>
          </section>

          <section className="rounded-lg border border-border bg-card p-4">
            <h2 className="mb-3 text-lg font-semibold">Generated Artifacts</h2>
            <div className="space-y-3 text-sm">
              {generation.data.generatedArtifacts.map((artifact) => (
                <article key={artifact.id} className="rounded-md border border-border bg-muted/20 p-3">
                  <p className="font-semibold">
                    {artifact.type} · {artifact.priority}
                  </p>
                  <p className="mt-1 text-muted-foreground">
                    Owner: {artifact.ownerCharacterId} → Recipient: {artifact.recipientCharacterId}
                  </p>
                  <p className="mt-1 text-muted-foreground">Rule: {artifact.metadata.trigger ?? 'n/a'}</p>
                  <p className="mt-1 text-muted-foreground">Status: {artifact.status}</p>
                  <p className="mt-1 text-muted-foreground">Expires: {artifact.expiresAt}</p>
                  <p className="mt-2">{artifact.summary}</p>
                  <p className="mt-2 text-muted-foreground">Trace: {artifact.trace.join(' → ')}</p>
                </article>
              ))}
            </div>
          </section>
        </>
      ) : null}

      {allArtifacts.data ? (
        <section className="rounded-lg border border-border bg-card p-4">
          <h2 className="mb-3 text-lg font-semibold">All Artifacts</h2>
          <div className="space-y-3 text-sm">
            {allArtifacts.data.map((artifact) => (
              <article key={`all-${artifact.id}`} className="rounded-md border border-border bg-muted/20 p-3">
                <p className="font-semibold">{artifact.title}</p>
                <p className="mt-1 text-muted-foreground">
                  {artifact.type} · {artifact.status} · {artifact.createdAt}
                </p>
                <p className="mt-1 text-muted-foreground">Conversation: {artifact.conversationId}</p>
              </article>
            ))}
          </div>
        </section>
      ) : null}
    </div>
  )
}

function CognitiveAnalysisPanel() {
  const execution = useCognitiveAnalysisExecution()
  const observations = useCognitiveObservations()
  const recommendations = useCognitiveRecommendations()
  const allAnalyses = useAllCognitiveAnalysesDev()

  return (
    <div className="space-y-6">
      <PageHeader
        title="Cognitive Analysis"
        description="Asynchronous semantic analysis of completed conversations — observations and recommendations only."
      />

      <div className="flex gap-2">
        <Button type="button" variant="outline" onClick={() => execution.refetch()} disabled={execution.isFetching}>
          Refresh Execution
        </Button>
        <Button type="button" variant="outline" onClick={() => observations.refetch()} disabled={observations.isFetching}>
          Refresh Observations
        </Button>
        <Button
          type="button"
          variant="outline"
          onClick={() => recommendations.refetch()}
          disabled={recommendations.isFetching}
        >
          Refresh Recommendations
        </Button>
      </div>

      {execution.isError ? (
        <ErrorState
          error={execution.error}
          title="No analysis execution yet — send a chat message to trigger cognitive analysis"
        />
      ) : null}

      {execution.data ? (
        <>
          <section className="rounded-lg border border-border bg-card p-4">
            <h2 className="mb-3 text-lg font-semibold">Latest Execution</h2>
            <dl className="grid gap-3 text-sm sm:grid-cols-2">
              <div>
                <dt className="text-muted-foreground">Provider</dt>
                <dd className="font-medium">{execution.data.provider}</dd>
              </div>
              <div>
                <dt className="text-muted-foreground">Model</dt>
                <dd className="font-medium">{execution.data.model || 'n/a'}</dd>
              </div>
              <div>
                <dt className="text-muted-foreground">Provider Latency</dt>
                <dd className="font-medium">{execution.data.providerLatencyMs} ms</dd>
              </div>
              <div>
                <dt className="text-muted-foreground">Execution Time</dt>
                <dd className="font-medium">{execution.data.executionTimeMs} ms</dd>
              </div>
              <div>
                <dt className="text-muted-foreground">Confidence</dt>
                <dd className="font-medium">{execution.data.confidence}</dd>
              </div>
              <div>
                <dt className="text-muted-foreground">Success</dt>
                <dd className="font-medium">{execution.data.success ? 'Yes' : 'No'}</dd>
              </div>
            </dl>
            {execution.data.errorMessage ? (
              <p className="mt-3 text-sm text-destructive">{execution.data.errorMessage}</p>
            ) : null}
          </section>

          {execution.data.result ? (
            <>
              <section className="rounded-lg border border-border bg-card p-4">
                <h2 className="mb-3 text-lg font-semibold">Observations</h2>
                <div className="space-y-3 text-sm">
                  {execution.data.result.observations.map((observation) => (
                    <article key={observation.id} className="rounded-md border border-border bg-muted/20 p-3">
                      <p className="font-semibold">
                        {observation.type} · {observation.confidence}
                      </p>
                      <p className="mt-2">{observation.summary}</p>
                      <p className="mt-2 text-muted-foreground">Evidence: {observation.evidence}</p>
                    </article>
                  ))}
                </div>
              </section>

              <section className="rounded-lg border border-border bg-card p-4">
                <h2 className="mb-3 text-lg font-semibold">Recommendations</h2>
                <div className="space-y-3 text-sm">
                  {execution.data.result.recommendations.map((recommendation) => (
                    <article key={recommendation.id} className="rounded-md border border-border bg-muted/20 p-3">
                      <p className="font-semibold">
                        {recommendation.action} · {recommendation.confidence}
                      </p>
                      <p className="mt-2">{recommendation.reason}</p>
                      <p className="mt-2 text-muted-foreground">Target: {recommendation.target}</p>
                    </article>
                  ))}
                </div>
              </section>

              <section className="rounded-lg border border-border bg-card p-4">
                <h2 className="mb-3 text-lg font-semibold">Raw JSON</h2>
                <pre className="overflow-x-auto rounded-md bg-muted/20 p-3 text-xs">{execution.data.result.rawJson}</pre>
              </section>
            </>
          ) : null}
        </>
      ) : null}

      {allAnalyses.data ? (
        <section className="rounded-lg border border-border bg-card p-4">
          <h2 className="mb-3 text-lg font-semibold">Stored Analyses</h2>
          <div className="space-y-3 text-sm">
            {allAnalyses.data.map((analysis) => (
              <article key={analysis.analysisId} className="rounded-md border border-border bg-muted/20 p-3">
                <p className="font-semibold">{analysis.conversationId}</p>
                <p className="mt-1 text-muted-foreground">
                  {analysis.provider} · {analysis.confidence} · {analysis.createdAt}
                </p>
              </article>
            ))}
          </div>
        </section>
      ) : null}
    </div>
  )
}

function MemoryInboxPanel() {
  const generation = useMemoryInboxGenerationTrace()
  const allItems = useAllMemoryInboxDev()

  return (
    <div className="space-y-6">
      <PageHeader
        title="Memory Inbox"
        description="Deterministic inbox ingestion, deduplication, and lifecycle for the current character."
      />

      <div className="flex gap-2">
        <Button type="button" variant="outline" onClick={() => generation.refetch()} disabled={generation.isFetching}>
          Refresh Generation
        </Button>
        <Button type="button" variant="outline" onClick={() => allItems.refetch()} disabled={allItems.isFetching}>
          Refresh All Items
        </Button>
      </div>

      {generation.isError ? (
        <ErrorState
          error={generation.error}
          title="No generation trace yet — send a chat message to trigger inbox ingestion"
        />
      ) : null}

      {generation.data ? (
        <>
          <section className="rounded-lg border border-border bg-card p-4">
            <h2 className="mb-3 text-lg font-semibold">Latest Generation</h2>
            <dl className="grid gap-3 text-sm sm:grid-cols-2">
              <div>
                <dt className="text-muted-foreground">Character</dt>
                <dd className="font-medium">{generation.data.characterId}</dd>
              </div>
              <div>
                <dt className="text-muted-foreground">Conversation</dt>
                <dd className="font-medium">{generation.data.conversationId}</dd>
              </div>
              <div>
                <dt className="text-muted-foreground">Generated At</dt>
                <dd className="font-medium">{generation.data.generatedAt}</dd>
              </div>
              <div>
                <dt className="text-muted-foreground">Items Generated</dt>
                <dd className="font-medium">{generation.data.generatedItems.length}</dd>
              </div>
            </dl>
          </section>

          <section className="rounded-lg border border-border bg-card p-4">
            <h2 className="mb-3 text-lg font-semibold">Generation Rules</h2>
            <div className="space-y-3 text-sm">
              {generation.data.trace.map((entry, index) => (
                <article key={`${entry.rule}-${index}`} className="rounded-md border border-border bg-muted/20 p-3">
                  <p className="font-semibold">{entry.rule}</p>
                  <p className="mt-2 text-muted-foreground">{entry.reason}</p>
                </article>
              ))}
            </div>
          </section>

          <section className="rounded-lg border border-border bg-card p-4">
            <h2 className="mb-3 text-lg font-semibold">Generated Items</h2>
            <div className="space-y-3 text-sm">
              {generation.data.generatedItems.map((item) => (
                <article key={item.id} className="rounded-md border border-border bg-muted/20 p-3">
                  <p className="font-semibold">
                    {item.type} · {item.importance} · {item.confidence}
                  </p>
                  <p className="mt-1 text-muted-foreground">
                    Source: {item.source} · Status: {item.status}
                  </p>
                  <p className="mt-1 text-muted-foreground">Evidence: {item.metadata.evidence ?? item.summary}</p>
                  <p className="mt-1 text-muted-foreground">
                    Artifacts: {item.artifactIds.length > 0 ? item.artifactIds.join(', ') : 'none'}
                  </p>
                  <p className="mt-1 text-muted-foreground">
                    Analysis: {item.analysisId || 'none'}
                  </p>
                  <p className="mt-2">{item.summary}</p>
                  <p className="mt-2 text-muted-foreground">Trace: {item.trace.join(' → ')}</p>
                </article>
              ))}
            </div>
          </section>
        </>
      ) : null}

      {allItems.data ? (
        <section className="rounded-lg border border-border bg-card p-4">
          <h2 className="mb-3 text-lg font-semibold">Lifecycle History</h2>
          <div className="space-y-3 text-sm">
            {allItems.data.map((item) => (
              <article key={`all-${item.id}`} className="rounded-md border border-border bg-muted/20 p-3">
                <p className="font-semibold">{item.summary}</p>
                <p className="mt-1 text-muted-foreground">
                  {item.source} · {item.status} · {item.createdAt}
                </p>
                <p className="mt-1 text-muted-foreground">
                  Expires: {item.expiresAt} · Confidence: {item.confidence}
                </p>
                <p className="mt-1 text-muted-foreground">Trace: {item.trace.join(' → ')}</p>
              </article>
            ))}
          </div>
        </section>
      ) : null}
    </div>
  )
}

function MemoryConsolidationPanel() {
  const execution = useConsolidationExecutionDev()
  const candidates = useAllCandidatesDev()

  return (
    <div className="space-y-6">
      <PageHeader
        title="Memory Consolidation"
        description="Nightly sleep cycle — promotion rules, chronicle candidates, reports, and email delivery."
      />

      <div className="flex gap-2">
        <Button type="button" variant="outline" onClick={() => execution.refetch()} disabled={execution.isFetching}>
          Refresh Execution
        </Button>
        <Button type="button" variant="outline" onClick={() => candidates.refetch()} disabled={candidates.isFetching}>
          Refresh Candidates
        </Button>
      </div>

      {execution.isError ? (
        <ErrorState
          error={execution.error}
          title="No consolidation execution yet — run consolidation from the Memory Consolidation page"
        />
      ) : null}

      {execution.data ? (
        <>
          <section className="rounded-lg border border-border bg-card p-4">
            <h2 className="mb-3 text-lg font-semibold">Latest Execution</h2>
            <dl className="grid gap-3 text-sm sm:grid-cols-2">
              <div>
                <dt className="text-muted-foreground">Run</dt>
                <dd className="font-medium">{execution.data.runId}</dd>
              </div>
              <div>
                <dt className="text-muted-foreground">Email Status</dt>
                <dd className="font-medium">{execution.data.emailStatus}</dd>
              </div>
              <div>
                <dt className="text-muted-foreground">Started</dt>
                <dd className="font-medium">{execution.data.startedAt}</dd>
              </div>
              <div>
                <dt className="text-muted-foreground">Completed</dt>
                <dd className="font-medium">{execution.data.completedAt}</dd>
              </div>
            </dl>
            {execution.data.reflection ? (
              <p className="mt-3 text-sm italic text-muted-foreground">{execution.data.reflection}</p>
            ) : null}
          </section>

          <section className="rounded-lg border border-border bg-card p-4">
            <h2 className="mb-3 text-lg font-semibold">Execution Timeline</h2>
            <div className="space-y-3 text-sm">
              {execution.data.trace.map((entry, index) => (
                <article key={`${entry.stage}-${entry.rule}-${index}`} className="rounded-md border border-border bg-muted/20 p-3">
                  <p className="font-semibold">
                    {entry.stage} · {entry.rule}
                  </p>
                  <p className="mt-2 text-muted-foreground">{entry.reason}</p>
                </article>
              ))}
            </div>
          </section>

          <section className="rounded-lg border border-border bg-card p-4">
            <h2 className="mb-3 text-lg font-semibold">Promotion Decisions</h2>
            <div className="space-y-3 text-sm">
              {execution.data.decisions.map((decision, index) => (
                <article key={`decision-${index}`} className="rounded-md border border-border bg-muted/20 p-3">
                  <p className="font-semibold">{decision.decision}</p>
                  <p className="mt-1 text-muted-foreground">{decision.reason}</p>
                  <p className="mt-1 text-muted-foreground">Items: {decision.inboxItemIds.join(', ')}</p>
                </article>
              ))}
            </div>
          </section>

          <section className="rounded-lg border border-border bg-card p-4">
            <h2 className="mb-3 text-lg font-semibold">Report Attachments</h2>
            <div className="grid gap-4 lg:grid-cols-2">
              <div>
                <h3 className="mb-2 text-sm font-semibold">TXT</h3>
                <pre className="overflow-x-auto rounded-md bg-muted/20 p-3 text-xs">{execution.data.report.txtReport}</pre>
              </div>
              <div>
                <h3 className="mb-2 text-sm font-semibold">JSON</h3>
                <pre className="overflow-x-auto rounded-md bg-muted/20 p-3 text-xs">{execution.data.report.jsonReport}</pre>
              </div>
            </div>
          </section>
        </>
      ) : null}

      {candidates.data ? (
        <section className="rounded-lg border border-border bg-card p-4">
          <h2 className="mb-3 text-lg font-semibold">Chronicle Candidates</h2>
          <div className="space-y-3 text-sm">
            {candidates.data.map((candidate) => (
              <article key={candidate.id} className="rounded-md border border-border bg-muted/20 p-3">
                <p className="font-semibold">{candidate.summary}</p>
                <p className="mt-1 text-muted-foreground">
                  {candidate.importance} · {candidate.ownerCharacterId}
                </p>
                <p className="mt-1 text-muted-foreground">Reason: {candidate.reason}</p>
                <p className="mt-1 text-muted-foreground">Inbox items: {candidate.sourceInboxItems.join(', ')}</p>
              </article>
            ))}
          </div>
        </section>
      ) : null}
    </div>
  )
}

function ReportingDeliveryPanel() {
  const configuration = useReportingConfiguration()
  const history = useReportingHistory()
  const archive = useReportingArchive()
  const sendTest = useSendTestReportEmail()
  const previewHtml = usePreviewReportingHtml()

  return (
    <div className="space-y-6">
      <PageHeader
        title="Reporting & Delivery"
        description="Templates, delivery configuration, archive, retry history, and previews."
      />

      <div className="flex flex-wrap gap-2">
        <Button type="button" variant="outline" onClick={() => configuration.refetch()} disabled={configuration.isFetching}>
          Refresh Configuration
        </Button>
        <Button type="button" variant="outline" onClick={() => history.refetch()} disabled={history.isFetching}>
          Refresh History
        </Button>
        <Button type="button" variant="outline" onClick={() => archive.refetch()} disabled={archive.isFetching}>
          Refresh Archive
        </Button>
        <Button type="button" onClick={() => sendTest.mutate()} disabled={sendTest.isPending}>
          {sendTest.isPending ? 'Sending…' : 'Send Test Email'}
        </Button>
        <Button type="button" variant="outline" onClick={() => previewHtml.mutate()} disabled={previewHtml.isPending}>
          {previewHtml.isPending ? 'Loading…' : 'Preview HTML'}
        </Button>
      </div>

      {configuration.data ? (
        <section className="rounded-lg border border-border bg-card p-4">
          <h2 className="mb-3 text-lg font-semibold">Configuration</h2>
          <dl className="grid gap-3 text-sm sm:grid-cols-2">
            <div>
              <dt className="text-muted-foreground">Enabled</dt>
              <dd className="font-medium">{String(configuration.data.enabled)}</dd>
            </div>
            <div>
              <dt className="text-muted-foreground">Email Enabled</dt>
              <dd className="font-medium">{String(configuration.data.emailEnabled)}</dd>
            </div>
            <div>
              <dt className="text-muted-foreground">Sender</dt>
              <dd className="font-medium">{configuration.data.sender || '—'}</dd>
            </div>
            <div>
              <dt className="text-muted-foreground">Subject Template</dt>
              <dd className="font-medium">{configuration.data.subjectTemplate}</dd>
            </div>
            <div className="sm:col-span-2">
              <dt className="text-muted-foreground">Recipients</dt>
              <dd className="font-medium">
                {configuration.data.recipients.length > 0 ? configuration.data.recipients.join(', ') : '—'}
              </dd>
            </div>
            <div className="sm:col-span-2">
              <dt className="text-muted-foreground">Attachments</dt>
              <dd className="font-medium">
                TXT={String(configuration.data.attachments.txt)} · JSON={String(configuration.data.attachments.json)} ·
                MD={String(configuration.data.attachments.md)} · HTML={String(configuration.data.attachments.html)}
              </dd>
            </div>
            <div className="sm:col-span-2">
              <dt className="text-muted-foreground">Closings</dt>
              <dd className="font-medium">{configuration.data.closings.join(' · ')}</dd>
            </div>
          </dl>
        </section>
      ) : null}

      {sendTest.data ? (
        <section className="rounded-lg border border-border bg-card p-4 text-sm">
          <h2 className="mb-2 text-lg font-semibold">Provider Response</h2>
          <p>Status: {sendTest.data.status}</p>
          <p className="mt-1">Sent: {sendTest.data.recipientsSent} · Failed: {sendTest.data.recipientsFailed}</p>
          {sendTest.data.error ? <p className="mt-1 text-destructive">{sendTest.data.error}</p> : null}
        </section>
      ) : null}

      {previewHtml.data ? (
        <section className="rounded-lg border border-border bg-card p-4">
          <h2 className="mb-2 text-lg font-semibold">HTML Preview</h2>
          <pre className="max-h-96 overflow-auto whitespace-pre-wrap text-xs text-muted-foreground">{previewHtml.data}</pre>
        </section>
      ) : null}

      {history.data && history.data.length > 0 ? (
        <section className="rounded-lg border border-border bg-card p-4">
          <h2 className="mb-3 text-lg font-semibold">Retry History</h2>
          <div className="space-y-3 text-sm">
            {history.data.map((entry) => (
              <article key={entry.id} className="rounded-md border border-border bg-muted/20 p-3">
                <p className="font-semibold">
                  {entry.recipient} · {entry.status} · attempt {entry.attempt}
                </p>
                <p className="mt-1 text-muted-foreground">
                  {entry.provider} · {entry.latencyMs} ms · {entry.createdAt}
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

      {archive.data && archive.data.length > 0 ? (
        <section className="rounded-lg border border-border bg-card p-4">
          <h2 className="mb-3 text-lg font-semibold">Archive</h2>
          <div className="space-y-3 text-sm">
            {archive.data.map((item) => (
              <article key={item.reportId} className="rounded-md border border-border bg-muted/20 p-3">
                <p className="font-semibold">{item.reportId}</p>
                <p className="mt-1 text-muted-foreground">{item.createdAt}</p>
              </article>
            ))}
          </div>
        </section>
      ) : null}
    </div>
  )
}

function LivingWorldPanel() {
  const latestTick = useLatestWorldTickDev()
  const runTick = useRunWorldTick()
  const events = useWorldEvents()
  const [tickHistory, setTickHistory] = useState<
    Array<{
      runId: string
      mode: string
      durationMs: number
      eventsGenerated: number
      artifactsGenerated: number
      notificationsGenerated: number
      worldDate: string
    }>
  >([])

  function handleRunTick() {
    runTick.mutate(undefined, {
      onSuccess: (result) => {
        setTickHistory((previous) => [
          {
            runId: result.runId,
            mode: result.mode,
            durationMs: result.durationMs,
            eventsGenerated: result.eventsGenerated,
            artifactsGenerated: result.artifactsGenerated,
            notificationsGenerated: result.notificationsGenerated,
            worldDate: result.worldDate,
          },
          ...previous,
        ])
      },
    })
  }

  const tick = runTick.data ?? latestTick.data

  return (
    <div className="space-y-6">
      <PageHeader
        title="Living World"
        description="Observe autonomous world ticks — generators, events, artifacts, and notifications."
      />

      <div className="flex flex-wrap gap-2">
        <Button type="button" onClick={handleRunTick} disabled={runTick.isPending}>
          {runTick.isPending ? 'Ticking…' : 'Run World Tick'}
        </Button>
        <Button type="button" variant="outline" onClick={() => latestTick.refetch()} disabled={latestTick.isFetching}>
          Refresh Latest Tick
        </Button>
        <Button type="button" variant="outline" onClick={() => events.refetch()} disabled={events.isFetching}>
          Refresh Events
        </Button>
      </div>

      {latestTick.isError && !runTick.data ? (
        <ErrorState error={latestTick.error} title="No world tick yet — run a tick to advance the Jungle" />
      ) : null}

      {tick ? (
        <>
          <section className="rounded-lg border border-border bg-card p-4">
            <h2 className="mb-3 text-lg font-semibold">Latest Tick</h2>
            <dl className="grid gap-3 text-sm sm:grid-cols-2 lg:grid-cols-3">
              <div>
                <dt className="text-muted-foreground">Run</dt>
                <dd className="font-medium">{tick.runId}</dd>
              </div>
              <div>
                <dt className="text-muted-foreground">Mode</dt>
                <dd className="font-medium">{tick.mode}</dd>
              </div>
              <div>
                <dt className="text-muted-foreground">World Date</dt>
                <dd className="font-medium">{tick.worldDate}</dd>
              </div>
              <div>
                <dt className="text-muted-foreground">Events</dt>
                <dd className="font-medium">{tick.eventsGenerated}</dd>
              </div>
              <div>
                <dt className="text-muted-foreground">Artifacts</dt>
                <dd className="font-medium">{tick.artifactsGenerated}</dd>
              </div>
              <div>
                <dt className="text-muted-foreground">Notifications</dt>
                <dd className="font-medium">{tick.notificationsGenerated}</dd>
              </div>
              <div>
                <dt className="text-muted-foreground">Duration</dt>
                <dd className="font-medium">{tick.durationMs} ms</dd>
              </div>
            </dl>
          </section>

          <section className="rounded-lg border border-border bg-card p-4">
            <h2 className="mb-3 text-lg font-semibold">Executed Generators</h2>
            <div className="space-y-3 text-sm">
              {tick.trace.map((entry, index) => (
                <article key={`${entry.generator}-${entry.rule}-${index}`} className="rounded-md border border-border bg-muted/20 p-3">
                  <p className="font-semibold">
                    {entry.generator} · {entry.rule}
                  </p>
                  <p className="mt-2 text-muted-foreground">{entry.reason}</p>
                </article>
              ))}
            </div>
          </section>

          <section className="rounded-lg border border-border bg-card p-4">
            <h2 className="mb-3 text-lg font-semibold">Generated Events</h2>
            <div className="space-y-3 text-sm">
              {tick.events.map((event) => (
                <article key={event.id} className="rounded-md border border-border bg-muted/20 p-3">
                  <p className="font-semibold">{event.title}</p>
                  <p className="mt-1 text-muted-foreground">
                    {event.type} · {event.origin} · {event.effectiveDate}
                  </p>
                  <p className="mt-2 text-muted-foreground">{event.summary}</p>
                </article>
              ))}
            </div>
          </section>

          <section className="rounded-lg border border-border bg-card p-4">
            <h2 className="mb-3 text-lg font-semibold">Artifacts & Notifications</h2>
            <dl className="grid gap-3 text-sm sm:grid-cols-2">
              <div>
                <dt className="text-muted-foreground">Artifact IDs</dt>
                <dd className="mt-1 space-y-1 font-mono text-xs">
                  {tick.artifactIds.length > 0 ? tick.artifactIds.map((id) => <p key={id}>{id}</p>) : '—'}
                </dd>
              </div>
              <div>
                <dt className="text-muted-foreground">Notification IDs</dt>
                <dd className="mt-1 space-y-1 font-mono text-xs">
                  {tick.notificationIds.length > 0 ? tick.notificationIds.map((id) => <p key={id}>{id}</p>) : '—'}
                </dd>
              </div>
            </dl>
          </section>
        </>
      ) : null}

      {tickHistory.length > 0 ? (
        <section className="rounded-lg border border-border bg-card p-4">
          <h2 className="mb-3 text-lg font-semibold">Tick History (session)</h2>
          <div className="space-y-2 text-sm text-muted-foreground">
            {tickHistory.map((entry) => (
              <p key={entry.runId}>
                {entry.runId} · {entry.mode} · {entry.worldDate} · {entry.eventsGenerated} events · {entry.durationMs} ms
              </p>
            ))}
          </div>
        </section>
      ) : null}
    </div>
  )
}

function ChronicleWriterPanel() {
  const execution = useChronicleWriteExecutionDev()
  const chronicles = useChronicles()
  const write = useWriteChronicles()

  return (
    <div className="space-y-6">
      <PageHeader
        title="Chronicle Writer"
        description="Deterministic conversion of long-term memory candidates into immutable chronicles."
      />

      <div className="flex flex-wrap gap-2">
        <Button type="button" onClick={() => write.mutate()} disabled={write.isPending}>
          {write.isPending ? 'Writing…' : 'Write Chronicles'}
        </Button>
        <Button type="button" variant="outline" onClick={() => execution.refetch()} disabled={execution.isFetching}>
          Refresh Execution
        </Button>
        <Button type="button" variant="outline" onClick={() => chronicles.refetch()} disabled={chronicles.isFetching}>
          Refresh Chronicles
        </Button>
      </div>

      {execution.isError ? (
        <ErrorState error={execution.error} title="No chronicle write run yet — write chronicles from the Chronicles page" />
      ) : null}

      {execution.data ? (
        <>
          <section className="rounded-lg border border-border bg-card p-4">
            <h2 className="mb-3 text-lg font-semibold">Latest Write Run</h2>
            <dl className="grid gap-3 text-sm sm:grid-cols-2">
              <div>
                <dt className="text-muted-foreground">Run</dt>
                <dd className="font-medium">{execution.data.runId}</dd>
              </div>
              <div>
                <dt className="text-muted-foreground">Written</dt>
                <dd className="font-medium">{execution.data.chroniclesWritten}</dd>
              </div>
              <div>
                <dt className="text-muted-foreground">Skipped</dt>
                <dd className="font-medium">{execution.data.skipped}</dd>
              </div>
              <div>
                <dt className="text-muted-foreground">Duration</dt>
                <dd className="font-medium">{execution.data.durationMs} ms</dd>
              </div>
            </dl>
          </section>

          <section className="rounded-lg border border-border bg-card p-4">
            <h2 className="mb-3 text-lg font-semibold">Write Trace</h2>
            <div className="space-y-3 text-sm">
              {execution.data.trace.map((entry, index) => (
                <article key={`${entry.stage}-${entry.rule}-${index}`} className="rounded-md border border-border bg-muted/20 p-3">
                  <p className="font-semibold">
                    {entry.stage} · {entry.rule}
                  </p>
                  <p className="mt-2 text-muted-foreground">{entry.reason}</p>
                </article>
              ))}
            </div>
          </section>

          <section className="rounded-lg border border-border bg-card p-4">
            <h2 className="mb-3 text-lg font-semibold">Generated Chronicles</h2>
            <div className="space-y-3 text-sm">
              {execution.data.chronicles.map((chronicle) => (
                <article key={chronicle.id} className="rounded-md border border-border bg-muted/20 p-3">
                  <p className="font-semibold">{chronicle.title}</p>
                  <p className="mt-1 text-muted-foreground">
                    {chronicle.category} · {chronicle.visibility} · {chronicle.confidence} · v{chronicle.version}
                  </p>
                  <p className="mt-1 text-muted-foreground">Template: {chronicle.metadata.template ?? '—'}</p>
                  <p className="mt-2 italic">{chronicle.body}</p>
                  <p className="mt-2 text-muted-foreground">Source candidate: {chronicle.provenance.candidateId}</p>
                  <ul className="mt-2 space-y-1 text-muted-foreground">
                    {chronicle.provenance.chain.map((link) => (
                      <li key={`${link.stage}-${link.entityId}`}>
                        {link.stage} → {link.entityId}
                      </li>
                    ))}
                  </ul>
                </article>
              ))}
            </div>
          </section>
        </>
      ) : null}

      {chronicles.data && chronicles.data.length > 0 ? (
        <section className="rounded-lg border border-border bg-card p-4">
          <h2 className="mb-3 text-lg font-semibold">All Chronicles</h2>
          <div className="space-y-2 text-sm text-muted-foreground">
            {chronicles.data.map((chronicle) => (
              <p key={chronicle.id}>
                {chronicle.title} · {chronicle.category} · {chronicle.chronicleDate}
              </p>
            ))}
          </div>
        </section>
      ) : null}
    </div>
  )
}

export function DeveloperPage() {
  const [tab, setTab] = useState<
    | 'context-plan'
    | 'knowledge-fragments'
    | 'prompt-composition'
    | 'identity-diagnostics'
    | 'working-memory'
    | 'conversation-director'
    | 'behavior-engine'
    | 'living-notifications'
    | 'conversation-artifacts'
    | 'cognitive-analysis'
    | 'memory-inbox'
    | 'memory-consolidation'
    | 'reporting-delivery'
    | 'chronicle-writer'
    | 'living-world'
    | 'context-profile'
    | 'budget-allocation'
    | 'llm-request'
  >('context-plan')

  return (
    <div className="space-y-6">
      <Breadcrumbs items={[{ label: 'Developer' }]} />
      <PageHeader
        title="Developer Panel"
        description="Inspect internal subsystems. Not for players."
      />

      <div className="flex gap-2 border-b border-border">
        <button
          type="button"
          className={`border-b-2 px-3 py-2 text-sm font-medium ${
            tab === 'context-plan'
              ? 'border-primary text-foreground'
              : 'border-transparent text-muted-foreground'
          }`}
          onClick={() => setTab('context-plan')}
        >
          Context Plan
        </button>
        <button
          type="button"
          className={`border-b-2 px-3 py-2 text-sm font-medium ${
            tab === 'knowledge-fragments'
              ? 'border-primary text-foreground'
              : 'border-transparent text-muted-foreground'
          }`}
          onClick={() => setTab('knowledge-fragments')}
        >
          Knowledge Fragments
        </button>
        <button
          type="button"
          className={`border-b-2 px-3 py-2 text-sm font-medium ${
            tab === 'prompt-composition'
              ? 'border-primary text-foreground'
              : 'border-transparent text-muted-foreground'
          }`}
          onClick={() => setTab('prompt-composition')}
        >
          Prompt Composition
        </button>
        <button
          type="button"
          className={`border-b-2 px-3 py-2 text-sm font-medium ${
            tab === 'identity-diagnostics'
              ? 'border-primary text-foreground'
              : 'border-transparent text-muted-foreground'
          }`}
          onClick={() => setTab('identity-diagnostics')}
        >
          Identity Diagnostics
        </button>
        <button
          type="button"
          className={`border-b-2 px-3 py-2 text-sm font-medium ${
            tab === 'working-memory'
              ? 'border-primary text-foreground'
              : 'border-transparent text-muted-foreground'
          }`}
          onClick={() => setTab('working-memory')}
        >
          Working Memory
        </button>
        <button
          type="button"
          className={`border-b-2 px-3 py-2 text-sm font-medium ${
            tab === 'conversation-director'
              ? 'border-primary text-foreground'
              : 'border-transparent text-muted-foreground'
          }`}
          onClick={() => setTab('conversation-director')}
        >
          Conversation Director
        </button>
        <button
          type="button"
          className={`border-b-2 px-3 py-2 text-sm font-medium ${
            tab === 'behavior-engine'
              ? 'border-primary text-foreground'
              : 'border-transparent text-muted-foreground'
          }`}
          onClick={() => setTab('behavior-engine')}
        >
          Behavior Engine
        </button>
        <button
          type="button"
          className={`border-b-2 px-3 py-2 text-sm font-medium ${
            tab === 'living-notifications'
              ? 'border-primary text-foreground'
              : 'border-transparent text-muted-foreground'
          }`}
          onClick={() => setTab('living-notifications')}
        >
          Living Notifications
        </button>
        <button
          type="button"
          className={`border-b-2 px-3 py-2 text-sm font-medium ${
            tab === 'conversation-artifacts'
              ? 'border-primary text-foreground'
              : 'border-transparent text-muted-foreground'
          }`}
          onClick={() => setTab('conversation-artifacts')}
        >
          Conversation Artifacts
        </button>
        <button
          type="button"
          className={`border-b-2 px-3 py-2 text-sm font-medium ${
            tab === 'cognitive-analysis'
              ? 'border-primary text-foreground'
              : 'border-transparent text-muted-foreground'
          }`}
          onClick={() => setTab('cognitive-analysis')}
        >
          Cognitive Analysis
        </button>
        <button
          type="button"
          className={`border-b-2 px-3 py-2 text-sm font-medium ${
            tab === 'memory-inbox'
              ? 'border-primary text-foreground'
              : 'border-transparent text-muted-foreground'
          }`}
          onClick={() => setTab('memory-inbox')}
        >
          Memory Inbox
        </button>
        <button
          type="button"
          className={`border-b-2 px-3 py-2 text-sm font-medium ${
            tab === 'memory-consolidation'
              ? 'border-primary text-foreground'
              : 'border-transparent text-muted-foreground'
          }`}
          onClick={() => setTab('memory-consolidation')}
        >
          Memory Consolidation
        </button>
        <button
          type="button"
          className={`border-b-2 px-3 py-2 text-sm font-medium ${
            tab === 'reporting-delivery'
              ? 'border-primary text-foreground'
              : 'border-transparent text-muted-foreground'
          }`}
          onClick={() => setTab('reporting-delivery')}
        >
          Reporting & Delivery
        </button>
        <button
          type="button"
          className={`border-b-2 px-3 py-2 text-sm font-medium ${
            tab === 'chronicle-writer'
              ? 'border-primary text-foreground'
              : 'border-transparent text-muted-foreground'
          }`}
          onClick={() => setTab('chronicle-writer')}
        >
          Chronicle Writer
        </button>
        <button
          type="button"
          className={`border-b-2 px-3 py-2 text-sm font-medium ${
            tab === 'living-world'
              ? 'border-primary text-foreground'
              : 'border-transparent text-muted-foreground'
          }`}
          onClick={() => setTab('living-world')}
        >
          Living World
        </button>
        <button
          type="button"
          className={`border-b-2 px-3 py-2 text-sm font-medium ${
            tab === 'context-profile'
              ? 'border-primary text-foreground'
              : 'border-transparent text-muted-foreground'
          }`}
          onClick={() => setTab('context-profile')}
        >
          Context Profile
        </button>
        <button
          type="button"
          className={`border-b-2 px-3 py-2 text-sm font-medium ${
            tab === 'budget-allocation'
              ? 'border-primary text-foreground'
              : 'border-transparent text-muted-foreground'
          }`}
          onClick={() => setTab('budget-allocation')}
        >
          Budget Allocation
        </button>
        <button
          type="button"
          className={`border-b-2 px-3 py-2 text-sm font-medium ${
            tab === 'llm-request'
              ? 'border-primary text-foreground'
              : 'border-transparent text-muted-foreground'
          }`}
          onClick={() => setTab('llm-request')}
        >
          LLM Request
        </button>
      </div>

      {tab === 'context-plan' ? (
        <ContextPlanPanel />
      ) : tab === 'knowledge-fragments' ? (
        <KnowledgeFragmentsPanel />
      ) : tab === 'prompt-composition' ? (
        <PromptCompositionPanel />
      ) : tab === 'identity-diagnostics' ? (
        <IdentityDiagnosticsPanel />
      ) : tab === 'working-memory' ? (
        <WorkingMemoryPanel />
      ) : tab === 'conversation-director' ? (
        <ConversationDirectorPanel />
      ) : tab === 'behavior-engine' ? (
        <BehaviorEnginePanel />
      ) : tab === 'living-notifications' ? (
        <LivingNotificationsPanel />
      ) : tab === 'conversation-artifacts' ? (
        <ConversationArtifactsPanel />
      ) : tab === 'cognitive-analysis' ? (
        <CognitiveAnalysisPanel />
      ) : tab === 'memory-inbox' ? (
        <MemoryInboxPanel />
      ) : tab === 'memory-consolidation' ? (
        <MemoryConsolidationPanel />
      ) : tab === 'reporting-delivery' ? (
        <ReportingDeliveryPanel />
      ) : tab === 'chronicle-writer' ? (
        <ChronicleWriterPanel />
      ) : tab === 'living-world' ? (
        <LivingWorldPanel />
      ) : tab === 'context-profile' ? (
        <ContextProfilePanel />
      ) : tab === 'budget-allocation' ? (
        <BudgetAllocationPanel />
      ) : (
        <LlmRequestPanel />
      )}
    </div>
  )
}
