# Reporting & Delivery Engine

Prompt #30.5 adds a **delivery layer** on top of Memory Consolidation. It does not change consolidation logic, promotion rules, or conversations. It consumes `MemoryConsolidationReport` only.

## Architecture

```
MemoryConsolidationReport
        ↓
ReportingEngine  →  templates + attachments + subject
        ↓
ReportingService →  archive + deliver + retry + preview
        ↓
ResendReportDeliveryProvider (current provider)
```

Package: `backend/src/main/java/com/chugalkhorbandar/application/reporting/`

## Templates

Email templates live under `backend/src/main/resources/email/`:

| File | Purpose |
|------|---------|
| `daily-report.html` | Polished HTML email body |
| `daily-report.txt` | Plain-text email body |
| `daily-report.md` | Markdown report |
| `reflection.md` | Reflection snippet template |

Templates use `{{placeholder}}` tokens rendered by `ReportingTemplateRenderer`. Branding changes should require template edits only — no large hardcoded strings in Java.

## Configuration

### Application (`application.yml`)

```yaml
chugalkhor:
  reporting:
    enabled: true
    archive-enabled: true
    retry-enabled: true
    preview-enabled: true
    max-retries: 3
    retry-poll-ms: 60000
    subject-template: "🐒 Bandar's Morning Letter — {date}"
    closings:
      - Every creature has a story.
      - Until tomorrow.
    branding:
      greeting-name: Ashirvad
    attachments:
      txt: true
      json: true
      md: true
      html: false
```

### Environment (`.env`)

| Variable | Description |
|----------|-------------|
| `MEMORY_REPORT_ENABLED` | Enable Resend delivery |
| `RESEND_API_KEY` | Resend API key |
| `RESEND_FROM` | Sender — plain email or `Bandar <bandar@ashirvad.work>` |
| `MEMORY_REPORT_TO` | Recipients — comma or semicolon separated |
| `MEMORY_REPORT_SUBJECT` | Subject template with `{date}`, `{conversationCount}`, `{promoted}`, `{discarded}`, `{pending}` |
| `MEMORY_REPORT_ATTACH_TXT` | Attach `.txt` report |
| `MEMORY_REPORT_ATTACH_JSON` | Attach `.json` report |
| `MEMORY_REPORT_ATTACH_MD` | Attach `.md` report |
| `MEMORY_REPORT_ATTACH_HTML` | Attach `.html` report |

Invalid recipient emails are skipped with warnings; valid addresses still receive the report.

## Delivery

After consolidation completes, `MemoryConsolidationService` calls `ReportingService.processReport()` when `email-enabled` is true. Otherwise it archives only.

Each recipient is delivered independently via Resend. Every attempt is persisted to `delivery_history` — history is never overwritten.

## Archive

Every generated report is archived with HTML, TXT, JSON, and Markdown content. Archives remain downloadable even if email delivery fails.

Tables: `report_archives` (migration `V10__reporting.sql`).

## Retry Policy

On failure, retries are scheduled with escalating delays:

1. **1 minute** after attempt 1
2. **5 minutes** after attempt 2

Maximum **3 attempts** per recipient. Each attempt is recorded in delivery history. The retry scheduler polls every 60 seconds (`chugalkhor.reporting.retry-poll-ms`).

## Preview APIs (developer)

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/reporting/preview/html` | HTML preview (no send) |
| GET | `/api/reporting/preview/txt` | TXT preview |
| GET | `/api/reporting/preview/json` | JSON preview |
| GET | `/api/reporting/preview/md` | Markdown preview |

Uses the latest consolidation report.

## Test Email

`POST /api/reporting/send-test` — sends using the latest consolidation report. Does not create a new consolidation run.

## History & Archive APIs

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/reporting/history` | All delivery attempts |
| GET | `/api/reporting/history/{id}` | Single delivery entry |
| GET | `/api/reporting/archive` | All archived reports |
| GET | `/api/reporting/archive/{reportId}` | Single archive |
| GET | `/api/reporting/dev/configuration` | Current reporting config |

## Frontend

- **`/reporting`** — Reporting console (history, archive, previews, send test)
- **Developer Panel → Reporting & Delivery** — configuration, retry history, provider response, archive

## Out of Scope

This engine does **not** modify:

- Memory Consolidation logic
- Chronicle Writer
- World Tick
- Inbox ingestion
- Cognitive Analysis

It is presentation and delivery only.
