# Living Notifications

The Living Notification Engine allows Bandar to **initiate conversations**.

Notifications are generated deterministically. A notification is **not** a conversation — it is an invitation to begin one.

## Notifications vs Conversations

| Aspect | Notification | Conversation |
| --- | --- | --- |
| Purpose | Invite the user to talk | Exchange messages with Bandar |
| Content | Short title + summary invitation | Full message history |
| Generation | Deterministic rules, no LLM | Conversation Director + LLM |
| Scope | Character-scoped, survives logout | Session-scoped active chat |
| Opening | User chooses to reply in chat | User sends a message |

Example invitation summaries:

```text
Hippu King...

before we begin today...
```

Bandar does not auto-generate conversation text when a notification is opened. The user replies in chat, and the Conversation Director controls Bandar's response.

## Notification Model

| Field | Meaning |
| --- | --- |
| `id` | Unique notification id |
| `recipientCharacterId` | Character who receives the invitation |
| `type` | e.g. `GREETING`, `BIRTHDAY`, `FESTIVAL` |
| `priority` | `LOW`, `MEDIUM`, `HIGH`, `CRITICAL` — ordering only |
| `title` | Short invitation title |
| `summary` | Invitation text (not a full conversation) |
| `status` | Lifecycle status |
| `createdAt` / `expiresAt` | Timestamps |
| `source` | Always `living-notification-engine` |
| `trigger` | Rule that created the notification |
| `metadata` | Structured extras (no generated prose) |

## Lifecycle

```text
PENDING → DELIVERED → READ
                   ↘ DISMISSED
                   ↘ EXPIRED
```

- **PENDING** — created during login generation
- **DELIVERED** — visible in the Notification Center
- **READ** — user opened the invitation
- **DISMISSED** — user dismissed without continuing
- **EXPIRED** — past `expiresAt`

## Login Flow

```text
Successful login
    ↓
LivingNotificationEngine
    ↓
NotificationService persists character-scoped notifications
    ↓
Notification Center + unread badge
    ↓
User opens notification → chat (invitation shown, no auto-reply)
    ↓
User sends message → Conversation Director + LLM
```

## Deterministic Rules

Initial rules (no LLM):

| Rule | Behavior |
| --- | --- |
| Daily greeting | ~80% chance on first login of the day (deterministic hash) |
| Returning visitor | Greeting when character has not been seen recently |
| Birthday | When character has structured `birthday` metadata matching today |
| Festival | When world customs include an upcoming festival date within 7 days |
| Developer force | Config flag forces a greeting for testing |

If no rule matches, **nothing is invented** — zero notifications are produced.

## Persistence

Notifications belong to a **character**, not a browser session.

- In-memory store for default dev profile
- PostgreSQL (`V5__notifications.sql`) for `postgres-dev`
- Survive logout and are shared across sessions for the same character

## Configuration

```yaml
chugalkhor:
  notifications:
    daily-greeting-probability: 0.8
    notification-expiration-days: 7
    maximum-active-notifications: 10
    developer-force-generation: false
    recent-visit-threshold-days: 3
```

## API

| Endpoint | Description |
| --- | --- |
| `GET /api/notifications` | Current character's active notifications |
| `GET /api/notifications/unread-count` | Unread count |
| `POST /api/notifications/{id}/read` | Mark read |
| `POST /api/notifications/{id}/dismiss` | Dismiss |
| `GET /api/notifications/dev/generation` | Latest generation trace (developer) |
| `GET /api/notifications/dev/all` | All notifications for character (developer) |

## Future Integration

Later prompts will add:

- **Conversation Artifacts** — durable records that may inform notification content
- **Memory Inbox** — promise-based reminders
- **World Tick** — world-driven events

The Living Notification Engine remains the deterministic invitation layer only.

## Package Layout

```text
application/notification/
  Notification.java
  LivingNotificationEngine.java
  NotificationService.java
  NotificationProperties.java
  NotificationGenerationStore.java
```
