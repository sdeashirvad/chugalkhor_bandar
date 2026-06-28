# Knowledge Fragments

The Jungle retrieves **Knowledge Fragments** — the smallest useful units of semantic knowledge — instead of entire documents.

## Why Fragments

| Old model | New model |
|-----------|-----------|
| Planner requests `PERSONALITY` | Planner requests `IDENTITY`, `SPEAKING_STYLE`, … |
| Provider returns entire prompt profile | Provider returns one fragment per section |
| Resolver returns document blobs | Resolver returns a collection of fragments |
| Budget trims whole documents | Budget competes per fragment |

Documents still exist in bootstrap and runtime storage. Fragments are the **retrieval unit** exposed to the prompt pipeline.

## Package

`application/context/knowledge/`

## KnowledgeFragment

Immutable record:

| Field | Description |
|-------|-------------|
| `fragmentId` | Stable identifier (`source:section:type`) |
| `fragmentType` | Semantic category |
| `title` | Human-readable label |
| `content` | Fragment text |
| `sourceDocument` | Origin document or entity id |
| `sourceSection` | Origin section key |
| `estimatedTokens` | Token estimate (chars ÷ 4) |
| `tags` | Deterministic tags (`location`, `hippu`, `history`, …) |
| `confidence` | Retrieval confidence (1.0 for deterministic MVP) |

## Fragment Types

Initially supported:

- Bandar voice: `IDENTITY`, `PERSONALITY`, `SPEAKING_STYLE`, `STORYTELLING`, `HUMOR`, `SECRET_POLICY`, `CHARACTER_OPINION`
- World: `WORLD_GEOGRAPHY`, `WORLD_HISTORY`, `WORLD_POLITICS`, `WORLD_SPECIES`, `WORLD_ECONOMY`, `WORLD_TRANSPORT`, `TIMELINE`
- Character: `CHARACTER_PROFILE`, `CHARACTER_LOCATION`, `CHARACTER_TITLES`, `CHARACTER_RELATIONSHIPS`, `CHARACTER_PREFERENCES`
- Stories: `STORY_SUMMARY`
- Conversation: `CONVERSATION`
- Fallback: `UNKNOWN`

## Architecture

```
Planner
  ↓
KnowledgeFragmentSelector (deterministic keywords)
  ↓
KnowledgeFragmentPlanner
  ↓
KnowledgeFragmentResolver
  ↓
KnowledgeFragmentRegistry
  ↓
PromptComposer (one PromptSection per fragment)
  ↓
BudgetAllocator (per fragment)
```

Legacy `ContextSection` references are synthesized for backward-compatible APIs.

## Example: "Where am I?"

Selected fragments:

- `IDENTITY` — "I am Bandar…"
- `SPEAKING_STYLE` — "Speak warmly…"
- `CHARACTER_LOCATION` — "Hippu King currently lives in Hippu Palace…"
- `CHARACTER_PROFILE` — "Hippu King rules 176 jungles…"
- `CONVERSATION` — recent turns

**Not** included: dynasties, economy, rabbit history, full canon.

## Knowledge Providers

| Provider | Fragments |
|----------|-----------|
| `BandarKnowledgeProvider` | Bandar personality sections |
| `CharacterKnowledgeProvider` | Character profile, location, titles, relationships, preferences |
| `WorldKnowledgeProvider` | Canon sections mapped to world fragment types |
| `ConversationKnowledgeProvider` | Conversation window |
| `StoryKnowledgeProvider` | Story summaries |

## Fragment Registry

`KnowledgeFragmentRegistry` indexes resolved fragments by:

- type
- tag
- entity
- source document

Designed for future RAG / vector retrieval without changing the fragment shape.

## Developer API

`POST /api/context/resolve` now returns:

```json
{
  "sections": [ "...legacy grouped sections..." ],
  "fragments": [
    {
      "fragmentId": "prompt_bandar_personality:identity:IDENTITY",
      "fragmentType": "IDENTITY",
      "title": "Bandar Identity",
      "content": "I am Bandar...",
      "sourceDocument": "prompt_bandar_personality",
      "sourceSection": "identity",
      "estimatedTokens": 12,
      "tags": [],
      "confidence": 1.0,
      "selectionReason": "Always included"
    }
  ],
  "totalEstimatedTokens": 120
}
```

`POST /api/context/plan` includes `fragmentTrace` with selected fragment types and reasons.

## Frontend

The Developer Panel **Knowledge Fragments** tab replaces Resolved Context. It shows fragment type, source, tags, selection reason, tokens, and content.

## Fragment Lifecycle

1. **Select** — keyword rules choose fragment types for the user message
2. **Plan** — providers emit fragment requests with references
3. **Resolve** — providers extract section-level content into fragments
4. **Register** — registry indexes fragments for inspection
5. **Compose** — each fragment becomes its own prompt section
6. **Budget** — fragments compete independently for tokens

## Future Work

### RAG Compatibility

Fragments are independently addressable (`fragmentId`, tags, source). A future retrieval layer can populate the same `KnowledgeFragment` records from embeddings without changing composer or budget logic.

### Summarization

When budgets are tight, optional fragments may be summarized instead of dropped.

## Related Docs

- [Context Planner](context-planner.md)
- [Prompt Composer](prompt-composer.md)
- [Budget Allocation](budget-allocation.md)
