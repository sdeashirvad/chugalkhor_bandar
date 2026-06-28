import { apiClient } from '@/api/client'
import type { ContextPlanRequest, ContextPlanResponse, ResolvedContextResponse } from '@/types/context'

export async function planContext(request: ContextPlanRequest): Promise<ContextPlanResponse> {
  const { data } = await apiClient.post<ContextPlanResponse>('/api/context/plan', request)
  return data
}

export async function resolveContext(request: ContextPlanRequest): Promise<ResolvedContextResponse> {
  const { data } = await apiClient.post<ResolvedContextResponse>('/api/context/resolve', request)
  return data
}
