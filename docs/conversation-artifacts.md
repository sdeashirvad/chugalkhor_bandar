# Conversation Artifacts

Conversation Artifacts represent **unfinished intentions** created during conversations.

Artifacts are **not** memories, **not** notifications, and **not** chronicles. They are temporary pieces of unfinished business that Bandar (or another character) is carrying.

## What Each Layer Answers

| Layer | Question |
| --- | --- |
| Working Memory | What am I currently thinking about? |
| Conversation Artifacts | What still needs my attention? |
| Long-term Memory (future) | What facts do we know? |
| Chronicles (future) | What story does history tell? |

Memories are facts. Artifacts are intentions.

## Artifact Model

| Field | Meaning |
| --- | --- |
| `type` | e.g. `PROMISE`, `REMINDER`, `STORY_SEED`, `OPEN_QUESTION` |
| `ownerCharacterId` | Character responsible for the intention |
| `recipientCharacterId` | Character the intention involves |
| `createdByCharacterId` | Who initiated the artifact |
| `conversationId` | Source conversation |
| `title` / `summary` | Structured labels (no generated prose) |
| `status` | Lifecycle status |
| `priority` | Ordering for future processing (not wording) |
| `metadata` / `trace` | Structured observability data |

## Ownership

Every artifact has an owner and a recipient.

| Scenario | Owner | Recipient |
| --- | --- | --- |
| Bandar promises Little Brother | Bandar | Little Brother |
| Hippu King asks "Remind me tomorrow" | Hippu King | Bandar |

Ownership is assigned deterministically by the Conversation Artifact Engine.

## Lifecycle

```text
NEW → ACTIVE → FULFILLED
            ↘ CANCELLED → ARCHIVED
            ↘ EXPIRED → ARCHIVED
```

Artifacts never disappear immediately. Lifecycle transitions remain observable in status and trace.

## Deterministic Creation Rules

No LLM. Initial rules:

| Trigger | Artifact Type |
| --- | --- |
| User asks "remind me" | `REMINDER` |
| Conversation outcome `PROMISE_MADE` | `PROMISE` |
| Conversation outcome `STORY_STARTED` | `STORY_SEED` |
| Conversation outcome `FOLLOW_UP_REQUIRED` | `OPEN_QUESTION` |

If no rule matches, **nothing is invented**.

## Integration Point

After each completed conversation turn:

```text
ConversationService.appendUserMessage()
    → Conversation Director plans
    → Plan Executor delivers Bandar replies
    → ConversationArtifactService.processCompletedTurn()
        → ConversationArtifactEngine (deterministic)
        → ArtifactRepository (persist)
```

Future systems (Living Notifications, AI Cognitive Analyst, Memory Inbox, Chronicle Writer) must **consume artifacts** instead of reading conversations directly.

## Persistence

- Character-scoped, not session-scoped
- Survives logout
- In-memory (default dev) or PostgreSQL (`V6__conversation_artifacts.sql`)

## Configuration

```yaml
chugalkhor:
  artifacts:
    artifact-expiration-days: 30
    maximum-active-artifacts: 20
```

## API

| Endpoint | Description |
| --- | --- |
| `GET /api/artifacts` | Artifacts relevant to current character |
| `GET /api/artifacts/{id}` | Artifact details |
| `POST /api/artifacts/{id}/fulfill` | Mark fulfilled |
| `POST /api/artifacts/{id}/cancel` | Cancel and archive |
| `GET /api/artifacts/dev/generation` | Latest generation trace |
| `GET /api/artifacts/dev/all` | All artifacts for character (developer) |

## Package Layout

```text
application/artifacts/
  ConversationArtifact.java
  ConversationArtifactEngine.java
  ConversationArtifactService.java
  ConversationArtifactProperties.java
```
