import { useQuery } from '@tanstack/react-query'
import {
  getCognitiveAnalysisExecution,
  getLatestCognitiveAnalysis,
  listAllCognitiveAnalysesDev,
  listCognitiveObservations,
  listCognitiveRecommendations,
} from '@/api/cognitiveAnalysis'

export function useLatestCognitiveAnalysis() {
  return useQuery({
    queryKey: ['cognition', 'latest'],
    queryFn: getLatestCognitiveAnalysis,
    retry: false,
  })
}

export function useCognitiveObservations() {
  return useQuery({
    queryKey: ['cognition', 'observations'],
    queryFn: listCognitiveObservations,
    retry: false,
  })
}

export function useCognitiveRecommendations() {
  return useQuery({
    queryKey: ['cognition', 'recommendations'],
    queryFn: listCognitiveRecommendations,
    retry: false,
  })
}

export function useCognitiveAnalysisExecution() {
  return useQuery({
    queryKey: ['cognition', 'dev-execution'],
    queryFn: getCognitiveAnalysisExecution,
    retry: false,
  })
}

export function useAllCognitiveAnalysesDev() {
  return useQuery({
    queryKey: ['cognition', 'dev-all'],
    queryFn: listAllCognitiveAnalysesDev,
    retry: false,
  })
}
