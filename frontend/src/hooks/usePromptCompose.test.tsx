import { renderHook, waitFor } from '@testing-library/react'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import { apiClient } from '@/api/client'
import { usePromptCompose } from '@/hooks/usePromptCompose'

describe('usePromptCompose', () => {
  beforeEach(() => {
    vi.restoreAllMocks()
  })

  function wrapper({ children }: { children: React.ReactNode }) {
    const queryClient = new QueryClient({ defaultOptions: { queries: { retry: false } } })
    return <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>
  }

  it('posts latest message to compose endpoint', async () => {
    vi.spyOn(apiClient, 'post').mockResolvedValue({
      data: {
        sections: [],
        totalEstimatedTokens: 0,
        requiredSectionCount: 0,
        optionalSectionCount: 0,
        inspection: { sections: [], totalEstimatedTokens: 0, requiredSectionCount: 0, optionalSectionCount: 0 },
      },
    } as never)

    const { result } = renderHook(() => usePromptCompose(), { wrapper })

    result.current.mutate({ latestMessage: 'Where am I?' })

    await waitFor(() => expect(result.current.isSuccess).toBe(true))
    expect(apiClient.post).toHaveBeenCalledWith('/api/prompt/compose', { latestMessage: 'Where am I?' })
  })
})
