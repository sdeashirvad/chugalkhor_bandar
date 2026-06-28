import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import {
  createConversation,
  fetchConversationMessages,
  fetchCurrentConversation,
  sendConversationMessage,
} from '@/api/conversation'
import type { ConversationMessage, ConversationMessagesResponse, SendMessageRequest } from '@/types/conversation'

export function useConversation() {
  return useQuery({
    queryKey: ['conversation', 'current'],
    queryFn: fetchCurrentConversation,
    retry: false,
  })
}

export function useConversationMessages(enabled = true, pollIntervalMs: number | false = false) {
  return useQuery({
    queryKey: ['conversation', 'messages'],
    queryFn: fetchConversationMessages,
    enabled,
    retry: false,
    refetchInterval: pollIntervalMs || false,
  })
}

export function useStartConversation() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: createConversation,
    onSuccess: (conversation) => {
      queryClient.setQueryData(['conversation', 'current'], conversation)
      queryClient.invalidateQueries({ queryKey: ['conversation', 'messages'] })
    },
  })
}

export function useSendMessage() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: SendMessageRequest) => sendConversationMessage(request),
    onMutate: async (request) => {
      await queryClient.cancelQueries({ queryKey: ['conversation', 'messages'] })
      const previous = queryClient.getQueryData<ConversationMessagesResponse>(['conversation', 'messages'])
      const optimisticMessage: ConversationMessage = {
        messageId: `optimistic-${Date.now()}`,
        sender: 'USER',
        timestamp: new Date().toISOString(),
        content: request.content,
        visibility: 'PUBLIC',
        metadata: {},
      }
      queryClient.setQueryData<ConversationMessagesResponse>(['conversation', 'messages'], {
        messages: [...(previous?.messages ?? []), optimisticMessage],
      })
      return { previous }
    },
    onError: (_error, _request, context) => {
      if (context?.previous) {
        queryClient.setQueryData(['conversation', 'messages'], context.previous)
      }
    },
    onSuccess: (response) => {
      queryClient.setQueryData<ConversationMessagesResponse>(['conversation', 'messages'], {
        messages: response.messages,
      })
      queryClient.invalidateQueries({ queryKey: ['conversation', 'current'] })
    },
  })
}
