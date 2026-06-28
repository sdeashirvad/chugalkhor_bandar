import { apiClient } from '@/api/client'
import type { PlaceDetails, PlaceSummary } from '@/types/api'

export async function fetchPlaces(): Promise<PlaceSummary[]> {
  const { data } = await apiClient.get<PlaceSummary[]>('/api/places')
  return data
}

export async function fetchPlaceById(id: string): Promise<PlaceDetails> {
  const { data } = await apiClient.get<PlaceDetails>(`/api/places/${id}`)
  return data
}
