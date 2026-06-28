import { useMutation } from '@tanstack/react-query'
import { planContext, resolveContext } from '@/api/context'
import type { ContextPlanRequest } from '@/types/context'

export function useContextPlan() {
  return useMutation({
    mutationFn: (request: ContextPlanRequest) => planContext(request),
  })
}

export function useContextResolve() {
  return useMutation({
    mutationFn: (request: ContextPlanRequest) => resolveContext(request),
  })
}
