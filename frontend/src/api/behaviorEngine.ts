import { apiClient } from '@/api/client'

export type OpeningStyle =
  | 'GREETING'
  | 'OBSERVATION'
  | 'QUESTION'
  | 'MEMORY'
  | 'JOKE'
  | 'DIRECT'

export type NarrationStyle =
  | 'DIRECT'
  | 'STORY'
  | 'ANALOGY'
  | 'HISTORICAL'
  | 'PLAYFUL'
  | 'REFLECTIVE'

export type HumorLevel = 'OFF' | 'LIGHT' | 'MEDIUM'

export type CuriosityLevel = 'LOW' | 'MEDIUM' | 'HIGH'

export type EndingStyle = 'NONE' | 'QUESTION' | 'REFLECTION' | 'INVITATION' | 'PROMISE'

export type ConversationFlavor =
  | 'COZY'
  | 'CURIOUS'
  | 'NOSTALGIC'
  | 'ADVENTUROUS'
  | 'CALM'
  | 'CELEBRATORY'
  | 'MYSTERIOUS'

export type EnergyModifier = 'SUBDUED' | 'STEADY' | 'LIVELY'

export type StorytellingPreference = 'MINIMAL' | 'BALANCED' | 'STRONG'

export interface BehaviorPlanningTraceEntry {
  rule: string
  reason: string
}

export interface BehaviorProfileResponse {
  sessionId: string
  openingStyle: OpeningStyle
  narrationStyle: NarrationStyle
  humorLevel: HumorLevel
  curiosityLevel: CuriosityLevel
  endingStyle: EndingStyle
  conversationFlavor: ConversationFlavor
  energyModifier: EnergyModifier
  storytellingPreference: StorytellingPreference
  createdAt: string
  trace: BehaviorPlanningTraceEntry[]
}

export async function getCurrentBehaviorProfile(): Promise<BehaviorProfileResponse> {
  const { data } = await apiClient.get<BehaviorProfileResponse>('/api/behavior/current')
  return data
}
