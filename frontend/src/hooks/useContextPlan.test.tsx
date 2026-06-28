import { renderHook, waitFor } from '@testing-library/react'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import { apiClient } from '@/api/client'
import { useContextPlan } from '@/hooks/useContextPlan'

describe('useContextPlan', () => {
  beforeEach(() => {
    vi.restoreAllMocks()
  })

  function wrapper({ children }: { children: React.ReactNode }) {
    const queryClient = new QueryClient({ defaultOptions: { queries: { retry: false } } })
    return <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>
  }

  it('posts latest message to context plan endpoint', async () => {
    vi.spyOn(apiClient, 'post').mockResolvedValue({
      data: {
        sections: [],
        totalEstimatedTokens: 0,
        trace: { entries: [] },
      },
    } as never)

    const { result } = renderHook(() => useContextPlan(), { wrapper })

    result.current.mutate({ latestMessage: 'Hello' })

    await waitFor(() => expect(result.current.isSuccess).toBe(true))
    expect(apiClient.post).toHaveBeenCalledWith('/api/context/plan', { latestMessage: 'Hello' })
  })
})
