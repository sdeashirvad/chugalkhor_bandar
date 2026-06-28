export interface ContextReference {
  provider: string
  entityType: string
  entityId: string
  attribute: string
  priority: number
}

export interface ContextSection {
  type: string
  priority: number
  source: string
  contentReference: string
  estimatedTokens: number
  reference?: ContextReference
}

export interface ContextPlanningTraceEntry {
  type: string
  reason: string
}

export interface ContextPlanResponse {
  sections: ContextSection[]
  totalEstimatedTokens: number
  trace: {
    entries: ContextPlanningTraceEntry[]
  }
}

export interface ResolvedContextSection {
  type: string
  priority: number
  source: string
  reference: ContextReference
  contentReference: string
  content: string
  estimatedTokens: number
}

export interface KnowledgeFragment {
  fragmentId: string
  fragmentType: string
  title: string
  content: string
  sourceDocument: string
  sourceSection: string
  estimatedTokens: number
  tags: string[]
  confidence: number
  selectionReason: string
}

export interface ResolvedContextResponse {
  sections: ResolvedContextSection[]
  fragments: KnowledgeFragment[]
  totalEstimatedTokens: number
}

export interface ContextPlanRequest {
  latestMessage: string
}
