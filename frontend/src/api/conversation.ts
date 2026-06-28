import { apiClient } from '@/api/client'
import type {
  AppendMessagesResponse,
  ConversationMessagesResponse,
  ConversationResponse,
  SendMessageRequest,
} from '@/types/conversation'

export async function createConversation(): Promise<ConversationResponse> {
  const { data } = await apiClient.post<ConversationResponse>('/api/conversations')
  return data
}

export async function fetchCurrentConversation(): Promise<ConversationResponse> {
  const { data } = await apiClient.get<ConversationResponse>('/api/conversations/current')
  return data
}

export async function fetchConversationMessages(): Promise<ConversationMessagesResponse> {
  const { data } = await apiClient.get<ConversationMessagesResponse>('/api/conversations/current/messages')
  return data
}

export async function sendConversationMessage(request: SendMessageRequest): Promise<AppendMessagesResponse> {
  const { data } = await apiClient.post<AppendMessagesResponse>('/api/conversations/current/messages', request)
  return data
}
