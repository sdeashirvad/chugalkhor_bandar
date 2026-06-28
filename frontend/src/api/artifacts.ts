import { apiClient } from '@/api/client'

export type ConversationArtifactType =
  | 'PROMISE'
  | 'REMINDER'
  | 'STORY_SEED'
  | 'RUMOR'
  | 'SECRET'
  | 'OPEN_QUESTION'
  | 'DELIVERY'
  | 'INVITATION'
  | 'TASK'

export type ConversationArtifactPriority = 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL'

export type ConversationArtifactStatus =
  | 'NEW'
  | 'ACTIVE'
  | 'FULFILLED'
  | 'CANCELLED'
  | 'EXPIRED'
  | 'ARCHIVED'

export interface ConversationArtifactResponse {
  id: string
  type: ConversationArtifactType
  ownerCharacterId: string
  recipientCharacterId: string
  createdByCharacterId: string
  conversationId: string
  title: string
  summary: string
  status: ConversationArtifactStatus
  priority: ConversationArtifactPriority
  createdAt: string
  updatedAt: string
  expiresAt: string
  metadata: Record<string, string>
  trace: string[]
}

export interface ConversationArtifactGenerationTraceEntry {
  rule: string
  reason: string
}

export interface ConversationArtifactGenerationResponse {
  characterId: string
  conversationId: string
  generatedAt: string
  trace: ConversationArtifactGenerationTraceEntry[]
  generatedArtifacts: ConversationArtifactResponse[]
}

export async function listArtifacts(): Promise<ConversationArtifactResponse[]> {
  const { data } = await apiClient.get<ConversationArtifactResponse[]>('/api/artifacts')
  return data
}

export async function getArtifact(id: string): Promise<ConversationArtifactResponse> {
  const { data } = await apiClient.get<ConversationArtifactResponse>(`/api/artifacts/${id}`)
  return data
}

export async function fulfillArtifact(id: string): Promise<ConversationArtifactResponse> {
  const { data } = await apiClient.post<ConversationArtifactResponse>(`/api/artifacts/${id}/fulfill`)
  return data
}

export async function cancelArtifact(id: string): Promise<ConversationArtifactResponse> {
  const { data } = await apiClient.post<ConversationArtifactResponse>(`/api/artifacts/${id}/cancel`)
  return data
}

export async function getArtifactGenerationTrace(): Promise<ConversationArtifactGenerationResponse> {
  const { data } = await apiClient.get<ConversationArtifactGenerationResponse>('/api/artifacts/dev/generation')
  return data
}

export async function listAllArtifactsDev(): Promise<ConversationArtifactResponse[]> {
  const { data } = await apiClient.get<ConversationArtifactResponse[]>('/api/artifacts/dev/all')
  return data
}
