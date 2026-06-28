import { apiClient } from '@/api/client'

export interface WorkingMemoryFieldTrace {
  field: string
  value: string
  heuristic: string
}

export interface WorkingMemoryResponse {
  sessionId: string
  activeTopic: string
  conversationMood: string
  currentStory: string
  activeEntities: string[]
  unansweredQuestions: string[]
  recentPromises: string[]
  importantFacts: string[]
  lastUpdated: string
  version: number
  fieldTraces: WorkingMemoryFieldTrace[]
}

export async function getWorkingMemory(): Promise<WorkingMemoryResponse> {
  const { data } = await apiClient.get<WorkingMemoryResponse>('/api/memory/working')
  return data
}

export async function rebuildWorkingMemory(): Promise<WorkingMemoryResponse> {
  const { data } = await apiClient.post<WorkingMemoryResponse>('/api/memory/working/rebuild')
  return data
}
