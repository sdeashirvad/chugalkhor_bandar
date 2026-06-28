import { useMutation } from '@tanstack/react-query'
import { generateLlmReply } from '@/api/llm'
import type { LLMGenerateRequest } from '@/types/llm'

export function useLlmGenerate() {
  return useMutation({
    mutationFn: (request: LLMGenerateRequest) => generateLlmReply(request),
  })
}
