# Repository Ports

The repository port layer defines how the runtime world is persisted. It contains **only interfaces** — no implementations, no Spring Data, no JPA, no SQL.

This is the persistence contract for the entire application.

## Philosophy

Repositories belong to the domain boundary. They express **domain language**, not database concepts.

| Repositories own | Repositories do not expose |
|------------------|---------------------------|
| Aggregate roots | `EntityManager` |
| Domain operations | Generic CRUD |
| Intentful queries | Table/column names |
| Jungle vocabulary | JDBC/SQL types |

A `CharacterRepository` knows about `moveCharacter` and `changePreference`. It does not know about `INSERT` or `@Entity`.

## Aggregate Ownership

The world is composed of aggregates — not one monolithic `WorldRepository`.

Each aggregate has its own repository port:

| Aggregate | Repository |
|-----------|------------|
| Character | `CharacterRepository` |
| Territory | `TerritoryRepository` |
| Place | `PlaceRepository` |
| Story | `StoryRepository` |
| Relationship | `RelationshipRepository` |
| Organization | `OrganizationRepository` |
| Resource | `ResourceRepository` |
| Object | `ObjectRepository` |
| Timeline | `TimelineRepository` |
| Prompt Profile | `PromptProfileRepository` |
| Canon | `CanonRepository` |
| World Rules | `WorldRulesRepository` |
| Law | `LawRepository` |
| Custom | `CustomRepository` |
| Glossary | `GlossaryRepository` |

There is **no** `WorldRepository`. The world is the composition of these aggregates.

## Why CRUD Was Avoided

Generic CRUD repositories (`save`, `findAll`, `deleteById`) leak persistence mechanics into the domain:

- They encourage anemic models
- They hide business intent (`moveCharacter` vs `update`)
- They invite cross-aggregate coupling
- They mirror database tables, not jungle behavior

Instead, each repository exposes only operations that make sense for its aggregate:

```java
// CharacterRepository — domain operations
void moveCharacter(String characterId, String fromPlaceId, String toPlaceId);
void changePreference(String characterId, String preferenceKey, String preferenceValue);

// StoryRepository — narrative operations
void linkStory(String storyId, String linkedStoryId, String linkType);

// TimelineRepository — chronology operations
void append(RuntimeTimelineEntry entry);
Optional<RuntimeTimelineEntry> latest();
```

Not every repository shares the same method set. Contracts reflect aggregate behavior.

## Query Specifications

Lightweight query records express domain intent without exposing database filtering:

| Query | Purpose |
|-------|---------|
| `CharacterQuery` | Find characters at a place or by title |
| `StoryQuery` | Find stories by participant or link |
| `TimelineQuery` | Filter timeline by chronology or time range |
| `RelationshipQuery` | Find relationships by character or type |

```java
CharacterQuery.atPlace("jungle-clearing");
StoryQuery.involvingParticipant("hippu-king");
TimelineQuery.between(start, end);
```

## Transaction Boundary

`WorldUnitOfWork` represents a transaction boundary with no framework dependencies:

```java
void begin();
void commit();
void rollback();
```

Implementations (future prompt) will map this to JPA, JDBC, or in-memory semantics.

## Provider Pattern

`WorldRepositoryProvider` exposes all repository ports through one abstraction:

```java
CharacterRepository characters();
TerritoryRepository territories();
// ... all 15 aggregates
```

Handlers and services receive repositories through the provider rather than individual injection of 15 dependencies.

## World Persistence Service

`WorldPersistenceService` defines the contract for applying runtime world changes:

```java
WorldUnitOfWork beginUnitOfWork();
void persist(WorldRuntime runtime, WorldUnitOfWork unitOfWork);
```

No implementation exists yet. This will bridge command execution to repository persistence in a future prompt.

## Package Structure

```text
domain/world/ports/
  CharacterRepository.java
  TerritoryRepository.java
  ...
  WorldUnitOfWork.java
  WorldRepositoryProvider.java
  WorldPersistenceService.java
  query/
    CharacterQuery.java
    StoryQuery.java
    TimelineQuery.java
    RelationshipQuery.java
```

## Relationship to Other Layers

```text
WorldCommand (intention)
    ↓
WorldCommandHandler (in-memory runtime)
    ↓
WorldPersistenceService (future)
    ↓
Repository Ports (this layer)
    ↓
Repository Implementations (future)
```

Nothing persists data yet. This prompt defines contracts only.
