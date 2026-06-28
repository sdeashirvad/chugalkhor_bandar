import { useQuery } from '@tanstack/react-query'
import { fetchTerritories, fetchTerritoryById } from '@/api/territories'

export function useTerritories() {
  return useQuery({
    queryKey: ['territories'],
    queryFn: fetchTerritories,
  })
}

export function useTerritory(id: string | undefined) {
  return useQuery({
    queryKey: ['territories', id],
    queryFn: () => fetchTerritoryById(id!),
    enabled: Boolean(id),
  })
}
