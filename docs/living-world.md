# Living World Engine

The Living World Engine is the heartbeat of the Jungle. It advances simulated world time, runs deterministic generators, and produces **WorldEvents** that flow through the existing conversation and notification pipeline — even when no users are connected.

The Jungle is not created by conversations. Conversations merely reveal parts of the Jungle. The world already exists. It changes with time. Bandar observes those changes and sometimes tells people about them.

## Architecture

```
WorldClock (single scheduler)
        ↓
LivingWorldEngine
        ↓
Generators (Festival, Birthday, Promise, Character Initiative, Gossip)
        ↓
WorldEvents (immutable, persisted)
        ↓
ConversationArtifacts
        ↓
Living Notifications (via NotificationRepository)
        ↓
Future Conversations
```

The Living World Engine **never** generates LLM replies and **never** mutates conversations directly.

## World clock

`WorldClock` supports four modes:

| Mode | Purpose |
|------|---------|
| `HOURLY` | Runs every scheduled tick |
| `DAILY` | Runs once per world date |
| `WEEKLY` | Runs once per ISO week |
| `MANUAL` | Developer-triggered tick |

A **single** `@Scheduled` bean (`LivingWorldScheduler`) triggers only `LivingWorldService`. Sub-engines are invoked by `LivingWorldEngine` — there are no independent schedulers.

## World events

Each `WorldEvent` is immutable and persisted:

- `id`, `type`, `title`, `summary`, `participants`, `visibility`
- `createdAt`, `effectiveDate`, `metadata`, `status`, `origin`

### Event types

`FESTIVAL`, `BIRTHDAY`, `PROMISE_DUE`, `CHARACTER_ACTIVITY`, `DISCOVERY`, `ANNOUNCEMENT`, `CUSTOM`

New types can be added to the `WorldEventType` enum without changing the core engine contract.

Event IDs are deterministic: `{type}-{date}-{sourceKey}` via `WorldEventIdFactory`.

## Generators

All generators are deterministic. No randomness. No LLM.

### Festival engine

Reads bootstrap custom definitions with `festivalDate` or `upcomingFestivalDate` (`MM-dd`). When today matches, produces a `FESTIVAL` event.

### Birthday engine

Reads character `birthday` from preferences or sections (`MM-dd`). When today matches, produces a `BIRTHDAY` event.

### Promise fulfillment engine

Scans active `PROMISE` conversation artifacts. When `dueDate` or `expiresAt` falls on today, produces `PROMISE_DUE`.

### Character initiative engine

Deterministic character rules:

| Character | Rule |
|-----------|------|
| Rabbitu Minister | Requests meeting after important Rabbitu politics chronicle |
| Second Hippu | Begins new adventure after completing a previous story seed |
| Little Brother | Asks Bandar about a previous story chronicle |
| Bandar | Continues an unfinished story seed |

### Gossip engine

Consumes public chronicles, active artifacts, and festival/birthday events from the current tick. Produces `ANNOUNCEMENT` events (and `GOSSIP` artifacts). Never sends notifications directly — it feeds the pipeline.

## Notification integration

`LivingWorldNotificationBridge` saves notifications through `NotificationRepository` with source `living-world-engine` and status `PENDING`. This reuses the established Living Notification architecture; the world engine does not bypass it.

| World event type | Notification type |
|------------------|-------------------|
| `FESTIVAL` | `FESTIVAL` |
| `BIRTHDAY` | `BIRTHDAY` |
| `PROMISE_DUE` | `REMINDER` |
| `CHARACTER_ACTIVITY` | `WORLD_EVENT` |
| `ANNOUNCEMENT` (gossip) | `GOSSIP` |

## Persistence

Every tick is recorded in `WorldTickHistory`:

- Run ID, mode, timing, world date
- Events, artifacts, and notifications generated
- Executed generator names
- Trace entries

Supported backends: in-memory and PostgreSQL (`V12__living_world.sql`).

## Configuration

```yaml
chugalkhor:
  living-world:
    enabled: true
    hourly-enabled: true
    daily-enabled: true
    festival-enabled: true
    birthday-enabled: true
    promise-engine-enabled: true
    character-initiative-enabled: true
    gossip-enabled: true
    manual-tick-enabled: true
    schedule: "0 0 * * * *"
```

## APIs

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/world/events` | List world events |
| GET | `/api/world/events/{id}` | Event details |
| GET | `/api/world/events/type/{type}` | Events by type |
| GET | `/api/world/dev/latest-tick` | Latest tick (developer) |
| POST | `/api/world/dev/run-tick` | Manual tick (developer) |

## Frontend

- **Living World** page (`/living-world`) — world date, festivals, birthdays, promises, recent events, autonomous activities, manual tick
- **Developer Panel → Living World** — latest tick, generators, events, artifacts, notifications, duration, session tick history

## Package layout

```
application/world/living/
  LivingWorldEngine.java      — orchestrator
  WorldClock.java             — schedule modes
  FestivalEngine.java
  BirthdayEngine.java
  PromiseFulfillmentEngine.java
  CharacterInitiativeEngine.java
  GossipEngine.java
  LivingWorldArtifactFactory.java
  LivingWorldNotificationBridge.java
  LivingWorldService.java
  LivingWorldScheduler.java
```

## Out of scope (MVP0)

Random world simulation, economic simulation, combat, AI-controlled character conversations, and dynamic map changes are intentionally excluded. The Living World remains fully deterministic for MVP0.
