import { useQuery } from '@tanstack/react-query'
import { fetchPlaceById, fetchPlaces } from '@/api/places'

export function usePlaces() {
  return useQuery({ queryKey: ['places'], queryFn: fetchPlaces })
}

export function usePlace(id: string | undefined) {
  return useQuery({
    queryKey: ['places', id],
    queryFn: () => fetchPlaceById(id!),
    enabled: Boolean(id),
  })
}
