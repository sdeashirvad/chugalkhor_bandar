# Memory Inbox

The Memory Inbox is a **decision queue** for observations and artifacts that may become long-term memories.

It is **not** permanent memory.

It is **not** a chronicle.

It is **not** working memory.

Every item entering the inbox is awaiting future evaluation. Nightly Compression and Chronicle Writer (future prompts) will consume the inbox.

## What Each Layer Answers

| Layer | Question |
| --- | --- |
| Working Memory | What am I currently thinking about? |
| Conversation Artifacts | What still needs my attention? |
| Cognitive Analysis (Observations) | What did I learn? |
| **Memory Inbox** | **What should I consider keeping?** |
| Chronicles (future) | What story does history tell? |

Nothing in the inbox is guaranteed to survive. The inbox collects candidates; promotion belongs to Nightly Compression.

## Inbox Item Model

| Field | Meaning |
| --- | --- |
| `type` | Rule trigger label (e.g. `PROMISE`, `PREFERENCE`, `PROMOTE_TO_MEMORY`) |
| `source` | Where the candidate came from |
| `sourceId` | Stable id from the source system |
| `ownerCharacterId` | Character whose memory queue this belongs to |
| `summary` | Structured summary (no prompt text, no generated replies) |
| `importance` | How valuable this might be (`LOW` … `VERY_HIGH`) |
| `confidence` | How certain we are (separate from importance) |
| `status` | Lifecycle status |
| `metadata` / `trace` | Evidence and observability |
| `analysisId` | Linked cognitive analysis, when applicable |
| `artifactIds` | Linked conversation artifacts |

## Sources

| Source | Description |
| --- | --- |
| `CONVERSATION_ARTIFACT` | Unfinished intentions (e.g. promises) |
| `COGNITIVE_OBSERVATION` | Learned observations from analysis |
| `COGNITIVE_RECOMMENDATION` | Explicit promote recommendations |
| `MANUAL_ADMIN` | Reserved for future admin ingestion |
| `WORLD_TICK` | Reserved for future world events |
| `CHRONICLE_REVIEW` | Reserved for future chronicle feedback |

## Status Lifecycle

```text
NEW → REVIEWED → PROMOTED   (promotion by Nightly Compression — future)
              ↘ DISCARDED → ARCHIVED
              ↘ EXPIRED → ARCHIVED
```

Items never disappear immediately. History remains observable through status and trace.

## Deterministic Ingestion Rules

No LLM. Initial rules:

| Input | Condition | Action |
| --- | --- | --- |
| Recommendation | `PROMOTE_TO_MEMORY` | Create inbox item |
| Observation | `PREFERENCE`, confidence ≥ threshold | Create inbox item |
| Observation | `STORY_SEED` | Create inbox item |
| Observation | `UNKNOWN` | Ignore |
| Artifact | `PROMISE` | Create inbox item |

Rules are configurable via `chugalkhor.memory-inbox` properties.

## Deduplication

Deterministic only — no semantic LLM matching:

| Case | Behavior |
| --- | --- |
| Same owner + source + sourceId | Skip duplicate |
| Artifact already in a `PROMOTED` item | Skip |
| Identical observation (type + summary) | Merge artifact references |

## Integration Point

After each completed conversation turn:

```text
ConversationService.appendUserMessage()
    → ConversationArtifactService.processCompletedTurn()
    → CognitiveAnalysisTrigger (async, when enabled)
        → CognitiveAnalysisService.analyzeCompletedTurn()
            → MemoryInboxService.ingestForCompletedTurn()
```

When cognitive analysis is disabled, artifact-only ingestion runs from `ConversationArtifactService`.

## Configuration

```yaml
chugalkhor:
  memory-inbox:
    enabled: true
    minimum-confidence: 0.5
    default-expiration-days: 30
    deduplication-enabled: true
    maximum-items: 100
```

Set `enabled: false` to disable inbox ingestion entirely.

## APIs

| Method | Path | Purpose |
| --- | --- | --- |
| GET | `/api/memory/inbox` | Current inbox for session character |
| GET | `/api/memory/inbox/{id}` | Item details |
| POST | `/api/memory/inbox/{id}/review` | Mark as reviewed |
| POST | `/api/memory/inbox/{id}/discard` | Discard and archive |
| GET | `/api/memory/inbox/dev/all` | Full history (developer) |
| GET | `/api/memory/inbox/dev/generation` | Latest generation trace (developer) |

## Persistence

Inbox items persist in **in-memory** (dev) or **PostgreSQL** (`V8__memory_inbox.sql`). Items are immutable except lifecycle status transitions.

## Out of Scope (This Prompt)

- Nightly Compression
- Chronicle Writer
- Automatic promotion
- Automatic deletion
- World Tick ingestion

The inbox only collects candidates.
