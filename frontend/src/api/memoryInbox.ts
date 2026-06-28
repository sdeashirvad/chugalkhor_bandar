import { apiClient } from '@/api/client'

export type MemoryInboxSource =
  | 'CONVERSATION_ARTIFACT'
  | 'COGNITIVE_OBSERVATION'
  | 'COGNITIVE_RECOMMENDATION'
  | 'MANUAL_ADMIN'
  | 'WORLD_TICK'
  | 'CHRONICLE_REVIEW'

export type MemoryInboxStatus = 'NEW' | 'REVIEWED' | 'PROMOTED' | 'DISCARDED' | 'EXPIRED' | 'ARCHIVED'

export type MemoryInboxImportance = 'LOW' | 'MEDIUM' | 'HIGH' | 'VERY_HIGH'

export interface MemoryInboxItemResponse {
  id: string
  type: string
  source: MemoryInboxSource
  sourceId: string
  ownerCharacterId: string
  summary: string
  importance: MemoryInboxImportance
  confidence: number
  status: MemoryInboxStatus
  createdAt: string
  expiresAt: string
  metadata: Record<string, string>
  trace: string[]
  analysisId: string
  artifactIds: string[]
}

export interface MemoryInboxGenerationTraceEntry {
  rule: string
  reason: string
}

export interface MemoryInboxGenerationResponse {
  characterId: string
  conversationId: string
  generatedAt: string
  trace: MemoryInboxGenerationTraceEntry[]
  generatedItems: MemoryInboxItemResponse[]
}

export async function listMemoryInbox(): Promise<MemoryInboxItemResponse[]> {
  const { data } = await apiClient.get<MemoryInboxItemResponse[]>('/api/memory/inbox')
  return data
}

export async function getMemoryInboxItem(id: string): Promise<MemoryInboxItemResponse> {
  const { data } = await apiClient.get<MemoryInboxItemResponse>(`/api/memory/inbox/${id}`)
  return data
}

export async function reviewMemoryInboxItem(id: string): Promise<MemoryInboxItemResponse> {
  const { data } = await apiClient.post<MemoryInboxItemResponse>(`/api/memory/inbox/${id}/review`)
  return data
}

export async function discardMemoryInboxItem(id: string): Promise<MemoryInboxItemResponse> {
  const { data } = await apiClient.post<MemoryInboxItemResponse>(`/api/memory/inbox/${id}/discard`)
  return data
}

export async function listAllMemoryInboxDev(): Promise<MemoryInboxItemResponse[]> {
  const { data } = await apiClient.get<MemoryInboxItemResponse[]>('/api/memory/inbox/dev/all')
  return data
}

export async function getMemoryInboxGenerationTrace(): Promise<MemoryInboxGenerationResponse> {
  const { data } = await apiClient.get<MemoryInboxGenerationResponse>('/api/memory/inbox/dev/generation')
  return data
}
