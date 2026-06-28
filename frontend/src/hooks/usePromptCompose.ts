import { useMutation } from '@tanstack/react-query'
import { composePrompt } from '@/api/prompt'
import type { PromptComposeRequest } from '@/types/prompt'

export function usePromptCompose() {
  return useMutation({
    mutationFn: (request: PromptComposeRequest) => composePrompt(request),
  })
}
