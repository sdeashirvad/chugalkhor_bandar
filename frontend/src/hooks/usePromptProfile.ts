import { useMutation } from '@tanstack/react-query'
import { allocatePromptBudget, selectPromptProfile } from '@/api/prompt'
import type { PromptBudgetRequest, PromptProfileRequest } from '@/types/promptProfile'

export function usePromptProfile() {
  return useMutation({
    mutationFn: (request: PromptProfileRequest) => selectPromptProfile(request),
  })
}

export function usePromptBudget() {
  return useMutation({
    mutationFn: (request: PromptBudgetRequest) => allocatePromptBudget(request),
  })
}
