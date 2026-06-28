import { apiClient } from '@/api/client'
import type { CharacterDetails, CharacterSummary } from '@/types/api'

export async function fetchCharacters(): Promise<CharacterSummary[]> {
  const { data } = await apiClient.get<CharacterSummary[]>('/api/characters')
  return data
}

export async function fetchCharacterById(id: string): Promise<CharacterDetails> {
  const { data } = await apiClient.get<CharacterDetails>(`/api/characters/${id}`)
  return data
}
