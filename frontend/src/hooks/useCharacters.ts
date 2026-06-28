import { useQuery } from '@tanstack/react-query'
import { fetchCharacterById, fetchCharacters } from '@/api/characters'

export function useCharacters() {
  return useQuery({
    queryKey: ['characters'],
    queryFn: fetchCharacters,
  })
}

export function useCharacter(id: string | undefined) {
  return useQuery({
    queryKey: ['characters', id],
    queryFn: () => fetchCharacterById(id!),
    enabled: Boolean(id),
  })
}
