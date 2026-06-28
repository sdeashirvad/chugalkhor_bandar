import { renderHook, waitFor } from '@testing-library/react'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import { apiClient } from '@/api/client'
import { useContextResolve } from '@/hooks/useContextPlan'

describe('useContextResolve', () => {
  beforeEach(() => {
    vi.restoreAllMocks()
  })

  function wrapper({ children }: { children: React.ReactNode }) {
    const queryClient = new QueryClient({ defaultOptions: { queries: { retry: false } } })
    return <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>
  }

  it('posts latest message to resolve endpoint', async () => {
    vi.spyOn(apiClient, 'post').mockResolvedValue({
      data: {
        sections: [
          {
            type: 'PERSONALITY',
            priority: 10,
            source: 'promptProfiles',
            reference: {
              provider: 'promptProfiles',
              entityType: 'promptProfile',
              entityId: 'prompt_bandar_personality',
              attribute: 'sections',
              priority: 10,
            },
            contentReference: 'promptProfile:prompt_bandar_personality:sections',
            content: 'Bandar',
            estimatedTokens: 2,
          },
        ],
        totalEstimatedTokens: 2,
      },
    } as never)

    const { result } = renderHook(() => useContextResolve(), { wrapper })

    result.current.mutate({ latestMessage: 'Hello' })

    await waitFor(() => expect(result.current.isSuccess).toBe(true))
    expect(apiClient.post).toHaveBeenCalledWith('/api/context/resolve', { latestMessage: 'Hello' })
  })
})
