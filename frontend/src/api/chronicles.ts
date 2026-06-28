import { apiClient } from '@/api/client'

export interface ChronicleProvenanceLinkResponse {
  stage: string
  entityId: string
  label: string
}

export interface ChronicleProvenanceResponse {
  conversationId: string
  artifactIds: string[]
  observationIds: string[]
  inboxItemIds: string[]
  consolidationRunId: string
  candidateId: string
  chronicleId: string
  chain: ChronicleProvenanceLinkResponse[]
  metadata: Record<string, string>
}

export interface ChronicleResponse {
  id: string
  title: string
  category: string
  visibility: string
  confidence: string
  ownerCharacterId: string
  summary: string
  body: string
  createdAt: string
  chronicleDate: string
  metadata: Record<string, string>
  provenance: ChronicleProvenanceResponse
  version: number
}

export interface ChronicleWriteExecutionResponse {
  runId: string
  startedAt: string
  completedAt: string
  durationMs: number
  candidatesProcessed: number
  chroniclesWritten: number
  skipped: number
  chronicles: ChronicleResponse[]
  trace: Array<{ stage: string; rule: string; reason: string }>
}

export async function listChronicles(): Promise<ChronicleResponse[]> {
  const { data } = await apiClient.get<ChronicleResponse[]>('/api/chronicles')
  return data
}

export async function getChronicle(id: string): Promise<ChronicleResponse> {
  const { data } = await apiClient.get<ChronicleResponse>(`/api/chronicles/${id}`)
  return data
}

export async function listChroniclesByCategory(category: string): Promise<ChronicleResponse[]> {
  const { data } = await apiClient.get<ChronicleResponse[]>(`/api/chronicles/category/${category}`)
  return data
}

export async function listChroniclesByVisibility(visibility: string): Promise<ChronicleResponse[]> {
  const { data } = await apiClient.get<ChronicleResponse[]>(`/api/chronicles/visibility/${visibility}`)
  return data
}

export async function getChronicleWriteExecutionDev(): Promise<ChronicleWriteExecutionResponse> {
  const { data } = await apiClient.get<ChronicleWriteExecutionResponse>('/api/chronicles/dev/execution')
  return data
}

export async function writeChroniclesDev(): Promise<ChronicleWriteExecutionResponse> {
  const { data } = await apiClient.post<ChronicleWriteExecutionResponse>('/api/chronicles/dev/write')
  return data
}
