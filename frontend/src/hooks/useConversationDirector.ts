import { useQuery } from '@tanstack/react-query'
import { getCurrentConversationPlan } from '@/api/conversationDirector'

export function useConversationDirectorPlan() {
  return useQuery({
    queryKey: ['conversation-director', 'current-plan'],
    queryFn: getCurrentConversationPlan,
    retry: false,
  })
}
