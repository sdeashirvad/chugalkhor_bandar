# Working Memory

Working Memory is Bandar's **temporary understanding of the ongoing conversation**. It is session-scoped, deterministic, and disposable.

It answers:

- What are we talking about?
- What questions are still unanswered?
- What entities are currently important?
- What emotional tone does this conversation have?
- What should Bandar remember for the next reply?

When the session ends, Working Memory disappears.

## Purpose

Long conversations cannot send the entire message history to the LLM on every turn. Working Memory preserves conversational coherence beyond the bounded message window by maintaining a structured cognitive snapshot.

The planner receives:

1. **Working Memory** — structured session state
2. **Last N conversation messages** — default 10 messages

Working Memory carries context that falls outside that window.

## Lifecycle

1. **Build** — `WorkingMemoryBuilder` derives state deterministically from the authenticated user, active conversation, and runtime world. No LLM is used.
2. **Update** — rebuilt automatically before each Bandar reply when a user message is appended.
3. **Persist** — one snapshot per active session, stored in the working-memory persistence context so it survives backend restarts during development.
4. **Cleanup** — removed when the session expires, when the user logs out, or when the conversation is closed.

## Structured State

Working Memory is **not** a free-text summary. It contains:

| Field | Meaning |
| --- | --- |
| `activeTopic` | Short label for the current discussion (e.g. Location, Hippu King) |
| `conversationMood` | Conversational tone (Curious, Playful, Thoughtful, etc.) |
| `currentStory` | Optional in-progress or requested story |
| `activeEntities` | Characters, places, or organizations dominating the discussion |
| `unansweredQuestions` | User questions not yet substantively answered |
| `recentPromises` | Short-lived commitments Bandar made recently |
| `importantFacts` | Temporary observations relevant only to this conversation |
| `lastUpdated` | Timestamp of the last rebuild |
| `version` | Monotonic rebuild counter |

Permanent world knowledge does **not** belong here.

## Prompt Presentation

Working Memory is rendered in the LLM prompt as **Current Train of Thought** — Bandar reminding himself, not a database dump.

Example:

```text
Current Train of Thought

We have been discussing Hippu King's palace.
The user previously asked where they are.
A story has not yet been started.
No promises remain outstanding.
```

## Developer API

| Endpoint | Description |
| --- | --- |
| `GET /api/memory/working` | Return the current Working Memory for the active session |
| `POST /api/memory/working/rebuild` | Rebuild Working Memory deterministically from the conversation |

Both endpoints are for developer use only.

The Developer Panel **Working Memory** tab displays each field and the heuristic that produced it.

## How Working Memory Differs From Permanent Memory

| Working Memory | Permanent / Long-Term Memory |
| --- | --- |
| Session-scoped | Cross-session |
| Rebuilt from recent conversation heuristics | Curated, promoted knowledge |
| Disposable at session end | Intended to persist |
| Structured cognitive scratch pad | Authoritative world or relationship knowledge |

Working Memory is Bandar's **train of thought**, not his library.

## Future Interaction With Conversation Artifacts

Later prompts will introduce **Conversation Artifacts** — durable records such as chronicle drafts or promoted memories.

Working Memory will remain the ephemeral layer:

- Artifacts may **inform** rebuild heuristics in the future.
- Working Memory will **not** automatically promote content into long-term storage.
- Session cleanup will continue to discard Working Memory even if artifacts exist elsewhere.

That separation keeps temporary reasoning distinct from permanent canon.

## Configuration

```yaml
chugalkhor:
  working-memory:
    conversation-window-messages: 10
    analysis-window-messages: 20
```

- `conversation-window-messages` — how many recent messages are sent to the LLM as `CURRENT_CONVERSATION`
- `analysis-window-messages` — how many recent messages the builder inspects when deriving Working Memory

## Package Layout

```text
application/memory/working/
  WorkingMemory.java
  WorkingMemoryBuilder.java
  WorkingMemoryNarrator.java
  WorkingMemoryService.java
  WorkingMemoryKnowledgeProvider.java
```

Persistence adapters live under `adapters/persistence/memory/` and `adapters/persistence/postgres/`.
