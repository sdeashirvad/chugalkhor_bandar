import type { PromptComposeRequest } from '@/types/prompt'

export interface ContextProfile {
  type: string
  displayName: string
  description: string
  preferredSections: string[]
  optionalSections: string[]
  minimumRequiredSections: string[]
  reducedSections: string[]
  sectionPriorities: Record<string, number>
}

export interface PromptProfileResponse {
  profile: ContextProfile
  selectionReason: string
}

export interface SectionBudget {
  sectionType: string
  maxTokens: number
  minimumTokens: number
  priority: number
  required: boolean
}

export interface PromptBudget {
  sectionBudgets: SectionBudget[]
  totalAvailableTokens: number
  reservedOutputTokens: number
  maxContextTokens: number
}

export interface DroppedSection {
  sectionType: string
  title: string
  estimatedTokens: number
  reason: string
}

export interface BudgetedPromptSection {
  section: {
    sectionType: string
    title: string
    priority: number
    required: boolean
    estimatedTokens: number
    content: string
  }
  budget: SectionBudget
  truncated: boolean
  allocatedTokens: number
}

export interface ProviderCapabilities {
  maxContextTokens: number
  reservedOutputTokens: number
  availablePromptTokens: number
  supportsSystemMessages: boolean
  supportsMultiMessage: boolean
}

export interface PromptBudgetResponse {
  profile: ContextProfile
  selectionReason: string
  sections: BudgetedPromptSection[]
  droppedSections: DroppedSection[]
  budget: PromptBudget
  totalPromptTokens: number
  remainingBudget: number
  providerCapabilities: ProviderCapabilities
}

export type PromptProfileRequest = PromptComposeRequest
export type PromptBudgetRequest = PromptComposeRequest
