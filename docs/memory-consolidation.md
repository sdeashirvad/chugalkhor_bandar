# Memory Consolidation

The Memory Consolidation Engine represents Bandar's **sleep**.

It periodically reviews the Memory Inbox and deterministically decides what should become long-term memory, what should be discarded, and what should be prepared for the Chronicle Writer (Prompt #31).

It never blocks conversations.

## Day vs Night

```text
During the day:
  Conversation → Artifacts → Cognitive Analysis → Memory Inbox

At night:
  Memory Inbox → Review → Promote → Archive → Report → Chronicle Candidates
```

Bandar sleeps. He reflects. He remembers. He forgets.

## Consolidation Stages

| Stage | Purpose |
| --- | --- |
| DISCOVER | Collect eligible inbox items (`NEW`, `REVIEWED`) |
| FILTER | Ignore expired, discarded, already promoted |
| GROUP | Group by character, artifact, recommendation, or observation |
| DECIDE | Apply deterministic promotion rules |
| PROMOTE | Create `LongTermMemoryCandidate` |
| ARCHIVE | Archive processed inbox items |
| REPORT | Generate deterministic TXT + JSON daily report |

No LLM is required for consolidation itself.

## Promotion Rules

| Input | Rule |
| --- | --- |
| `PROMISE` | Always promote |
| `PROMOTE_TO_MEMORY` recommendation | Always promote |
| Repeated `PREFERENCE` | Promote when repeat count ≥ threshold |
| Repeated `STORY_SEED` | Promote when repeat count ≥ threshold |
| Single low-confidence observation | Discard |
| `UNKNOWN` | Discard |
| Other eligible items | Remain pending in inbox |

Thresholds are configurable under `chugalkhor.memory-consolidation.promotion-thresholds`.

## LongTermMemoryCandidate

Immutable candidates produced by consolidation. **Not yet chronicles.**

Prompt #31 (Chronicle Writer) consumes these candidates.

| Field | Meaning |
| --- | --- |
| `sourceInboxItems` | Inbox items that led to this candidate |
| `ownerCharacterId` | Character whose memory this belongs to |
| `summary` | Structured summary |
| `importance` | Value ranking |
| `reason` | Why consolidation promoted this |
| `metadata` | Includes `chronicleCandidate=true` |

## Daily Report

Each run produces:

- **TXT** — human-readable Jungle Daily Report
- **JSON** — same statistics in structured form

Both include: date, conversations, artifacts, inbox items, promoted, discarded, candidates, pending promises, unread notifications.

## Optional Bandar Reflection

When `reflection-enabled: true`, a short LLM reflection may be generated after consolidation.

Failure never affects consolidation — reflection is best-effort only.

## Email Delivery

`ReportEmailService` uses **Resend** when configured:

| Variable | Purpose |
| --- | --- |
| `MEMORY_REPORT_ENABLED` | Enable email delivery |
| `RESEND_API_KEY` | Resend API key |
| `RESEND_FROM` | Sender address |
| `MEMORY_REPORT_TO` | Recipient address |

When enabled, emails include TXT and JSON attachments plus optional Bandar reflection.

When disabled, delivery is skipped silently.

## Scheduler

Runs once daily (default: midnight cron `0 0 0 * * *`) via `@Scheduled`.

Manual trigger: `POST /api/memory/consolidation/run` (developer only, when `developer-manual-run: true`).

Async runs use a dedicated executor and do not block chat.

## Configuration

```yaml
chugalkhor:
  memory-consolidation:
    enabled: true
    schedule: "0 0 0 * * *"
    reflection-enabled: false
    email-enabled: false
    developer-manual-run: true
    promotion-thresholds:
      preference-repeat-count: 2
      story-seed-repeat-count: 2
      minimum-confidence: 0.5

memory-report:
  enabled: ${MEMORY_REPORT_ENABLED:false}
  api-key: ${RESEND_API_KEY:}
  from: ${RESEND_FROM:}
  to: ${MEMORY_REPORT_TO:}
```

## APIs

| Method | Path | Purpose |
| --- | --- | --- |
| GET | `/api/memory/consolidation/latest` | Latest report |
| GET | `/api/memory/consolidation/history` | Report history |
| POST | `/api/memory/consolidation/run` | Manual execution |
| GET | `/api/memory/consolidation/dev/execution` | Full execution trace |
| GET | `/api/memory/consolidation/dev/candidates` | All chronicle candidates |

## Persistence

- `memory_consolidation_reports` — run history, TXT/JSON, reflection, email status
- `long_term_memory_candidates` — chronicle-ready candidates

Supported in **in-memory** (dev) and **PostgreSQL** (`V9__memory_consolidation.sql`).

## Future Integration

| System | Relationship |
| --- | --- |
| Memory Inbox | Input — items reviewed and archived |
| Chronicle Writer (Prompt #31) | Consumes `LongTermMemoryCandidate` |
| Long-term memory usage | Out of scope for this prompt |

## Out of Scope

- Chronicle Writer
- World Tick
- Long-term memory usage in conversations

This prompt only produces `LongTermMemoryCandidate` records and daily reports.
