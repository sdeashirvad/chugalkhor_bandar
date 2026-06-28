# Context Planner

The Context Planner decides what information Bandar is allowed to know before answering. It does not build prompts, call an LLM, or retrieve full text—it produces a **Context Plan** made of ordered **references**.

## Philosophy

The LLM answers. The application decides what the LLM is allowed to know.

Messages and conversation history belong to the Conversation Engine. The Context Planner sits upstream of any future prompt builder: it selects *which* slices of world, character, and chat context may be loaded—not how they are worded in a prompt.

## Context Plan

A `ContextPlan` contains:

| Field | Description |
|-------|-------------|
| `sections` | Ordered list of `ContextSection` references |
| `totalEstimatedTokens` | Sum of section token estimates (no budgeting yet) |
| `trace` | Debug trace explaining why each section type was selected |

No prompt text appears in the plan.

## Context Section

Each `ContextSection` is immutable:

| Field | Description |
|-------|-------------|
| `type` | Section category (see below) |
| `priority` | Lower number = earlier in plan |
| `source` | Subsystem that owns the reference (e.g. `canon`, `conversationEngine`) |
| `contentReference` | Opaque pointer (e.g. `character:character_alpha`, `canon:canon_main`) |
| `estimatedTokens` | Rough estimate from reference length ÷ 4 |

## Section Types

| Type | Purpose |
|------|---------|
| `PERSONALITY` | Bandar voice / prompt profile |
| `WORLD_CANON` | Stable world truths |
| `WORLD_STATE` | Runtime counts / readiness |
| `CURRENT_CHARACTER` | Logged-in character identity |
| `CURRENT_LOCATION` | Character's current place |
| `RELATIONSHIPS` | Character relationship graph |
| `CURRENT_CONVERSATION` | Active chat window |
| `SESSION_SUMMARY` | Session-level summary (stub) |
| `PUBLIC_EVENTS` | Timeline / public events (stub) |
| `LONG_TERM_MEMORY` | Persistent memory (stub) |
| `SECRET_MEMORY` | Private memory (stub) |
| `RELEVANT_STORIES` | Story references |
| `PROMPT_RULES` | World rules for prompting |
| `UNKNOWN` | Fallback |

## Deterministic Rules (MVP0)

Keyword matching on the latest user message (case-insensitive):

| Trigger | Sections added |
|---------|----------------|
| Always | `PERSONALITY`, `WORLD_CANON`, `CURRENT_CONVERSATION` |
| `"where"` | `CURRENT_LOCATION` |
| `"story"` | `RELEVANT_STORIES` |
| `"remember"` | `LONG_TERM_MEMORY` |
| `"king"` | `RELATIONSHIPS`, `CURRENT_CHARACTER` |

No LLM, embeddings, or semantic search.

## Context Providers

The planner orchestrates providers; it does not load content itself.

| Provider | Types |
|----------|-------|
| `PersonalityContextProvider` | `PERSONALITY`, `PROMPT_RULES` |
| `ConversationContextProvider` | `CURRENT_CONVERSATION`, `SESSION_SUMMARY` |
| `WorldContextProvider` | `WORLD_CANON`, `WORLD_STATE`, `PUBLIC_EVENTS` |
| `CharacterContextProvider` | `CURRENT_CHARACTER`, `CURRENT_LOCATION` |
| `RelationshipContextProvider` | `RELATIONSHIPS` |
| `StoryContextProvider` | `RELEVANT_STORIES` |
| `MemoryContextProvider` | `LONG_TERM_MEMORY`, `SECRET_MEMORY` |

Each provider returns `ContextSection` references only.

## Planning Trace

`ContextPlanningTrace` records one entry per selected section type with a human-readable reason, e.g.:

- `PERSONALITY` — Always included
- `CURRENT_LOCATION` — User asked about location ("where")

Useful for debugging and the Developer panel.

## API

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/api/context/plan` | Body: `{ latestMessage }` → `ContextPlan` |

Requires a valid session. Developer-oriented; not used by the chat UI in MVP0.

## Frontend

**Developer** → **Context Plan** tab at `/dev`:

- Enter a sample user message
- View ordered sections, token estimates, and trace reasons
- No prompt text displayed

## Future: RAG, Memory, Budgeting

Later phases can:

1. **RAG / embeddings** — Replace keyword rules with retrieval scores while keeping the same `ContextPlan` shape.
2. **Memory** — Implement `LONG_TERM_MEMORY` and `SECRET_MEMORY` providers with real stores.
3. **Summarization** — Populate `SESSION_SUMMARY` from conversation windows.
4. **Token budgeting** — Trim or drop sections by priority when `totalEstimatedTokens` exceeds a limit.
5. **Prompt builder** — Consume `ContextPlan` references and resolve them into prompt sections (separate subsystem).

The planner's output should remain stable; only selection logic and providers evolve.
