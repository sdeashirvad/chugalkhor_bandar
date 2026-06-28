import { renderHook, waitFor } from '@testing-library/react'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import { apiClient } from '@/api/client'
import { usePromptBudget, usePromptProfile } from '@/hooks/usePromptProfile'

describe('usePromptProfile', () => {
  beforeEach(() => {
    vi.restoreAllMocks()
  })

  function wrapper({ children }: { children: React.ReactNode }) {
    const queryClient = new QueryClient({ defaultOptions: { queries: { retry: false } } })
    return <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>
  }

  it('posts latest message to profile endpoint', async () => {
    vi.spyOn(apiClient, 'post').mockResolvedValue({
      data: {
        profile: {
          type: 'LOCATION_QUERY',
          displayName: 'Location Query',
          description: 'Location profile',
          preferredSections: [],
          optionalSections: [],
          minimumRequiredSections: [],
          reducedSections: [],
          sectionPriorities: {},
        },
        selectionReason: 'User message contains "where"',
      },
    } as never)

    const { result } = renderHook(() => usePromptProfile(), { wrapper })

    result.current.mutate({ latestMessage: 'Where am I?' })

    await waitFor(() => expect(result.current.isSuccess).toBe(true))
    expect(apiClient.post).toHaveBeenCalledWith('/api/prompt/profile', { latestMessage: 'Where am I?' })
  })
})

describe('usePromptBudget', () => {
  beforeEach(() => {
    vi.restoreAllMocks()
  })

  function wrapper({ children }: { children: React.ReactNode }) {
    const queryClient = new QueryClient({ defaultOptions: { queries: { retry: false } } })
    return <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>
  }

  it('posts latest message to budget endpoint', async () => {
    vi.spyOn(apiClient, 'post').mockResolvedValue({
      data: {
        profile: {
          type: 'LOCATION_QUERY',
          displayName: 'Location Query',
          description: 'Location profile',
          preferredSections: [],
          optionalSections: [],
          minimumRequiredSections: [],
          reducedSections: [],
          sectionPriorities: {},
        },
        selectionReason: 'User message contains "where"',
        sections: [],
        droppedSections: [],
        budget: {
          sectionBudgets: [],
          totalAvailableTokens: 7168,
          reservedOutputTokens: 1024,
          maxContextTokens: 8192,
        },
        totalPromptTokens: 100,
        remainingBudget: 7068,
        providerCapabilities: {
          maxContextTokens: 8192,
          reservedOutputTokens: 1024,
          availablePromptTokens: 7168,
          supportsSystemMessages: true,
          supportsMultiMessage: true,
        },
      },
    } as never)

    const { result } = renderHook(() => usePromptBudget(), { wrapper })

    result.current.mutate({ latestMessage: 'Where am I?' })

    await waitFor(() => expect(result.current.isSuccess).toBe(true))
    expect(apiClient.post).toHaveBeenCalledWith('/api/prompt/budget', { latestMessage: 'Where am I?' })
  })
})
