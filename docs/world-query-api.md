# World Query API

The world query API is the first public read-only interface to the compiled Jungle. It exposes persisted runtime data without mutations, authentication, or AI.

## Command / Query Separation

| Layer | Responsibility |
|-------|----------------|
| Commands | Change the world (`WorldCommand`, handlers, persistence) |
| Queries | Observe the world (query services, REST controllers) |

Query services never reuse command handlers. Controllers never access persistence adapters directly.

```text
Controller → Query Service → Repository Port → Runtime record → DTO mapper → JSON
```

## Endpoints

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/world/status` | Runtime status, bootstrap version, counts, persistence provider |
| GET | `/api/characters` | Character summaries (optional `title`, `place` filters) |
| GET | `/api/characters/{id}` | Character details (no secrets) |
| GET | `/api/stories` | Story summaries |
| GET | `/api/stories/{id}` | Story details |
| GET | `/api/territories` | Territory summaries |
| GET | `/api/territories/{id}` | Territory details |

## Response Philosophy

- **Summaries** are lightweight lists for browsing (id, name, key fields).
- **Details** include profile sections, relationships, and location context.
- **Secrets are never exposed** — `secrets` sections are stripped from API responses.
- **DTOs only** — aggregates and runtime records are never returned directly from controllers.

## DTO Guidelines

| DTO | Purpose |
|-----|---------|
| `WorldStatusDto` | Bootstrap and runtime health |
| `CharacterSummaryDto` | List view |
| `CharacterDetailsDto` | Full character view |
| `StorySummaryDto` / `StoryDetailsDto` | Story list and detail |
| `TerritorySummaryDto` / `TerritoryDetailsDto` | Territory list and detail |
| `ApiErrorDto` | Standard error envelope |

Mappers in `adapters/api/mapper/` perform explicit field mapping with no reflection.

## OpenAPI / Swagger

Springdoc OpenAPI is enabled. In development:

- Swagger UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- OpenAPI JSON: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

## Error Handling

Unknown resources return `404` with a standard JSON body:

```json
{
  "timestamp": "2026-06-27T12:00:00Z",
  "status": 404,
  "error": "Not Found",
  "message": "Character not found: missing",
  "path": "/api/characters/missing"
}
```

## Packages

| Package | Role |
|---------|------|
| `application/query/` | Read-only query services |
| `adapters/api/controller/` | REST controllers |
| `adapters/api/dto/` | API response models |
| `adapters/api/mapper/` | Runtime → DTO mapping |

## Out of Scope (Prompt #11)

- Mutations
- Authentication
- Chat / AI
- Secret exposure
- Chronicle generation
