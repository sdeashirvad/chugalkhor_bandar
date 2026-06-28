import { apiClient } from '@/api/client'

export interface MemoryConsolidationReportResponse {
  runId: string
  startedAt: string
  completedAt: string
  durationMs: number
  processed: number
  promoted: number
  discarded: number
  expired: number
  archived: number
  pending: number
  candidateCount: number
  summary: string
  txtReport: string
  jsonReport: string
  reflection: string
  emailStatus: string
  emailError: string
}

export interface LongTermMemoryCandidateResponse {
  id: string
  sourceInboxItems: string[]
  ownerCharacterId: string
  summary: string
  importance: string
  reason: string
  createdAt: string
  runId: string
  metadata: Record<string, string>
}

export interface MemoryConsolidationTraceEntry {
  stage: string
  rule: string
  reason: string
}

export interface MemoryConsolidationDecisionResponse {
  decision: 'PROMOTE' | 'DISCARD' | 'PENDING'
  reason: string
  inboxItemIds: string[]
}

export interface MemoryConsolidationExecutionResponse {
  runId: string
  startedAt: string
  completedAt: string
  report: MemoryConsolidationReportResponse
  candidates: LongTermMemoryCandidateResponse[]
  trace: MemoryConsolidationTraceEntry[]
  decisions: MemoryConsolidationDecisionResponse[]
  reflection: string
  emailStatus: string
  emailError: string
}

export async function getLatestConsolidationReport(): Promise<MemoryConsolidationReportResponse> {
  const { data } = await apiClient.get<MemoryConsolidationReportResponse>('/api/memory/consolidation/latest')
  return data
}

export async function getConsolidationHistory(): Promise<MemoryConsolidationReportResponse[]> {
  const { data } = await apiClient.get<MemoryConsolidationReportResponse[]>('/api/memory/consolidation/history')
  return data
}

export async function runConsolidation(): Promise<MemoryConsolidationReportResponse> {
  const { data } = await apiClient.post<MemoryConsolidationReportResponse>('/api/memory/consolidation/run')
  return data
}

export async function getConsolidationExecutionDev(): Promise<MemoryConsolidationExecutionResponse> {
  const { data } = await apiClient.get<MemoryConsolidationExecutionResponse>('/api/memory/consolidation/dev/execution')
  return data
}

export async function listAllCandidatesDev(): Promise<LongTermMemoryCandidateResponse[]> {
  const { data } = await apiClient.get<LongTermMemoryCandidateResponse[]>('/api/memory/consolidation/dev/candidates')
  return data
}
