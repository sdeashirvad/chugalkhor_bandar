import { useQuery } from '@tanstack/react-query'
import { getCurrentBehaviorProfile } from '@/api/behaviorEngine'

export function useBehaviorEngineProfile() {
  return useQuery({
    queryKey: ['behavior-engine', 'current'],
    queryFn: getCurrentBehaviorProfile,
    retry: false,
  })
}
