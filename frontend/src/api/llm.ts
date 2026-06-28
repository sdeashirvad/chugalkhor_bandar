import { apiClient } from '@/api/client'
import type { LLMGenerateRequest, LLMGenerateResponse } from '@/types/llm'

export async function generateLlmReply(request: LLMGenerateRequest): Promise<LLMGenerateResponse> {
  const { data } = await apiClient.post<LLMGenerateResponse>('/api/llm/generate', request)
  return data
}
