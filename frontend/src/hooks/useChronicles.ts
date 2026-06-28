import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import {
  getChronicleWriteExecutionDev,
  listChronicles,
  writeChroniclesDev,
} from '@/api/chronicles'

export function useChronicles() {
  return useQuery({
    queryKey: ['chronicles'],
    queryFn: listChronicles,
  })
}

export function useChronicleWriteExecutionDev() {
  return useQuery({
    queryKey: ['chronicles', 'execution'],
    queryFn: getChronicleWriteExecutionDev,
    retry: false,
  })
}

export function useWriteChronicles() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: writeChroniclesDev,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['chronicles'] })
      queryClient.invalidateQueries({ queryKey: ['chronicles', 'execution'] })
    },
  })
}
