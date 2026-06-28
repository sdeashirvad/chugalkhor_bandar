import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { render, screen, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { createMemoryRouter, RouterProvider } from 'react-router-dom'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import { appRoutes } from '@/router'
import { SESSION_STORAGE_KEY } from '@/types/session'

const { useSessionMock, loginMock } = vi.hoisted(() => ({
  useSessionMock: vi.fn(),
  loginMock: vi.fn(),
}))

const mockSession = {
  sessionId: 'session-test',
  currentCharacter: {
    id: 'character_alpha',
    displayName: 'Alpha',
    titles: ['Alpha'],
    species: 'Rabbitu',
    homeTerritory: null,
  },
  startedAt: '2026-06-27T12:00:00Z',
  lastActivity: '2026-06-27T12:00:00Z',
  status: 'ACTIVE',
}

vi.mock('@/hooks/useHomeSummary', () => ({
  useHomeSummary: () => ({
    isLoading: false,
    isError: false,
    data: {
      session: mockSession,
      character: { id: 'character_alpha', name: 'Alpha', currentPlaceName: 'Home Jungle', titles: [], species: 'Rabbitu', currentPlace: null, lastSeenAt: null },
      workingMemory: null,
      lastBandarLine: null,
      unreadCount: 0,
      activeArtifacts: 0,
      recentEvents: [],
      latestChronicle: null,
      worldDate: '2026-06-27',
      conversation: null,
    },
  }),
}))

vi.mock('@/hooks/useWorldSearch', () => ({
  useWorldSearch: () => ({ results: [], isLoading: false }),
}))

vi.mock('@/hooks/useUnreadNotificationCount', () => ({
  useUnreadNotificationCount: () => ({ data: { unreadCount: 0 } }),
}))

vi.mock('@/hooks/useCharacters', () => ({
  useCharacters: () => ({
    data: [
      { id: 'character_alpha', name: 'Alpha', species: 'Rabbitu', titles: [], currentPlace: null, currentPlaceName: null, lastSeenAt: null },
    ],
    isLoading: false,
  }),
}))

vi.mock('@/hooks/useSession', () => ({
  useSession: () => useSessionMock(),
  useLogin: () => ({
    mutate: loginMock,
    isPending: false,
    isError: false,
    error: null,
  }),
  useLogout: () => ({
    mutate: vi.fn(),
    isPending: false,
  }),
}))

function renderAt(path: string) {
  const queryClient = new QueryClient({ defaultOptions: { queries: { retry: false } } })
  const memoryRouter = createMemoryRouter(appRoutes, { initialEntries: [path] })
  return render(
    <QueryClientProvider client={queryClient}>
      <RouterProvider router={memoryRouter} />
    </QueryClientProvider>,
  )
}

describe('router', () => {
  beforeEach(() => {
    sessionStorage.clear()
    loginMock.mockReset()
  })

  it('redirects unauthenticated users from /home to login', async () => {
    useSessionMock.mockReturnValue({ data: undefined, isLoading: false, isError: true })

    renderAt('/home')

    expect(await screen.findByLabelText('Character')).toBeInTheDocument()
    expect(screen.getByRole('button', { name: 'Step Inside' })).toBeInTheDocument()
  })

  it('renders home when session is valid', async () => {
    sessionStorage.setItem(SESSION_STORAGE_KEY, 'session-test')
    useSessionMock.mockReturnValue({ data: mockSession, isLoading: false, isError: false })

    renderAt('/home')

    expect(await screen.findByRole('heading', { name: 'Home' })).toBeInTheDocument()
    expect(screen.getAllByText('Alpha').length).toBeGreaterThan(0)
  })

  it('redirects / to /home when authenticated', async () => {
    sessionStorage.setItem(SESSION_STORAGE_KEY, 'session-test')
    useSessionMock.mockReturnValue({ data: mockSession, isLoading: false, isError: false })

    renderAt('/')

    expect(await screen.findByRole('heading', { name: 'Home' })).toBeInTheDocument()
  })

  it('renders 404 page for unknown protected routes', async () => {
    sessionStorage.setItem(SESSION_STORAGE_KEY, 'session-test')
    useSessionMock.mockReturnValue({ data: mockSession, isLoading: false, isError: false })

    renderAt('/missing-route')

    expect(await screen.findByText('Page not found')).toBeInTheDocument()
  })
})

describe('login page', () => {
  beforeEach(() => {
    sessionStorage.clear()
    loginMock.mockReset()
    useSessionMock.mockReturnValue({ data: undefined, isLoading: false, isError: true })
  })

  it('submits animal name and passkey', async () => {
    const user = userEvent.setup()
    renderAt('/login')

    await user.selectOptions(screen.getByLabelText('Character'), 'Alpha')
    await user.type(screen.getByLabelText('Passkey'), 'jungle')
    await user.click(screen.getByRole('button', { name: 'Step Inside' }))

    await waitFor(() => {
      expect(loginMock).toHaveBeenCalledWith(
        { animalName: 'Alpha', passkey: 'jungle' },
        expect.objectContaining({ onSuccess: expect.any(Function) }),
      )
    })
  })
})
