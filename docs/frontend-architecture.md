# Frontend Architecture

The Chugalkhor Bandar frontend is a read-only **World Explorer** for validating the compiled runtime Jungle. It communicates only through REST — no bootstrap knowledge, no database access, no mutations.

## Stack

| Layer | Choice |
|-------|--------|
| UI | React 19 + TypeScript |
| Build | Vite |
| Routing | React Router |
| Data | TanStack Query + Axios |
| Styling | Tailwind CSS + shadcn-style primitives |
| Icons | Lucide |

## Navigation Philosophy

The explorer should feel like a **wiki**: every entity links to related entities.

```text
Character → Place → Territory → Story → Character
```

Principles:

- **Everything clickable** — rulers, places, participants, relationship targets
- **Breadcrumbs** on detail pages (`World > Characters > Rabbitu Minister`)
- **Global search** in the header across characters, stories, territories, places, organizations
- **No dead ends** — empty states explain missing data; errors are friendly

## Entity Linking

`EntityReference` is the shared link shape from the API:

```typescript
{ id, name, type: 'character' | 'place' | 'territory' | 'organization' | 'story' }
```

| Component | Role |
|-----------|------|
| `EntityLink` | Inline navigable reference |
| `EntityChipList` | Chip row for participants, places, ministers |
| `RelationshipCard` | Type → target character → status, click to navigate |

Route mapping lives in `lib/routes.ts` (`entityPath()`).

## Folder Layout

```text
frontend/src/
  app/           Application shell (QueryClient, Router)
  router/        Route definitions
  layout/        Header (search), sidebar, shell
  pages/         Route-level screens
  components/    Reusable UI
  api/           Typed Axios functions per resource
  hooks/         React Query hooks + useWorldSearch
  types/         API response types
  lib/           Utilities and route helpers
```

## Routing

| Path | Page |
|------|------|
| `/` | Redirect → `/world` |
| `/world` | World dashboard + statistics |
| `/characters`, `/characters/:id` | Character explorer |
| `/stories`, `/stories/:id` | Story explorer + timeline |
| `/territories`, `/territories/:id` | Territory explorer |
| `/places`, `/places/:id` | Place explorer |
| `/organizations`, `/organizations/:id` | Organization explorer |
| `*` | 404 |

## API Layer

- `api/client.ts` — Axios instance, error helper
- One module per resource (`world`, `characters`, `stories`, `territories`, `places`, `organizations`)
- Types mirror backend DTOs including `EntityReference`

### Environment

```text
VITE_API_BASE_URL=http://localhost:8080
```

Leave empty in dev to use the Vite proxy (`/api` → backend).

## Query Strategy

Network calls go through hooks; components never call Axios directly.

| Hook | Purpose |
|------|---------|
| `useWorldStatus()` | Dashboard aggregates |
| `useCharacters()` / `useCharacter(id)` | Characters |
| `useStories()` / `useStory(id)` | Stories |
| `useTerritories()` / `useTerritory(id)` | Territories |
| `usePlaces()` / `usePlace(id)` | Places |
| `useOrganizations()` / `useOrganization(id)` | Organizations |
| `useWorldSearch(query)` | Client-side global search (MVP) |

Defaults: 30s stale time, one retry, no refetch on window focus.

## Search Strategy (MVP)

Global search loads cached lists from React Query and filters client-side by name/subtitle. No dedicated search endpoint yet — acceptable for MVP0 world size.

## Component Philosophy

Pages compose primitives: `PageHeader`, `Breadcrumbs`, `StatCard`, `SectionCard`, `Skeleton`, `EmptyState`, `ErrorState`. Business logic stays in hooks and API modules.

## Security & Scope

- No secrets rendered (backend strips `secrets`)
- No authentication, chat, websockets, editing, or AI
- Light mode only

## Running

```bash
cd frontend
npm install
npm run dev
```

Backend on port 8080 (or adjust proxy / `VITE_API_BASE_URL`).

```bash
npm test
npm run build
```
