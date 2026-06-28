import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { render, screen, waitFor } from '@testing-library/react'
import { describe, expect, it, vi } from 'vitest'
import { useWorldStatus } from '@/hooks/useWorldStatus'

vi.mock('@/api/world', () => ({
  fetchWorldStatus: vi.fn(async () => ({
    status: 'READY',
    bootstrapVersion: '1.0',
    bootstrapTimestamp: '2026-06-27T00:00:00Z',
    runtimeStartedAt: '2026-06-27T12:00:00Z',
    persistenceProvider: 'IN_MEMORY_H2',
    characters: 13,
    stories: 3,
    territories: 2,
    places: 8,
    organizations: 2,
    relationships: 21,
    timelineEntries: 5,
    charactersBySpecies: { Hippu: 10 },
    storiesByEra: { Uncategorized: 3 },
  })),
}))

function Probe() {
  const query = useWorldStatus()
  if (query.isLoading) return <div>loading</div>
  if (query.isError) return <div>error</div>
  return <div>{query.data?.status}</div>
}

describe('useWorldStatus', () => {
  it('loads world status through react query', async () => {
    const client = new QueryClient({ defaultOptions: { queries: { retry: false } } })
    render(
      <QueryClientProvider client={client}>
        <Probe />
      </QueryClientProvider>,
    )

    await waitFor(() => {
      expect(screen.getByText('READY')).toBeInTheDocument()
    })
  })
})
