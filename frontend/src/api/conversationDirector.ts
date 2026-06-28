import { apiClient } from '@/api/client'

export type ConversationGoal =
  | 'GREETING'
  | 'ANSWER'
  | 'LOCATION_HELP'
  | 'IDENTITY'
  | 'STORY'
  | 'CHEER_UP'
  | 'REMEMBER'
  | 'GOODBYE'
  | 'SMALL_TALK'
  | 'QUESTION'
  | 'CONTINUE_STORY'
  | 'REMINDER'
  | 'UNKNOWN'

export type ConversationOutcome =
  | 'RESOLVED'
  | 'UNRESOLVED'
  | 'PROMISE_MADE'
  | 'QUESTION_LEFT_OPEN'
  | 'STORY_STARTED'
  | 'STORY_COMPLETED'
  | 'FOLLOW_UP_REQUIRED'

export type ConversationEnergy = 'LOW' | 'MEDIUM' | 'HIGH' | 'VERY_HIGH'

export type ConversationArc =
  | 'QUESTION_ANSWER'
  | 'QUESTION_STORY'
  | 'GREETING_REPLY'
  | 'CHEER_UP'
  | 'REMINDER'
  | 'GOODBYE'
  | 'SMALL_TALK'
  | 'STORY_CONTINUATION'

export interface ConversationPlanningTraceEntry {
  rule: string
  reason: string
}

export interface ConversationExecutionTimelineEntry {
  replyIndex: number
  event: string
  at: string
  delayMs: number
}

export interface ConversationPlanResponse {
  sessionId: string
  goal: ConversationGoal
  confidence: number
  continueConversation: boolean
  conversationEnergy: ConversationEnergy
  conversationArc: ConversationArc
  expectedMessageCount: number
  delays: number[]
  askFollowUpQuestion: boolean
  tellStory: boolean
  tellJoke: boolean
  tellMemory: boolean
  endConversation: boolean
  suggestedTone: string
  outcome: ConversationOutcome
  createdAt: string
  isInterrupted: boolean
  isCancelled: boolean
  startedAt: string | null
  completedAt: string | null
  executed: boolean
  executedMessageCount: number
  cancelledMessageCount: number
  interruptionReason: string
  timeline: ConversationExecutionTimelineEntry[]
  deliveredMessageIds: string[]
  cancelledReplyIndexes: number[]
  trace: ConversationPlanningTraceEntry[]
}

export async function getCurrentConversationPlan(): Promise<ConversationPlanResponse> {
  const { data } = await apiClient.get<ConversationPlanResponse>('/api/conversation/director/current-plan')
  return data
}
