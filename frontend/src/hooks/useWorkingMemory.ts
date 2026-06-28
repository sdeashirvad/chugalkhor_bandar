import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { getWorkingMemory, rebuildWorkingMemory } from '@/api/memory'

export function useWorkingMemory() {
  return useQuery({
    queryKey: ['working-memory'],
    queryFn: getWorkingMemory,
    retry: false,
  })
}

export function useRebuildWorkingMemory() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: rebuildWorkingMemory,
    onSuccess: (data) => {
      queryClient.setQueryData(['working-memory'], data)
    },
  })
}
