import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { renderHook, waitFor } from '@testing-library/react'
import axios from 'axios'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import { apiClient } from '@/api/client'
import {
  clearSessionId,
  fetchCurrentSession,
  login,
  logout,
  persistSessionId,
  readSessionId,
} from '@/api/session'
import { useLogin, useLogout, useSession } from '@/hooks/useSession'

describe('session api', () => {
  beforeEach(() => {
    sessionStorage.clear()
    vi.restoreAllMocks()
  })

  it('persists session id on login', async () => {
    vi.spyOn(apiClient, 'post').mockResolvedValue({
      data: {
        sessionId: 'abc',
        currentCharacter: {
          id: 'c1',
          displayName: 'Alpha',
          titles: ['Alpha'],
          species: 'Rabbitu',
          homeTerritory: null,
        },
        startedAt: '2026-06-27T12:00:00Z',
        lastActivity: '2026-06-27T12:00:00Z',
        status: 'ACTIVE',
      },
    } as never)

    const session = await login({ animalName: 'Alpha', passkey: 'jungle' })

    expect(session.sessionId).toBe('abc')
    expect(readSessionId()).toBe('abc')
  })

  it('clears session id on logout', async () => {
    persistSessionId('abc')
    vi.spyOn(apiClient, 'post').mockResolvedValue({} as never)

    await logout()

    expect(readSessionId()).toBeNull()
  })

  it('fetchCurrentSession refreshes stored session id', async () => {
    vi.spyOn(apiClient, 'get').mockResolvedValue({
      data: { sessionId: 'fresh', currentCharacter: {}, startedAt: '', lastActivity: '', status: 'ACTIVE' },
    } as never)

    await fetchCurrentSession()

    expect(readSessionId()).toBe('fresh')
  })

  it('clears session id on 401 responses via client interceptor', async () => {
    persistSessionId('abc')
    const handlers = (apiClient.interceptors.response as unknown as { handlers: Array<{ rejected: (error: unknown) => Promise<unknown> }> })
      .handlers
    const rejected = handlers[0]?.rejected
    expect(rejected).toBeDefined()

    await expect(
      rejected!({
        response: { status: 401 },
        config: { url: '/api/session/current' },
      }),
    ).rejects.toBeTruthy()

    expect(readSessionId()).toBeNull()
  })

  it('sends session header on requests when stored', async () => {
    persistSessionId('header-session')
    const adapter = vi.fn().mockResolvedValue({ data: {}, status: 200, headers: {}, config: {} })
    const client = axios.create({ adapter })

    client.interceptors.request.use((config) => {
      const sessionId = sessionStorage.getItem('chugalkhor_session_id')
      if (sessionId) {
        config.headers.set('X-Chugalkhor-Session', sessionId)
      }
      return config
    })

    await client.get('/test')

    expect(adapter.mock.calls[0][0].headers['X-Chugalkhor-Session']).toBe('header-session')
  })
})

describe('session hooks', () => {
  beforeEach(() => {
    sessionStorage.clear()
    vi.restoreAllMocks()
  })

  function wrapper({ children }: { children: React.ReactNode }) {
    const queryClient = new QueryClient({ defaultOptions: { queries: { retry: false } } })
    return <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>
  }

  it('useSession loads current session', async () => {
    persistSessionId('s1')
    vi.spyOn(apiClient, 'get').mockResolvedValue({
      data: {
        sessionId: 's1',
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
    } as never)

    const { result } = renderHook(() => useSession(), { wrapper })

    await waitFor(() => expect(result.current.isSuccess).toBe(true))
    expect(result.current.data?.currentCharacter.displayName).toBe('Alpha')
  })

  it('useLogin stores session on success', async () => {
    vi.spyOn(apiClient, 'post').mockResolvedValue({
      data: {
        sessionId: 's2',
        currentCharacter: {
          id: 'c2',
          displayName: 'Beta',
          titles: [],
          species: 'Hippu',
          homeTerritory: null,
        },
        startedAt: '2026-06-27T12:00:00Z',
        lastActivity: '2026-06-27T12:00:00Z',
        status: 'ACTIVE',
      },
    } as never)

    const { result } = renderHook(() => useLogin(), { wrapper })

    result.current.mutate({ animalName: 'Beta', passkey: 'jungle' })

    await waitFor(() => expect(result.current.isSuccess).toBe(true))
    expect(readSessionId()).toBe('s2')
  })

  it('useLogout clears session cache', async () => {
    persistSessionId('s3')
    vi.spyOn(apiClient, 'post').mockResolvedValue({} as never)

    const { result } = renderHook(() => useLogout(), { wrapper })

    result.current.mutate()

    await waitFor(() => expect(result.current.isSuccess).toBe(true))
    expect(readSessionId()).toBeNull()
  })
})

describe('session storage helpers', () => {
  beforeEach(() => sessionStorage.clear())

  it('clearSessionId removes stored value', () => {
    persistSessionId('x')
    clearSessionId()
    expect(readSessionId()).toBeNull()
  })
})
