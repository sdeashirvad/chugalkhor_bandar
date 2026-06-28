# Persistence Layer

The persistence layer implements repository ports defined in `domain.world.ports`. Domain code never imports Spring Data, JPA, or SQL.

## Adapter Pattern

```text
Domain (WorldCommand, Runtime records, Repository Ports)
    ↓
Repository Implementations (adapters)
    ↓
Persistence Technology (in-memory maps / PostgreSQL + JPA)
```

| Layer | Package | Role |
|-------|---------|------|
| Ports | `domain.world.ports` | Contracts |
| In-memory | `adapters.persistence.memory` | Dev reference implementation |
| PostgreSQL | `adapters.persistence.postgres` | Production persistence |
| Entities | `adapters.persistence.postgres.entity` | JPA models (adapter-only) |
| Mappers | `adapters.persistence.postgres.mapper` | Explicit aggregate ↔ entity mapping |

Never the reverse: persistence does not drive domain design.

## Profile Selection

| Profile | World repositories | Schema validation |
|---------|-------------------|-------------------|
| `dev` | In-memory (`InMemoryWorldRepositoryProvider`) | H2 + Flyway + Hibernate `validate` |
| `postgres-dev` | PostgreSQL (`PostgresWorldRepositoryProvider`) | PostgreSQL + Flyway + Hibernate `validate` |

Handlers and the command executor remain persistence-agnostic.

## Aggregate Mapping

Runtime records map explicitly to persistence entities:

```text
RuntimeCharacter
    ↓ CharacterMapper.toEntity()
CharacterEntity
    ↓ JPA save
PostgreSQL
    ↓ CharacterMapper.toRuntime()
RuntimeCharacter
```

No reflection. No magic mapping libraries. JSON map fields use `JsonSerialization` for `TEXT` columns.

## Flyway Ownership

Schema is owned by Flyway, not Hibernate:

- Migration: `db/migration/V1__initial_schema.sql`
- Hibernate: `ddl-auto: validate` (never `create` or `update`)

On startup, Flyway creates tables; Hibernate verifies entity mappings match the schema.

## Environment Configuration

Database settings load from `.env` (via `DotEnvEnvironmentPostProcessor`) and environment variables:

```text
POSTGRES_HOST
POSTGRES_PORT
POSTGRES_DB
POSTGRES_USER
POSTGRES_PASSWORD
POSTGRES_SSLMODE=require
```

Optional `POSTGRES_URL` overrides the constructed JDBC URL. Supabase requires `sslmode=require`.

## World Persistence Service

`WorldPersistenceService` coordinates unit-of-work boundaries:

```text
beginUnitOfWork() → begin() → persist(WorldRuntime) → commit()
                                              ↓ failure
                                         rollback()
```

`WorldStatePersister` writes each collection from `WorldState` through the appropriate repository port.

## Bootstrap Pipeline

```text
Bootstrap → Compiler → World Commands → Executor → WorldPersistenceService → Repositories
```

Startup logs persistence provider, Flyway success, and persisted entity counts.

## Repository Contract Testing

The same `RepositoryContractTestBase` suite runs against:

- `InMemoryRepositoryContractTest` — pure Java, no Spring
- `PostgresRepositoryContractTest` — Testcontainers PostgreSQL

This ensures both implementations satisfy identical port contracts.

## Why Hibernate Validation Only

Hibernate validates that JPA entity annotations match the Flyway-managed schema. It does not create or alter tables. This keeps schema changes reviewable in SQL migrations and prevents drift between environments.
