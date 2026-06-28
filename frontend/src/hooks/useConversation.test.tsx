import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { renderHook, waitFor } from '@testing-library/react'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import { apiClient } from '@/api/client'
import { useSendMessage, useStartConversation } from '@/hooks/useConversation'

describe('useConversation hooks', () => {
  beforeEach(() => {
    vi.restoreAllMocks()
  })

  function wrapper({ children }: { children: React.ReactNode }) {
    const queryClient = new QueryClient({ defaultOptions: { queries: { retry: false } } })
    return <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>
  }

  it('useStartConversation stores current conversation', async () => {
    vi.spyOn(apiClient, 'post').mockResolvedValue({
      data: {
        conversationId: 'c1',
        sessionId: 's1',
        currentCharacter: {
          id: 'char1',
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

    const { result } = renderHook(() => useStartConversation(), { wrapper })

    result.current.mutate()

    await waitFor(() => expect(result.current.isSuccess).toBe(true))
    expect(result.current.data?.conversationId).toBe('c1')
  })

  it('useSendMessage posts message content', async () => {
    vi.spyOn(apiClient, 'post').mockResolvedValue({
      data: {
        messages: [
          {
            messageId: 'm1',
            sender: 'USER',
            timestamp: '2026-06-27T12:00:00Z',
            content: 'Hello',
            visibility: 'PUBLIC',
            metadata: {},
          },
          {
            messageId: 'm2',
            sender: 'BANDAR',
            timestamp: '2026-06-27T12:00:01Z',
            content: 'I heard you.',
            visibility: 'PUBLIC',
            metadata: {},
          },
        ],
      },
    } as never)

    const { result } = renderHook(() => useSendMessage(), { wrapper })

    result.current.mutate({ content: 'Hello' })

    await waitFor(() => expect(result.current.isSuccess).toBe(true))
    expect(result.current.data?.messages[1].content).toBe('I heard you.')
  })
})
