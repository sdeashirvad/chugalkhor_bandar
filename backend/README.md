# Chugalkhor Bandar — Backend

Engineering foundation for the Chugalkhor Bandar backend service.

## Prerequisites

- **Java 21** (JDK)
- **Maven 3.9+**
- **PostgreSQL** (only when using the `postgres-dev` profile)

The repository root must contain a `bootstrap/` directory. The application validates this at startup.

## Profiles

Two Spring profiles are supported:

| Profile | Purpose | Persistence |
|---------|---------|-------------|
| `dev` | Fast local development (default) | H2 in-memory |
| `postgres-dev` | Local development against PostgreSQL | PostgreSQL |

### dev

Uses an H2 in-memory database. No PostgreSQL installation required.

```bash
mvn spring-boot:run
```

Or explicitly:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### postgres-dev

Connects to PostgreSQL using environment variables or `application-postgres-dev.yml`.

Required configuration:

| Property | Environment variable |
|----------|---------------------|
| `spring.datasource.url` | `POSTGRES_URL` |
| `spring.datasource.username` | `POSTGRES_USER` |
| `spring.datasource.password` | `POSTGRES_PASSWORD` |

If any value is missing, the application fails fast with a clear error. It does **not** fall back to H2.

Use **`dev,postgres-dev`** together — PostgreSQL adapters activate on `postgres-dev`; dev settings (session, director, etc.) stay on `dev`.

**Bash / macOS / Linux:**

```bash
export POSTGRES_HOST=localhost
export POSTGRES_DB=chugalkhor_bandar
export POSTGRES_USER=postgres
export POSTGRES_PASSWORD=postgres
mvn spring-boot:run -Dspring-boot.run.profiles=dev,postgres-dev
```

Or with a full JDBC URL:

```bash
export POSTGRES_URL=jdbc:postgresql://localhost:5432/chugalkhor_bandar
export POSTGRES_USER=postgres
export POSTGRES_PASSWORD=postgres
mvn spring-boot:run -Dspring-boot.run.profiles=dev,postgres-dev
```

**PowerShell (Windows):**

PowerShell treats `-D` as its own switch. Quote the JVM property or set the profile via environment variable:

```powershell
$env:SPRING_PROFILES_ACTIVE = "dev,postgres-dev"
$env:POSTGRES_HOST = "localhost"
$env:POSTGRES_DB = "chugalkhor_bandar"
$env:POSTGRES_USER = "postgres"
$env:POSTGRES_PASSWORD = "postgres"
mvn spring-boot:run
```

Copy [`.env.example`](.env.example) to `.env` in the `backend` folder and fill in `POSTGRES_HOST`, `POSTGRES_DB`, `POSTGRES_USER`, and `POSTGRES_PASSWORD`. The app loads `backend/.env` automatically on startup.

Use **both** profiles together (`dev,postgres-dev`). The `dev` profile alone uses in-memory H2; `postgres-dev` switches persistence to PostgreSQL.

Or with a quoted `-D` flag (`.env` values are picked up automatically):

```powershell
mvn spring-boot:run "-Dspring-boot.run.profiles=dev,postgres-dev"
```

For a full JDBC URL override, set `POSTGRES_URL` in `.env` instead of individual `POSTGRES_*` vars.

### Supabase (IPv6 direct host)

Supabase **direct** database URLs (`db.<project-ref>.supabase.co`) resolve to **IPv6 only**. Many home and office networks (and Java on Windows) cannot reach them, which shows up as:

```
java.net.UnknownHostException: db.<project-ref>.supabase.co
```

Your `.env` is loaded correctly when you see the real hostname in the error (not `${POSTGRES_HOST}`).

**Fix:** In the Supabase dashboard go to **Project → Connect → Session pooler** (port 5432) and copy the pooler host. Update `backend/.env`:

```env
POSTGRES_HOST=aws-0-<region>.pooler.supabase.com
POSTGRES_PORT=5432
POSTGRES_DB=postgres
POSTGRES_USER=postgres.<project-ref>
POSTGRES_PASSWORD=<your-db-password>
POSTGRES_SSLMODE=require
```

Use the pooler username format `postgres.<project-ref>`, not plain `postgres`. Then run:

```powershell
mvn spring-boot:run "-Dspring-boot.run.profiles=dev,postgres-dev"
```

## How to Run

```bash
cd backend
mvn clean package
mvn spring-boot:run
```

Run tests:

```bash
mvn test
```

## Docker

Build from the **repository root** (bootstrap must be included):

```bash
docker build -f backend/Dockerfile .
```

Run with PostgreSQL (defaults to `dev,postgres-dev` profiles):

```bash
docker run --rm -p 8080:8080 \
  -e POSTGRES_HOST=host.docker.internal \
  -e POSTGRES_DB=chugalkhor_bandar \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -e LLM_PROVIDER=mock \
  chugalkhor-bandar-backend
```

Override profiles with `SPRING_PROFILES_ACTIVE=dev,postgres-dev`. Bootstrap and chronicles paths inside the container default to `/app/bootstrap` and `/app/chronicles`.

## Project Structure

```
backend/
├── pom.xml
└── src/
    ├── main/
    │   ├── java/com/chugalkhorbandar/
    │   │   ├── config/           # Spring configuration, properties, startup logging
    │   │   ├── shared/           # Shared utilities
    │   │   ├── domain/           # Domain entities (future)
    │   │   ├── application/      # Use cases and application services (future)
    │   │   ├── ports/            # Port interfaces (hexagonal boundary)
    │   │   ├── adapters/
    │   │   │   ├── persistence/
    │   │   │   │   ├── memory/   # H2 adapters (dev profile)
    │   │   │   │   └── postgres/ # PostgreSQL adapters (postgres-dev profile)
    │   │   │   └── api/          # REST controllers (future)
    │   │   ├── bootstrap/        # Bootstrap folder validation
    │   │   └── chronicle/        # Chronicle folder initialization
    │   └── resources/
    │       ├── application.yml
    │       ├── application-dev.yml
    │       ├── application-postgres-dev.yml
    │       └── db/migration/     # Flyway migrations (empty for now)
    └── test/
```

## Architecture Philosophy

This backend follows **hexagonal architecture** (ports and adapters):

- **Domain** holds business logic and entities — no framework dependencies.
- **Application** orchestrates use cases through port interfaces.
- **Ports** define boundaries (`PersistenceProvider`, `RepositoryHealthPort`, etc.).
- **Adapters** implement ports for specific technologies (H2, PostgreSQL, REST).
- **Config** wires Spring beans, profiles, and startup behavior.

Persistence is selected by Spring profile. Infrastructure abstractions prove the architecture without coupling to business concepts like characters or stories.

At startup the application:

1. Validates the bootstrap folder exists (`../bootstrap` by default).
2. Scans and validates bootstrap canon (manifest, frontmatter, IDs, status).
3. Creates the chronicle folder if missing (`../chronicles` by default).
4. Logs the active profile, persistence provider, and folder status.

Bootstrap validation is independent of persistence. See [docs/bootstrap-format.md](../docs/bootstrap-format.md) for the canon contract and [docs/bootstrap-document-engine.md](../docs/bootstrap-document-engine.md) for the markdown document engine.

After successful validation, `BootstrapContextHolder` exposes the scanned world and manifest. Parsed documents are available via `BootstrapDocumentRepository`. Typed specs are available via `BootstrapTypedWorldHolder`. Compiled commands are available via `BootstrapCompilationHolder`. The runtime world is built and persisted at startup by `BootstrapRuntimeInitializer`. Read-only REST endpoints are available under `/api`. See [docs/bootstrap-typed-readers.md](../docs/bootstrap-typed-readers.md), [docs/bootstrap-compiler.md](../docs/bootstrap-compiler.md), [docs/world-command-model.md](../docs/world-command-model.md), [docs/world-runtime.md](../docs/world-runtime.md), [docs/repository-ports.md](../docs/repository-ports.md), [docs/persistence-layer.md](../docs/persistence-layer.md), and [docs/world-query-api.md](../docs/world-query-api.md).

## Configuration

Custom properties under `chugalkhor`:

| Property | Default | Description |
|----------|---------|-------------|
| `chugalkhor.name` | `Chugalkhor Bandar` | Application display name |
| `chugalkhor.bootstrap-folder` | `../bootstrap` | Path to bootstrap content |
| `chugalkhor.chronicle-folder` | `../chronicles` | Path to chronicle output |
