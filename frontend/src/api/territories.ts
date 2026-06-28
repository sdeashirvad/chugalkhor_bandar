import { apiClient } from '@/api/client'
import type { TerritoryDetails, TerritorySummary } from '@/types/api'

export async function fetchTerritories(): Promise<TerritorySummary[]> {
  const { data } = await apiClient.get<TerritorySummary[]>('/api/territories')
  return data
}

export async function fetchTerritoryById(id: string): Promise<TerritoryDetails> {
  const { data } = await apiClient.get<TerritoryDetails>(`/api/territories/${id}`)
  return data
}
