# Conversation Engine

The Conversation Engine owns every message exchanged between a Jungle character and Bandar. It is application infrastructure—not an LLM concern.

## Philosophy: Conversation Ownership

Messages belong to the application. The LLM (when added later) only generates replies; the Conversation Engine owns history, ordering, persistence, and retrieval.

This separation keeps chat history stable even when models, prompts, or providers change.

## Domain Model

### Conversation

A conversation links a browser session to an ordered message history:

| Field | Description |
|-------|-------------|
| `conversationId` | Stable conversation identifier |
| `sessionId` | Browser session that owns this conversation |
| `currentCharacter` | Character identity snapshot at conversation start |
| `startedAt` | When the conversation was created |
| `lastActivity` | Updated when messages are appended |
| `status` | `ACTIVE` or `CLOSED` |

One active conversation per session. Starting again returns the existing active conversation.

### ConversationMessage (immutable)

| Field | Description |
|-------|-------------|
| `messageId` | Unique message identifier |
| `sender` | `USER`, `BANDAR`, or `SYSTEM` |
| `timestamp` | When the message was recorded |
| `content` | Message text |
| `visibility` | `PUBLIC`, `PRIVATE`, or `SYSTEM` (future-proof for secrets) |
| `metadata` | Extensible key-value bag |

Messages are append-only. They are never mutated after creation.

## Message Lifecycle

1. User opens Chat → frontend ensures an active conversation exists (`POST /api/conversations` if needed).
2. User sends a message → `POST /api/conversations/current/messages`.
3. Engine appends a `USER` message with `PUBLIC` visibility.
4. Engine appends a hardcoded `BANDAR` echo (`"I heard you."`) for MVP0—no LLM.
5. Both messages persist immediately.
6. Frontend refetches the full message list and auto-scrolls.

## Conversation Window

`ConversationWindow` supports future context planning without summarization today:

| Responsibility | MVP0 behavior |
|----------------|---------------|
| Last N messages | Returns the most recent messages up to a limit (default 50) |
| Current turn | `USER` if last message was `BANDAR`/`SYSTEM`; otherwise `BANDAR` |
| Token estimate | Simple heuristic: total characters ÷ 4 |

No summarization in this phase. The window model exists so a future context planner can request a bounded slice of history.

## API Endpoints

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/api/conversations` | Start (or return) active conversation |
| `GET` | `/api/conversations/current` | Current conversation metadata |
| `POST` | `/api/conversations/current/messages` | Append user message + echo reply |
| `GET` | `/api/conversations/current/messages` | Full ordered message history |

All endpoints require a valid session (`X-Chugalkhor-Session` header or session cookie).

## Persistence

Conversations and messages persist through repository ports:

- `ConversationRepository` — conversation metadata
- `ConversationMessageRepository` — ordered messages

Implementations exist for in-memory (dev) and Postgres (postgres-dev). Flyway migration `V3__conversations.sql` creates `conversations` and `conversation_messages` tables.

## Frontend

The Chat page (`/chat`) is protected by the session guard. It shows a simple message list, input box, send button, loading indicator, and auto-scroll on new messages.

## Future: Summarization & LLM

Later phases can:

1. Replace the hardcoded echo with LLM-generated `BANDAR` replies.
2. Use `ConversationWindow` to feed bounded context to a prompt builder.
3. Add `PRIVATE`/`SYSTEM` messages for secrets and internal events.
4. Summarize older messages into system notes without losing the authoritative full history in persistence.

The message and conversation shapes should remain stable; only reply generation and window usage evolve.
