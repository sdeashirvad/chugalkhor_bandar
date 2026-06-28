export interface PromptSection {
  sectionType: string
  title: string
  priority: number
  required: boolean
  estimatedTokens: number
  content: string
  fragmentId: string
  fragmentType: string
}

export interface LlmPromptMessage {
  role: string
  content: string
}

export interface PromptInspectionEntry {
  sectionType: string
  title: string
  priority: number
  required: boolean
  estimatedTokens: number
}

export interface PromptInspection {
  sections: PromptInspectionEntry[]
  totalEstimatedTokens: number
  requiredSectionCount: number
  optionalSectionCount: number
}

export interface ComposedPromptResponse {
  sections: PromptSection[]
  totalEstimatedTokens: number
  requiredSectionCount: number
  optionalSectionCount: number
  inspection: PromptInspection
  llmMessages: LlmPromptMessage[]
}

export interface PromptComposeRequest {
  latestMessage: string
}
