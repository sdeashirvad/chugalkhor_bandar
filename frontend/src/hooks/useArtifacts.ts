import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import {
  cancelArtifact,
  fulfillArtifact,
  getArtifactGenerationTrace,
  listAllArtifactsDev,
  listArtifacts,
} from '@/api/artifacts'

export function useArtifacts() {
  return useQuery({
    queryKey: ['artifacts'],
    queryFn: listArtifacts,
  })
}

export function useFulfillArtifact() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: fulfillArtifact,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['artifacts'] })
    },
  })
}

export function useCancelArtifact() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: cancelArtifact,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['artifacts'] })
    },
  })
}

export function useArtifactGenerationTrace() {
  return useQuery({
    queryKey: ['artifacts', 'dev-generation'],
    queryFn: getArtifactGenerationTrace,
    retry: false,
  })
}

export function useAllArtifactsDev() {
  return useQuery({
    queryKey: ['artifacts', 'dev-all'],
    queryFn: listAllArtifactsDev,
    retry: false,
  })
}
