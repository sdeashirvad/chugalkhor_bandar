export interface ConversationCharacter {
  id: string
  displayName: string
  titles: string[]
  species: string
  homeTerritory: string | null
}

export interface ConversationMessage {
  messageId: string
  sender: 'USER' | 'BANDAR' | 'SYSTEM'
  timestamp: string
  content: string
  visibility: string
  metadata: Record<string, string>
}

export interface ConversationResponse {
  conversationId: string
  sessionId: string
  currentCharacter: ConversationCharacter
  startedAt: string
  lastActivity: string
  status: string
}

export interface SendMessageRequest {
  content: string
}

export interface AppendMessagesResponse {
  messages: ConversationMessage[]
}

export interface ConversationMessagesResponse {
  messages: ConversationMessage[]
}
