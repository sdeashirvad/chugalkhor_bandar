# World Runtime

The world runtime is the first execution layer of Chugalkhor Bandar. It applies `WorldCommand` intentions to an in-memory `WorldState` — proving the command pipeline without persistence.

## Execution Pipeline

```text
BootstrapCompilation
    ↓  BootstrapToWorldCommandMapper
List<WorldCommand>
    ↓  WorldCommandExecutor
WorldRuntime (WorldState + WorldExecutionReport)
```

At startup, `BootstrapRuntimeInitializer` runs this pipeline and logs runtime statistics.

| Stage | Package | Role |
|-------|---------|------|
| Commands | `domain.world.commands` | Immutable intentions |
| Handlers | `domain.world.runtime` | Pure state transitions |
| Executor | `domain.world.runtime` | Ordered dispatch |
| Runtime | `domain.world.runtime` | Final in-memory world |

## Handler Philosophy

Every world mutation happens through a `WorldCommandHandler`:

```java
interface WorldCommandHandler<T extends WorldCommand> {
    boolean supports(WorldCommand command);
    WorldState handle(WorldState current, T command);
}
```

Handlers are **pure**:

- No mutation of the input `WorldState`
- Always return a new `WorldState`
- No repositories, databases, or Spring dependencies
- No business-rule validation (structural checks only)

Each of the 23 world command types has a dedicated handler registered in `WorldCommandHandlerRegistry`.

## Immutable World State

`WorldState` is an immutable aggregate containing maps for:

- Characters, Territories, Places, Organizations
- Resources, Objects, Relationships, Stories
- Timeline, Prompt Profiles, World Rules, Canon
- Glossary, Laws, Customs

Runtime records (`RuntimeCharacter`, `RuntimeTerritory`, etc.) are lightweight immutable snapshots — not JPA entities. They hold only the fields needed to prove execution flow.

Updates use `add*`, `update*`, and `remove*` methods that return new `WorldState` instances.

## WorldCommandExecutor

The executor:

1. Accepts an ordered list of commands
2. Dispatches each to the correct handler via the registry
3. Builds the final `WorldState`
4. Produces a `WorldExecutionReport`

The executor does not know command internals — it only routes.

### Structural validation

The executor fails if:

| Condition | Result |
|-----------|--------|
| Null command list | `WorldExecutionException` |
| Null command in list | `WorldExecutionException` |
| No handler for command type | `WorldExecutionException` |
| Duplicate runtime id on create | `WorldExecutionException` |
| Handler returns null state | `WorldExecutionException` |

## WorldExecutionReport

Immutable record containing:

- `executedCommandCount`
- `durationMillis`
- `failures` and `warnings` lists
- `statistics` — collection sizes from final state
- `success` flag

## Bootstrap Execution

`BootstrapRuntimeInitializer` (startup Order 6):

```text
BootstrapCompilationHolder
    → BootstrapToWorldCommandMapper
    → WorldCommandExecutor
    → WorldRuntime
    → startup log
```

Example log output:

```text
Bootstrap Compilation .... OK

World Commands .......... 31

Executing Commands ....... OK

Characters ............... 13
Stories .................. 3
Relationships ............ 1
Timeline Entries ......... 1

Runtime World ............ READY
```

No global holder stores the runtime — the initializer logs results and returns `WorldRuntime` for downstream stages.

## Independence from Persistence

The runtime layer:

- Does **not** use JPA or Hibernate
- Does **not** access PostgreSQL or H2 for world data
- Does **not** implement repositories or REST APIs
- Does **not** emit domain events

Persistence of the runtime world begins in a future prompt.

## Testing

All runtime tests are pure unit tests without Spring:

- Handler registry resolution
- Handler dispatch per command type
- Full bootstrap pipeline execution
- Immutable state updates
- Command ordering
- Duplicate id detection
- Execution report statistics
