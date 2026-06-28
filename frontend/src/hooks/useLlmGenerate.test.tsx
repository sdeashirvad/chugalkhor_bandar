import { renderHook, waitFor } from '@testing-library/react'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import { apiClient } from '@/api/client'
import { useLlmGenerate } from '@/hooks/useLlmGenerate'

describe('useLlmGenerate', () => {
  beforeEach(() => {
    vi.restoreAllMocks()
  })

  function wrapper({ children }: { children: React.ReactNode }) {
    const queryClient = new QueryClient({ defaultOptions: { queries: { retry: false } } })
    return <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>
  }

  it('posts latest message to llm generate endpoint', async () => {
    vi.spyOn(apiClient, 'post').mockResolvedValue({
      data: {
        provider: {
          type: 'MOCK',
          name: 'Mock Bandar',
          description: 'Developer inspection provider',
          healthy: true,
          model: 'mock-bandar',
        },
        request: {
          messages: [],
          metadata: {},
          temperature: 0.7,
          maxOutputTokens: 1024,
          model: 'mock-bandar',
        },
        response: {
          reply: '[Mock Bandar]',
          tokenUsage: { promptTokens: 1, completionTokens: 1, totalTokens: 2 },
          providerMetadata: {},
          latencyMs: 1,
          finishReason: 'stop',
        },
      },
    } as never)

    const { result } = renderHook(() => useLlmGenerate(), { wrapper })

    result.current.mutate({ latestMessage: 'Where am I?' })

    await waitFor(() => expect(result.current.isSuccess).toBe(true))
    expect(apiClient.post).toHaveBeenCalledWith('/api/llm/generate', { latestMessage: 'Where am I?' })
  })
})
