import { apiClient } from '@/api/client'

export type ObservationType =
  | 'PROMISE'
  | 'REMINDER'
  | 'PREFERENCE'
  | 'RELATIONSHIP_SIGNAL'
  | 'STORY_SEED'
  | 'OPEN_QUESTION'
  | 'GOSSIP'
  | 'FACT_CANDIDATE'
  | 'EMOTION'
  | 'INTEREST'
  | 'UNKNOWN'

export type RecommendationAction =
  | 'PROMOTE_TO_MEMORY'
  | 'CREATE_NOTIFICATION'
  | 'MERGE_ARTIFACT'
  | 'IGNORE'
  | 'WAIT'

export interface ObservationResponse {
  id: string
  type: ObservationType
  confidence: number
  summary: string
  evidence: string
  metadata: Record<string, string>
  createdAt: string
}

export interface RecommendationResponse {
  id: string
  action: RecommendationAction
  confidence: number
  reason: string
  target: string
  metadata: Record<string, string>
}

export interface CognitiveAnalysisResponse {
  analysisId: string
  characterId: string
  conversationId: string
  provider: string
  model: string
  latencyMs: number
  confidence: number
  createdAt: string
  observations: ObservationResponse[]
  recommendations: RecommendationResponse[]
  rawJson: string
}

export interface CognitiveAnalysisExecutionResponse {
  characterId: string
  conversationId: string
  success: boolean
  provider: string
  model: string
  providerLatencyMs: number
  executionTimeMs: number
  confidence: number
  errorMessage: string
  completedAt: string
  result: CognitiveAnalysisResponse | null
}

export async function getLatestCognitiveAnalysis(): Promise<CognitiveAnalysisResponse> {
  const { data } = await apiClient.get<CognitiveAnalysisResponse>('/api/cognition/latest')
  return data
}

export async function getCognitiveAnalysisForConversation(
  conversationId: string,
): Promise<CognitiveAnalysisResponse> {
  const { data } = await apiClient.get<CognitiveAnalysisResponse>(`/api/cognition/${conversationId}`)
  return data
}

export async function listCognitiveObservations(): Promise<ObservationResponse[]> {
  const { data } = await apiClient.get<ObservationResponse[]>('/api/cognition/observations')
  return data
}

export async function listCognitiveRecommendations(): Promise<RecommendationResponse[]> {
  const { data } = await apiClient.get<RecommendationResponse[]>('/api/cognition/recommendations')
  return data
}

export async function getCognitiveAnalysisExecution(): Promise<CognitiveAnalysisExecutionResponse> {
  const { data } = await apiClient.get<CognitiveAnalysisExecutionResponse>('/api/cognition/dev/execution')
  return data
}

export async function listAllCognitiveAnalysesDev(): Promise<CognitiveAnalysisResponse[]> {
  const { data } = await apiClient.get<CognitiveAnalysisResponse[]>('/api/cognition/dev/all')
  return data
}
