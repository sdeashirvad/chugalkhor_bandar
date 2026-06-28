import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { MemoryRouter } from 'react-router-dom'
import { describe, expect, it, vi } from 'vitest'
import { WelcomeOverlay } from '@/components/world/WelcomeOverlay'

vi.mock('@/hooks/useSession', () => ({
  useSession: () => ({
    data: {
      sessionId: 's-1',
      currentCharacter: {
        id: 'character_hippu_king',
        displayName: 'Hippu King',
        titles: ['King'],
        species: 'Hippu',
        homeTerritory: 'Home Jungle',
      },
      startedAt: '2026-06-01T00:00:00Z',
      lastActivity: '2026-06-01T00:00:00Z',
      status: 'ACTIVE',
    },
  }),
}))

vi.mock('@/hooks/useUnreadNotificationCount', () => ({
  useUnreadNotificationCount: () => ({ data: { unreadCount: 0 } }),
}))

vi.mock('@/hooks/useArtifacts', () => ({
  useArtifacts: () => ({ data: [] }),
}))

describe('WelcomeOverlay', () => {
  it('shows character name and skip', async () => {
    const onComplete = vi.fn()
    const queryClient = new QueryClient()
    render(
      <QueryClientProvider client={queryClient}>
        <MemoryRouter>
          <WelcomeOverlay onComplete={onComplete} />
        </MemoryRouter>
      </QueryClientProvider>,
    )

    expect(screen.getByText('Hippu King')).toBeInTheDocument()
    await userEvent.click(screen.getByRole('button', { name: 'Skip' }))
    expect(onComplete).toHaveBeenCalled()
  })
})
