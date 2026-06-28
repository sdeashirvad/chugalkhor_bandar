import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { renderHook, waitFor } from '@testing-library/react'
import { describe, expect, it, vi } from 'vitest'
import { useWorldSearch } from '@/hooks/useWorldSearch'

vi.mock('@/hooks/useCharacters', () => ({
  useCharacters: () => ({
    isLoading: false,
    data: [{ id: 'c1', name: 'Hippu King', species: 'Hippu', titles: [], currentPlace: null, currentPlaceName: null }],
  }),
}))

vi.mock('@/hooks/useStories', () => ({
  useStories: () => ({ isLoading: false, data: [] }),
}))

vi.mock('@/hooks/useTerritories', () => ({
  useTerritories: () => ({ isLoading: false, data: [] }),
}))

vi.mock('@/hooks/usePlaces', () => ({
  usePlaces: () => ({ isLoading: false, data: [] }),
}))

vi.mock('@/hooks/useOrganizations', () => ({
  useOrganizations: () => ({ isLoading: false, data: [] }),
}))

describe('useWorldSearch', () => {
  it('filters characters by query', async () => {
    const client = new QueryClient()
    const { result } = renderHook(() => useWorldSearch('hippu'), {
      wrapper: ({ children }) => <QueryClientProvider client={client}>{children}</QueryClientProvider>,
    })

    await waitFor(() => {
      expect(result.current.results).toHaveLength(1)
      expect(result.current.results[0]?.name).toBe('Hippu King')
    })
  })
})
