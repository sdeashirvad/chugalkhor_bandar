import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import {
  getConsolidationExecutionDev,
  getConsolidationHistory,
  getLatestConsolidationReport,
  listAllCandidatesDev,
  runConsolidation,
} from '@/api/memoryConsolidation'

export function useLatestConsolidationReport() {
  return useQuery({
    queryKey: ['memory-consolidation', 'latest'],
    queryFn: getLatestConsolidationReport,
    retry: false,
  })
}

export function useConsolidationHistory() {
  return useQuery({
    queryKey: ['memory-consolidation', 'history'],
    queryFn: getConsolidationHistory,
    retry: false,
  })
}

export function useRunConsolidation() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: runConsolidation,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['memory-consolidation'] })
    },
  })
}

export function useConsolidationExecutionDev() {
  return useQuery({
    queryKey: ['memory-consolidation', 'dev-execution'],
    queryFn: getConsolidationExecutionDev,
    retry: false,
  })
}

export function useAllCandidatesDev() {
  return useQuery({
    queryKey: ['memory-consolidation', 'dev-candidates'],
    queryFn: listAllCandidatesDev,
    retry: false,
  })
}
