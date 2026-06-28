import { useQuery } from '@tanstack/react-query'
import { fetchWorldStatus } from '@/api/world'

export function useWorldStatus() {
  return useQuery({
    queryKey: ['world', 'status'],
    queryFn: fetchWorldStatus,
  })
}
