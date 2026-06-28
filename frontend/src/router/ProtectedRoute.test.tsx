import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { render, screen } from '@testing-library/react'
import { MemoryRouter, Route, Routes } from 'react-router-dom'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import { ProtectedRoute } from '@/router/ProtectedRoute'
import { SESSION_STORAGE_KEY } from '@/types/session'

const { useSessionMock } = vi.hoisted(() => ({
  useSessionMock: vi.fn(),
}))

vi.mock('@/hooks/useSession', () => ({
  useSession: () => useSessionMock(),
}))

function renderGuard(initialPath = '/world') {
  const queryClient = new QueryClient({ defaultOptions: { queries: { retry: false } } })
  return render(
    <QueryClientProvider client={queryClient}>
      <MemoryRouter initialEntries={[initialPath]}>
        <Routes>
          <Route path="/world" element={<ProtectedRoute />}>
            <Route index element={<div>Protected content</div>} />
          </Route>
          <Route path="/login" element={<div>Login page</div>} />
        </Routes>
      </MemoryRouter>
    </QueryClientProvider>,
  )
}

describe('ProtectedRoute', () => {
  beforeEach(() => {
    sessionStorage.clear()
    useSessionMock.mockReset()
  })

  it('redirects to login without session id', async () => {
    useSessionMock.mockReturnValue({ data: undefined, isLoading: false, isError: false })

    renderGuard()

    expect(await screen.findByText('Login page')).toBeInTheDocument()
  })

  it('shows loading while session is fetched', () => {
    sessionStorage.setItem(SESSION_STORAGE_KEY, 'session-1')
    useSessionMock.mockReturnValue({ data: undefined, isLoading: true, isError: false })

    renderGuard()

    expect(screen.getByText('Checking session…')).toBeInTheDocument()
  })

  it('redirects when session fetch fails', async () => {
    sessionStorage.setItem(SESSION_STORAGE_KEY, 'session-1')
    useSessionMock.mockReturnValue({ data: undefined, isLoading: false, isError: true })

    renderGuard()

    expect(await screen.findByText('Login page')).toBeInTheDocument()
  })

  it('renders protected content for valid session', async () => {
    sessionStorage.setItem(SESSION_STORAGE_KEY, 'session-1')
    useSessionMock.mockReturnValue({
      data: {
        sessionId: 'session-1',
        currentCharacter: {
          id: 'c1',
          displayName: 'Alpha',
          titles: [],
          species: 'Rabbitu',
          homeTerritory: null,
        },
        startedAt: '2026-06-27T12:00:00Z',
        lastActivity: '2026-06-27T12:00:00Z',
        status: 'ACTIVE',
      },
      isLoading: false,
      isError: false,
    })

    renderGuard()

    expect(await screen.findByText('Protected content')).toBeInTheDocument()
  })
})
