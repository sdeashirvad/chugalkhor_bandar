import { apiClient } from '@/api/client'

export interface WorldEventResponse {
  id: string
  type: string
  title: string
  summary: string
  participants: string[]
  visibility: string
  createdAt: string
  effectiveDate: string
  metadata: Record<string, string>
  status: string
  origin: string
}

export interface LivingWorldTraceEntryResponse {
  generator: string
  rule: string
  reason: string
}

export interface LivingWorldTickResponse {
  runId: string
  mode: string
  startedAt: string
  completedAt: string
  durationMs: number
  worldDate: string
  eventsGenerated: number
  artifactsGenerated: number
  notificationsGenerated: number
  events: WorldEventResponse[]
  artifactIds: string[]
  notificationIds: string[]
  trace: LivingWorldTraceEntryResponse[]
}

export async function listWorldEvents(): Promise<WorldEventResponse[]> {
  const { data } = await apiClient.get<WorldEventResponse[]>('/api/world/events')
  return data
}

export async function getLatestWorldTickDev(): Promise<LivingWorldTickResponse> {
  const { data } = await apiClient.get<LivingWorldTickResponse>('/api/world/dev/latest-tick')
  return data
}

export async function runWorldTickDev(): Promise<LivingWorldTickResponse> {
  const { data } = await apiClient.post<LivingWorldTickResponse>('/api/world/dev/run-tick')
  return data
}
