import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { getLatestWorldTickDev, listWorldEvents, runWorldTickDev } from '@/api/livingWorld'

export function useWorldEvents() {
  return useQuery({
    queryKey: ['living-world', 'events'],
    queryFn: listWorldEvents,
  })
}

export function useLatestWorldTickDev() {
  return useQuery({
    queryKey: ['living-world', 'latest-tick'],
    queryFn: getLatestWorldTickDev,
    retry: false,
  })
}

export function useRunWorldTick() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: runWorldTickDev,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['living-world'] })
    },
  })
}
