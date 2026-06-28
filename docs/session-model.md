# Session and Identity Model

Chugalkhor Bandar uses **world identity**, not enterprise authentication. A visitor chooses a Jungle character; the application remembers who they are for the duration of a browser session.

## Philosophy

The Jungle already knows its characters. Login is not about creating accounts or verifying external identity—it is about **choosing which character you are** and proving you know the family passkey for that character.

This is a family application. Passkeys are stored in persistence as plain text for MVP0. There is no JWT, no OAuth, and no role-based access control in this phase.

## Identity: CurrentCharacter

When a visitor logs in, the session carries a **CurrentCharacter** snapshot:

| Field | Description |
|-------|-------------|
| `id` | Stable character identifier from the world model |
| `displayName` | Primary name shown in the UI |
| `titles` | All known titles/aliases for the character |
| `species` | Species label (e.g. Hippu, Rabbitu) |
| `homeTerritory` | Home territory name, if any |

`CurrentCharacter` is identity only. It does not include runtime world state, chat history, or mutable game state.

## Session: ChatSession

A **ChatSession** represents one active browser visit:

| Field | Description |
|-------|-------------|
| `sessionId` | Opaque identifier (UUID) |
| `currentCharacter` | The logged-in character summary |
| `startedAt` | When the session was created (login) |
| `lastActivity` | Updated on each authenticated request |
| `status` | `ACTIVE` or `EXPIRED` |

Messages and chat are out of scope for MVP0; the session model is named for future chat integration.

## Authentication Flow

1. User submits **Animal Name** and **Passkey** on the login screen.
2. Backend resolves the character by title (case-insensitive) and compares the passkey to stored credentials.
3. On success, an in-memory session is created and returned.
4. The client stores `sessionId` in `sessionStorage` and sends it on every API request via the `X-Chugalkhor-Session` header (and optionally an HttpOnly cookie from the server).

### Default credentials (development)

After bootstrap, all characters are seeded with the default passkey `jungle` unless overridden in configuration (`chugalkhor.session.passkeys`).

Login with a character title such as **Hippu King** and passkey **jungle**.

## Session Store

Sessions live **in memory only** on the backend:

- One active session per browser (new login replaces the previous session id in the client).
- No distributed session replication.
- **30 minutes of inactivity** marks a session expired; subsequent requests receive `401 Unauthorized`.

The client clears stored session id on 401 (except failed login attempts) and redirects protected routes to `/login`.

## API Endpoints

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/api/session/login` | Body: `{ animalName, passkey }` → session + character |
| `POST` | `/api/session/logout` | Ends session; requires session id |
| `GET` | `/api/session/current` | Returns current session or `401` |

## Frontend Behavior

- **`/login`** — public landing; Animal Name, Passkey, Login.
- **Protected routes** (`/world`, explorer pages) — require a valid session; otherwise redirect to `/login`.
- **Header** — shows current character name, species, and Logout after login.
- **Axios** — `withCredentials: true` and automatic `X-Chugalkhor-Session` header from `sessionStorage`.

## Future: JWT Migration

MVP0 intentionally avoids JWT to keep the foundation simple. A later phase can:

1. Issue signed tokens at login instead of (or in addition to) opaque session ids.
2. Move session storage to Redis or a database for horizontal scaling.
3. Hash passkeys and support per-user credential rotation.
4. Add refresh tokens and explicit token expiry separate from inactivity timeout.

The **CurrentCharacter** and **ChatSession** shapes should remain stable; only transport and storage mechanisms would change.
