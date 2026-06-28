export interface ProviderMessage {
  role: string
  content: string
  sectionType: string | null
  metadata: Record<string, string>
}

export interface ProviderRequest {
  messages: ProviderMessage[]
  metadata: Record<string, string>
  temperature: number
  maxOutputTokens: number
  model: string
}

export interface ProviderTokenUsage {
  promptTokens: number
  completionTokens: number
  totalTokens: number
}

export interface ProviderResponse {
  reply: string
  tokenUsage: ProviderTokenUsage
  providerMetadata: Record<string, string>
  latencyMs: number
  finishReason: string
}

export interface LLMProviderInfo {
  type: string
  name: string
  description: string
  healthy: boolean
  model: string
}

export interface LLMGenerateResponse {
  provider: LLMProviderInfo
  request: ProviderRequest
  response: ProviderResponse
}

export interface LLMGenerateRequest {
  latestMessage: string
}
