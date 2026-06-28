# Chugalkhor Bandar Explorer

Read-only React frontend for exploring the runtime Jungle via the world query API.

## Quick start

```bash
npm install
npm run dev
```

Ensure the backend is running on `http://localhost:8080`. Development uses the Vite proxy (see `.env.development`).

## Scripts

| Command | Description |
|---------|---------------|
| `npm run dev` | Start dev server |
| `npm run build` | Production build |
| `npm test` | Run Vitest |
| `npm run preview` | Preview production build |

## Configuration

Copy `.env.example` to `.env.local` and set `VITE_API_BASE_URL` if not using the dev proxy.

See [docs/frontend-architecture.md](../docs/frontend-architecture.md) for routing, API layer, and component design.
