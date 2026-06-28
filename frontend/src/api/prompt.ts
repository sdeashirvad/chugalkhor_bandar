import { apiClient } from '@/api/client'
import type { ComposedPromptResponse, PromptComposeRequest } from '@/types/prompt'
import type { PromptBudgetResponse, PromptProfileResponse } from '@/types/promptProfile'

export async function composePrompt(request: PromptComposeRequest): Promise<ComposedPromptResponse> {
  const { data } = await apiClient.post<ComposedPromptResponse>('/api/prompt/compose', request)
  return data
}

export async function selectPromptProfile(request: PromptComposeRequest): Promise<PromptProfileResponse> {
  const { data } = await apiClient.post<PromptProfileResponse>('/api/prompt/profile', request)
  return data
}

export async function allocatePromptBudget(request: PromptComposeRequest): Promise<PromptBudgetResponse> {
  const { data } = await apiClient.post<PromptBudgetResponse>('/api/prompt/budget', request)
  return data
}
