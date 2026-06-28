import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { render, screen, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { MemoryRouter } from 'react-router-dom'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import { DeveloperPage } from '@/pages/DeveloperPage'

const mutateMock = vi.fn()
const resolveMock = vi.fn()

vi.mock('@/hooks/useContextPlan', () => ({
  useContextPlan: () => ({
    mutate: mutateMock,
    isPending: false,
    isError: false,
    data: {
      sections: [
        {
          type: 'PERSONALITY',
          priority: 10,
          source: 'promptProfiles',
          contentReference: 'promptProfile:prompt_bandar_personality:sections',
          estimatedTokens: 9,
        },
      ],
      totalEstimatedTokens: 9,
      trace: {
        entries: [{ type: 'PERSONALITY', reason: 'Always included' }],
      },
    },
  }),
  useContextResolve: () => ({
    mutate: resolveMock,
    isPending: false,
    isError: false,
    data: {
      sections: [],
      fragments: [
        {
          fragmentId: 'prompt_bandar_personality:identity:IDENTITY',
          fragmentType: 'IDENTITY',
          title: 'Bandar Identity',
          content: 'I am Bandar, the oldest living being.',
          sourceDocument: 'prompt_bandar_personality',
          sourceSection: 'identity',
          estimatedTokens: 8,
          tags: [],
          confidence: 1,
          selectionReason: 'Always included',
        },
      ],
      totalEstimatedTokens: 8,
    },
  }),
}))

const composeMock = vi.fn()

vi.mock('@/hooks/usePromptCompose', () => ({
  usePromptCompose: () => ({
    mutate: composeMock,
    isPending: false,
    isError: false,
    data: {
      sections: [
        {
          sectionType: 'CURRENT_USER',
          title: 'Current User',
          priority: 5,
          required: true,
          estimatedTokens: 12,
          content: 'The character currently speaking with you is:\n\nName: Hippu King',
          fragmentId: '',
          fragmentType: 'UNKNOWN',
        },
        {
          sectionType: 'INSTRUCTIONS',
          title: 'Instructions',
          priority: 950,
          required: true,
          estimatedTokens: 20,
          content: 'Answer only using the provided world knowledge.',
          fragmentId: '',
          fragmentType: 'UNKNOWN',
        },
      ],
      totalEstimatedTokens: 32,
      requiredSectionCount: 2,
      optionalSectionCount: 0,
      llmMessages: [
        {
          role: 'system',
          content: 'The Current Speaker\n\nThe one speaking with you now is:\n\nName: Hippu King',
        },
        {
          role: 'system',
          content: 'Instructions\n\nAnswer only using the provided world knowledge.',
        },
      ],
      inspection: {
        sections: [
          { sectionType: 'CURRENT_USER', title: 'Current User', priority: 5, required: true, estimatedTokens: 12 },
        ],
        totalEstimatedTokens: 32,
        requiredSectionCount: 2,
        optionalSectionCount: 0,
      },
    },
  }),
}))

vi.mock('@/hooks/useMemoryInbox', () => ({
  useMemoryInboxGenerationTrace: () => ({
    refetch: vi.fn(),
    isFetching: false,
    isError: false,
    data: {
      characterId: 'character_alpha',
      conversationId: 'conv-1',
      generatedAt: '2026-06-01T12:00:00Z',
      trace: [{ rule: 'promise-artifact-rule', reason: 'PROMISE artifact detected' }],
      generatedItems: [
        {
          id: 'inbox-1',
          type: 'PROMISE',
          source: 'CONVERSATION_ARTIFACT',
          sourceId: 'art-1',
          ownerCharacterId: 'character_alpha',
          summary: 'Remember this',
          importance: 'HIGH',
          confidence: 0.85,
          status: 'NEW',
          createdAt: '2026-06-01T12:00:00Z',
          expiresAt: '2026-07-01T12:00:00Z',
          metadata: { evidence: 'Remember this' },
          trace: ['created:promise-artifact'],
          analysisId: '',
          artifactIds: ['art-1'],
        },
      ],
    },
  }),
  useAllMemoryInboxDev: () => ({
    refetch: vi.fn(),
    isFetching: false,
    data: [],
  }),
}))

vi.mock('@/hooks/useMemoryConsolidation', () => ({
  useConsolidationExecutionDev: () => ({
    refetch: vi.fn(),
    isFetching: false,
    isError: false,
    data: {
      runId: 'run-1',
      startedAt: '2026-06-01T00:00:00Z',
      completedAt: '2026-06-01T00:00:01Z',
      report: {
        txtReport: 'Jungle Daily Report',
        jsonReport: '{}',
      },
      candidates: [],
      trace: [{ stage: 'DISCOVER', rule: 'collect', reason: 'Scanning inbox items' }],
      decisions: [{ decision: 'PROMOTE', reason: 'PROMISE always promoted', inboxItemIds: ['i-1'] }],
      reflection: 'Today I heard many stories.',
      emailStatus: 'SKIPPED',
      emailError: '',
    },
  }),
  useAllCandidatesDev: () => ({
    refetch: vi.fn(),
    isFetching: false,
    data: [],
  }),
}))

vi.mock('@/hooks/useReporting', () => ({
  useReportingConfiguration: () => ({
    refetch: vi.fn(),
    isFetching: false,
    data: {
      enabled: true,
      emailEnabled: false,
      sender: 'Bandar <bandar@example.com>',
      subjectTemplate: 'Letter {date}',
      recipients: ['a@gmail.com'],
      closings: ['Until tomorrow.'],
      attachments: { txt: true, json: true, md: true, html: false },
    },
  }),
  useReportingHistory: () => ({
    refetch: vi.fn(),
    isFetching: false,
    data: [],
  }),
  useReportingArchive: () => ({
    refetch: vi.fn(),
    isFetching: false,
    data: [],
  }),
  useSendTestReportEmail: () => ({
    mutate: vi.fn(),
    isPending: false,
    data: null,
  }),
  usePreviewReportingHtml: () => ({
    mutate: vi.fn(),
    isPending: false,
    data: null,
  }),
}))

vi.mock('@/hooks/useChronicles', () => ({
  useChronicles: () => ({
    refetch: vi.fn(),
    isFetching: false,
    data: [],
  }),
  useChronicleWriteExecutionDev: () => ({
    refetch: vi.fn(),
    isFetching: false,
    isError: false,
    data: {
      runId: 'write-run-1',
      startedAt: '2026-06-01T00:00:00Z',
      completedAt: '2026-06-01T00:00:01Z',
      durationMs: 10,
      candidatesProcessed: 1,
      chroniclesWritten: 1,
      skipped: 0,
      chronicles: [
        {
          id: 'chron-candidate-1-v1',
          title: 'Promise: Lost Crown',
          category: 'PROMISE',
          visibility: 'PRIVATE',
          confidence: 'OFFICIAL',
          ownerCharacterId: 'character_alpha',
          summary: 'Lost Crown story',
          body: 'Bandar promised Hippu King that he would tell the Lost Crown story.',
          createdAt: '2026-06-01T00:00:01Z',
          chronicleDate: '2026-06-01',
          metadata: { template: 'promise-narrative-v1' },
          provenance: {
            conversationId: 'conv-1',
            artifactIds: ['artifact-1'],
            observationIds: [],
            inboxItemIds: ['inbox-1'],
            consolidationRunId: 'run-1',
            candidateId: 'candidate-1',
            chronicleId: 'chron-candidate-1-v1',
            chain: [{ stage: 'CANDIDATE', entityId: 'candidate-1', label: 'Candidate' }],
            metadata: {},
          },
          version: 1,
        },
      ],
      trace: [{ stage: 'WRITE', rule: 'promise-narrative-v1', reason: 'Wrote chronicle' }],
    },
  }),
  useWriteChronicles: () => ({
    mutate: vi.fn(),
    isPending: false,
    data: null,
  }),
}))

vi.mock('@/hooks/useLivingWorld', () => ({
  useWorldEvents: () => ({
    refetch: vi.fn(),
    isFetching: false,
    data: [],
  }),
  useLatestWorldTickDev: () => ({
    refetch: vi.fn(),
    isFetching: false,
    isError: false,
    data: {
      runId: 'tick-1',
      mode: 'MANUAL',
      startedAt: '2026-06-27T12:00:00Z',
      completedAt: '2026-06-27T12:00:01Z',
      durationMs: 12,
      worldDate: '2026-06-27',
      eventsGenerated: 1,
      artifactsGenerated: 1,
      notificationsGenerated: 1,
      events: [
        {
          id: 'evt-festival',
          type: 'FESTIVAL',
          title: 'Spring Festival',
          summary: 'Celebration today',
          participants: [],
          visibility: 'PUBLIC',
          createdAt: '2026-06-27T12:00:00Z',
          effectiveDate: '2026-06-27',
          metadata: {},
          status: 'ACTIVE',
          origin: 'FESTIVAL_ENGINE',
        },
      ],
      artifactIds: ['art-world-evt-festival'],
      notificationIds: ['notif-1'],
      trace: [{ generator: 'FestivalEngine', rule: 'festival-today', reason: 'Spring Festival' }],
    },
  }),
  useRunWorldTick: () => ({
    mutate: vi.fn(),
    isPending: false,
    data: null,
  }),
}))

function renderDeveloper() {
  const queryClient = new QueryClient({ defaultOptions: { queries: { retry: false } } })
  return render(
    <QueryClientProvider client={queryClient}>
      <MemoryRouter>
        <DeveloperPage />
      </MemoryRouter>
    </QueryClientProvider>,
  )
}

describe('DeveloperPage', () => {
  beforeEach(() => {
    mutateMock.mockReset()
    resolveMock.mockReset()
    composeMock.mockReset()
  })

  it('renders context plan tab and sections', () => {
    renderDeveloper()

    expect(screen.getByText('Developer Panel')).toBeInTheDocument()
    expect(screen.getByRole('button', { name: 'Context Plan' })).toBeInTheDocument()
    expect(screen.getAllByText('PERSONALITY').length).toBeGreaterThan(0)
    expect(screen.getByText('Always included')).toBeInTheDocument()
  })

  it('submits latest message for planning', async () => {
    const user = userEvent.setup()
    renderDeveloper()

    await user.clear(screen.getByLabelText('Latest user message'))
    await user.type(screen.getByLabelText('Latest user message'), 'Where am I?')
    await user.click(screen.getByRole('button', { name: 'Plan Context' }))

    await waitFor(() => {
      expect(mutateMock).toHaveBeenCalledWith({ latestMessage: 'Where am I?' })
    })
  })

  it('shows knowledge fragments tab content', async () => {
    const user = userEvent.setup()
    renderDeveloper()

    await user.click(screen.getByRole('button', { name: 'Knowledge Fragments' }))

    expect(screen.getByRole('heading', { name: 'Knowledge Fragments' })).toBeInTheDocument()
    expect(screen.getByText(/oldest living being/)).toBeInTheDocument()
  })

  it('shows prompt composition tab content', async () => {
    const user = userEvent.setup()
    renderDeveloper()

    await user.click(screen.getByRole('button', { name: 'Prompt Composition' }))

    expect(screen.getByRole('heading', { name: 'Prompt Composition' })).toBeInTheDocument()
    expect(screen.getAllByText('CURRENT_USER').length).toBeGreaterThan(0)
    expect(screen.getAllByText(/currently speaking with you/).length).toBeGreaterThan(0)
    expect(screen.queryByText(/OpenAI|Claude|Gemini/)).not.toBeInTheDocument()
  })

  it('shows memory inbox tab content', async () => {
    const user = userEvent.setup()
    renderDeveloper()

    await user.click(screen.getByRole('button', { name: 'Memory Inbox' }))

    expect(screen.getByRole('heading', { name: 'Memory Inbox' })).toBeInTheDocument()
    expect(screen.getByText('Generation Rules')).toBeInTheDocument()
    expect(screen.getByText('promise-artifact-rule')).toBeInTheDocument()
  })

  it('shows memory consolidation tab content', async () => {
    const user = userEvent.setup()
    renderDeveloper()

    await user.click(screen.getByRole('button', { name: 'Memory Consolidation' }))

    expect(screen.getByRole('heading', { name: 'Memory Consolidation' })).toBeInTheDocument()
    expect(screen.getByText('Execution Timeline')).toBeInTheDocument()
    expect(screen.getByText('Scanning inbox items')).toBeInTheDocument()
  })

  it('shows living world tab content', async () => {
    const user = userEvent.setup()
    renderDeveloper()

    await user.click(screen.getByRole('button', { name: 'Living World' }))

    expect(screen.getByRole('heading', { name: 'Living World' })).toBeInTheDocument()
    expect(screen.getByText('Latest Tick')).toBeInTheDocument()
    expect(screen.getByText('FestivalEngine · festival-today')).toBeInTheDocument()
    expect(screen.getAllByText('Spring Festival').length).toBeGreaterThan(0)
  })
})
