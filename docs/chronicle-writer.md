# Chronicle Writer

The Chronicle Writer converts trusted **LongTermMemoryCandidates** into immutable **Chronicles** — the permanent historical record of the Jungle.

It never analyzes conversations, never calls the Cognitive Analysis Engine, and never performs promotion decisions. It only transforms candidates that Memory Consolidation has already trusted.

## Memory pipeline

| Layer | Role |
|-------|------|
| **Working Memory** | Current thoughts during conversation |
| **Conversation Artifacts** | Unfinished intentions (promises, story seeds) |
| **Memory Inbox** | Candidates awaiting Bandar's review |
| **Memory Consolidation** | Promotion decisions; produces long-term candidates |
| **Chronicles** | Permanent, append-only history |

Chronicles are **forever**. Nothing else in the system represents permanent narrative history.

## Chronicle model

Each chronicle is immutable and append-only:

- `id`, `title`, `category`, `visibility`, `confidence`
- `ownerCharacterId`, `summary`, `body`
- `createdAt`, `chronicleDate`, `metadata`, `provenance`, `version`

### Categories

`PERSONAL`, `WORLD`, `RELATIONSHIP`, `PROMISE`, `DISCOVERY`, `EVENT`, `PREFERENCE`, `STORY`, `CUSTOM`

Mapped deterministically from candidate inbox type (e.g. `PROMISE` → `PROMISE`, `STORY_SEED` → `STORY`).

### Visibility

| Value | Meaning |
|-------|---------|
| `PUBLIC` | Anyone with access may read |
| `PRIVATE` | Owning character and authorized systems |
| `SECRET` | Known only to Bandar (or explicitly authorized entities) |

Visibility affects retrieval, not storage.

### Confidence

| Value | Meaning |
|-------|---------|
| `OFFICIAL` | Confirmed history (high inbox confidence) |
| `LIKELY` | Probable record |
| `RUMOR` | Uncertain historical record |

Confidence is set at creation and never changes.

## Writing rules

- Each candidate → **exactly one** chronicle
- Never merge or split chronicles
- Chronicle IDs are deterministic: `chron-{candidateId}-v{version}`
- Body text uses deterministic templates (no LLM)

Example:

> Bandar promised Hippu King that one day he would tell the story of the Lost Crown.

## Provenance

Every chronicle preserves its full origin chain:

```
Conversation → Artifact → Observation → Inbox Item → Consolidation Run → Candidate → Chronicle
```

Nothing in this chain is lost. The developer panel displays the provenance graph for each write run.

## Versioning

Chronicles are immutable. If history must be revised, create a **new** chronicle with an incremented version — never edit an existing one. Version starts at `1`.

When `future-versioning-enabled` is false, candidates that already have a chronicle are skipped on subsequent writes.

## Configuration

```yaml
chugalkhor:
  chronicles:
    enabled: true
    writer-enabled: true
    default-visibility: PRIVATE
    default-confidence: LIKELY
    allow-template-customization: false
    developer-write-enabled: true
    future-versioning-enabled: true
```

## APIs

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/chronicles` | List chronicles |
| GET | `/api/chronicles/{id}` | Chronicle details |
| GET | `/api/chronicles/category/{category}` | Filter by category |
| GET | `/api/chronicles/visibility/{visibility}` | Filter by visibility |
| GET | `/api/chronicles/dev/all` | All chronicles (developer) |
| GET | `/api/chronicles/dev/execution` | Latest write run (developer) |
| POST | `/api/chronicles/dev/write` | Write chronicles from candidates (developer) |

## Frontend

- **`/chronicles`** — Chronicle explorer grouped by category, visibility, or date; provenance viewer
- **Developer Panel → Chronicle Writer** — latest write run, generated chronicles, templates, provenance graph

## Persistence

Chronicles persist in-memory (dev) or PostgreSQL (`V11__chronicles.sql`). They survive forever unless explicitly deleted by administrators.

## Out of scope

- World Tick
- Character Initiative
- Chronicle retrieval during conversations
- Chronicle editing

This prompt implements **write-only** permanent chronicles.
