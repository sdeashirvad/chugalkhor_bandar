package com.chugalkhorbandar.bootstrap;

/**
 * Developer-only trace of {@code currentPlace} through the character lifecycle.
 */
public record CharacterLocationDiagnostic(
        String typedModel,
        String bootstrapCommand,
        String worldCommand,
        String runtimeAggregate,
        String repository,
        String queryDto,
        String contextResolver) {

    public static CharacterLocationDiagnostic trace(
            String typedModel,
            String bootstrapCommand,
            String worldCommand,
            String runtimeAggregate,
            String repository,
            String queryDto,
            String contextResolver) {
        return new CharacterLocationDiagnostic(
                typedModel, bootstrapCommand, worldCommand, runtimeAggregate, repository, queryDto, contextResolver);
    }

    public String format() {
        return """
                Bootstrap (typed model): %s
                Bootstrap Command:       %s
                World Command:           %s
                Runtime Aggregate:       %s
                Repository:              %s
                Query DTO:               %s
                Context Resolver:        %s
                """
                .formatted(
                        value(typedModel),
                        value(bootstrapCommand),
                        value(worldCommand),
                        value(runtimeAggregate),
                        value(repository),
                        value(queryDto),
                        value(contextResolver));
    }

    private static String value(String currentPlace) {
        return currentPlace == null ? "null" : currentPlace;
    }
}
